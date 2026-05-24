package com.example.irp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // --- Purana method (waise hi rahega) ---
    public void sendStatusEmail(String toEmail, String userName, String resourceName, String status) {
        // ... (aapka pehle wala code yahan rahega) ...
    }

    // --- Naya updated method (Ise copy karein) ---
    public void sendConfirmationEmail(String toEmail, String userName, String resourceName, int id) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Action Required: Please Confirm Receipt of " + resourceName);

            String baseUrl = "https://institute-project-production.up.railway.app/user/verify/";
//            // EmailService.java mein change karein
//// Purana: String baseUrl = "http://localhost:8080/user/verify/";
//// Naya (Example):
//            String baseUrl = "https://your-ngrok-url.ngrok-free.app/user/verify/";

            // Buttons ko stylize kiya hai taaki click karne mein aasani ho
            String htmlContent = "<div style='font-family: sans-serif;'>"
                    + "<h3>Hello " + userName + ",</h3>"
                    + "<p>Admin ne aapka <b>" + resourceName + "</b> ke liye request approve kar diya hai. "
                    + "Kripya confirm karein ki aapko resource mil gaya hai ya nahi:</p>"
                    + "<a href='" + baseUrl + "yes/" + id + "' style='padding:10px 20px; background:#22c55e; color:white; text-decoration:none; border-radius:5px;'>Yes, I received it</a> &nbsp;"
                    + "<a href='" + baseUrl + "no/" + id + "' style='padding:10px 20px; background:#ef4444; color:white; text-decoration:none; border-radius:5px;'>No, not received</a>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Confirmation email sent successfully to: " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send confirmation email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
