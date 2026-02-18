package com.votingsystem.Voting.System.service;

import com.votingsystem.Voting.System.entity.OtpEntity;
import com.votingsystem.Voting.System.reposetory.OtpRepo;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
public class EmailOtpService {
    private final JavaMailSender javaMailSender;
    private final OtpRepo otpRepo;

    @Async
    public void sendEmail(String toemail, String otp, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("springbootjava199@gmail.com");
        message.setTo(toemail);
        message.setSubject("Voter Verification - Voting System");
        message.setText("Dear " + name.toUpperCase() + ",\n\nYour OTP is: " + otp + "\n\nDo not share this with anyone.");
        javaMailSender.send(message);
    }

    @Transactional
    public void otpGenerator(String email, String name) {
        // Step 1: Purana koi bhi OTP ho is email ka, use delete karo (Fixes Retry issue)
        otpRepo.findByEmail(email.trim()).ifPresent(otpRepo::delete);

        // Step 2: Naya OTP generate karo
        String otp = String.format("%04d", ThreadLocalRandom.current().nextInt(1000, 10000));

        OtpEntity otpObj = new OtpEntity();
        otpObj.setEmail(email.trim());
        otpObj.setOtp(otp.trim());

        otpRepo.save(otpObj);
        sendEmail(email.trim(), otp, name);
    }

    @Transactional
    public boolean verifyOtp(String email, String otp) {
        // Email aur OTP dono ko trim karke check karein
        Optional<OtpEntity> otpInDb = otpRepo.findByEmail(email.trim());

        if (otpInDb.isPresent() && otpInDb.get().getOtp().equals(otp.trim())) {
            // Sahi OTP milte hi use delete kar do taaki session fresh rahe
            otpRepo.delete(otpInDb.get());
            return true;
        }
        // Agar galat OTP hai, toh delete mat karo, user ko dubara try karne do
        return false;
    }
}