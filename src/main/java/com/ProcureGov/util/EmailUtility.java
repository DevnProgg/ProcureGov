package com.ProcureGov.util;

import com.ProcureGov.model.SupplierData;
import javax.mail.*;
import javax.mail.internet.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailUtility {

    private static final Logger logger = Logger.getLogger(EmailUtility.class.getName());

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String fromAddress;
    private final String fromName;
    private final boolean useAuth;
    private final boolean useTLS;

    public EmailUtility() {
        this.host = resolveString("EMAIL_HOST", "smtp.gmail.com");
        this.port = resolvePort();
        this.username = resolveString("EMAIL_USERNAME", null);
        this.password = resolveString("EMAIL_PASSWORD", null);
        this.fromAddress = firstNonBlank(resolveString("EMAIL_FROM_ADDRESS", null), this.username);
        this.fromName = resolveString("EMAIL_FROM_NAME", "ProcureGov System");
        this.useAuth = resolveEmailAuth();
        this.useTLS = resolveEmailTls();
    }

    public boolean isConfigured() {
        boolean authReady = !useAuth || (isNotBlank(username) && isNotBlank(password));
        return isNotBlank(host) && port > 0 && isNotBlank(fromAddress) && authReady;
    }

    /**
     * Send an email notification with HTML content
     */
    public boolean sendEmail(String to, String subject, String htmlBody) {
        if (!isConfigured()) {
            logger.warning("Email service not configured. Cannot send email to: " + to +
                    ". Set EMAIL_HOST, EMAIL_PORT, EMAIL_FROM_ADDRESS and, if auth is enabled, EMAIL_USERNAME/EMAIL_PASSWORD.");
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", this.host);
        props.put("mail.smtp.port", this.port);
        props.put("mail.smtp.auth", String.valueOf(this.useAuth));

        if (useTLS) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        Session session = useAuth
                ? Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                })
                : Session.getInstance(props);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress, fromName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");

            Transport.send(message);
            logger.info("Email sent successfully to: " + to);
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send email to " + to, e);

            if (looksLikeGmailAppPasswordIssue(e)) {
                logger.severe("Gmail rejected the SMTP login. If this account uses 2-Step Verification, set EMAIL_PASSWORD to a Google App Password instead of the normal account password.");
            }
            return false;
        }
    }

    private String resolveString(String key, String defaultValue) {
        String value = trimToNull(System.getProperty(key));
        if (value == null) {
            value = trimToNull(System.getenv(key));
        }
        if (value == null) {
            value = trimToNull(lookupJndiString(key));
        }
        return value != null ? value : defaultValue;
    }

    private int resolvePort() {
        String value = resolveString("EMAIL_PORT", null);
        if (value == null) {
            return 587;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid integer for EMAIL_PORT: " + value + ". Using default 587.");
            return 587;
        }
    }

    private boolean resolveEmailAuth() {
        String value = resolveString("EMAIL_AUTH", null);
        return value == null || Boolean.parseBoolean(value);
    }

    private boolean resolveEmailTls() {
        String value = resolveString("EMAIL_TLS", null);
        return value == null || Boolean.parseBoolean(value);
    }

    private String lookupJndiString(String key) {
        try {
            Object value = new InitialContext().lookup("java:comp/env/" + key);
            return value != null ? value.toString() : null;
        } catch (NamingException e) {
            return null;
        }
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean isNotBlank(String value) {
        return trimToNull(value) != null;
    }

    private static String firstNonBlank(String first, String second) {
        return isNotBlank(first) ? first : second;
    }

    private static boolean looksLikeGmailAppPasswordIssue(Throwable t) {
        String message = t.getMessage();
        if (message == null) {
            return false;
        }
        return message.contains("534 5.7.9") || message.contains("Application-specific password required") || message.contains("InvalidSecondFactor");
    }

    /**
     * Generate HTML email body for award notification with ProcureGov styling
     */
    public String generateAwardEmailBody(SupplierData supplier, String tenderTitle,
                                         String awardDate, double awardAmount, String awardRef) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    /* Using your project's design tokens */
                    :root {
                        --color-primary: #003f87;
                        --color-secondary: #006e25;
                        --color-tertiary: #722b00;
                        --color-surface: #f8f9fa;
                        --color-surface-container-lowest: #ffffff;
                        --color-surface-container-high: #e7e8e9;
                        --color-outline: #727784;
                        --radius-card: 0.75rem;
                        --radius-lg: 0.5rem;
                        --font-headline: 'Newsreader', Georgia, serif;
                        --font-body: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
                    }

                    body {
                        margin: 0;
                        padding: 0;
                        font-family: var(--font-body);
                        line-height: 1.6;
                        color: #191c1d;
                        background: #f8f9fa;
                    }

                    .email-wrapper {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 32px 20px;
                    }

                    .email-card {
                        background: var(--color-surface-container-lowest);
                        border-radius: var(--radius-card);
                        box-shadow: 0 4px 16px rgba(25, 28, 29, 0.04);
                        overflow: hidden;
                    }
                    
                    .email-header {
                        background: linear-gradient(135deg, #003f87, #0056b3);
                        padding: 32px 32px 24px;
                        text-align: center;
                    }
                    
                    .email-badge {
                        display: inline-block;
                        font-size: 10px;
                        font-weight: 700;
                        letter-spacing: 0.12em;
                        text-transform: uppercase;
                        color: rgba(255, 255, 255, 0.8);
                        background: rgba(255, 255, 255, 0.1);
                        padding: 4px 12px;
                        border-radius: 20px;
                        margin-bottom: 16px;
                        backdrop-filter: blur(10px);
                    }
                    
                    .email-header h1 {
                        font-family: var(--font-headline);
                        font-size: 28px;
                        font-weight: 700;
                        color: white;
                        margin: 0 0 8px;
                        line-height: 1.2;
                        letter-spacing: -0.01em;
                    }
                    
                    .email-header-subtitle {
                        font-size: 14px;
                        color: rgba(255, 255, 255, 0.9);
                        margin: 0;
                    }
                    
                    .email-body {
                        padding: 32px;
                    }
                    
                    .supplier-greeting {
                        font-family: var(--font-headline);
                        font-size: 20px;
                        font-weight: 600;
                        color: #003f87;
                        margin: 0 0 16px;
                    }
                    
                    .congratulations-text {
                        font-size: 16px;
                        color: #006e25;
                        font-weight: 600;
                        margin-bottom: 24px;
                        padding: 12px 16px;
                        background: #e8f5e9;
                        border-left: 3px solid #006e25;
                        border-radius: 0 var(--radius-lg) var(--radius-lg) 0;
                    }
                    
                    .award-details {
                        background: #f8f9fa;
                        border-radius: var(--radius-lg);
                        padding: 20px;
                        margin: 24px 0;
                    }
                    
                    .detail-row {
                        display: flex;
                        padding: 12px 0;
                        border-bottom: 1px solid #e7e8e9;
                    }
                    
                    .detail-row:last-child {
                        border-bottom: none;
                    }
                    
                    .detail-label {
                        flex: 0 0 120px;
                        font-size: 11px;
                        font-weight: 700;
                        letter-spacing: 0.08em;
                        text-transform: uppercase;
                        color: #727784;
                    }
                    
                    .detail-value {
                        flex: 1;
                        font-size: 15px;
                        font-weight: 500;
                        color: #191c1d;
                    }
                    
                    .detail-value.highlight {
                        font-family: var(--font-headline);
                        font-size: 20px;
                        font-weight: 700;
                        color: #003f87;
                    }
                    
                    .action-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #003f87, #0056b3);
                        color: white;
                        text-decoration: none;
                        padding: 12px 28px;
                        border-radius: 30px;
                        font-size: 14px;
                        font-weight: 600;
                        margin: 20px 0 16px;
                        box-shadow: 0 4px 16px rgba(0, 63, 135, 0.2);
                    }
                    
                    .email-footer {
                        background: #f8f9fa;
                        padding: 24px 32px;
                        text-align: center;
                        border-top: 1px solid #e7e8e9;
                    }
                    
                    .footer-text {
                        font-size: 11px;
                        text-transform: uppercase;
                        letter-spacing: 0.08em;
                        color: #727784;
                        margin: 0 0 8px;
                    }
                    
                    .footer-links {
                        font-size: 11px;
                        color: #727784;
                    }
                    
                    .footer-links a {
                        color: #003f87;
                        text-decoration: none;
                        margin: 0 8px;
                    }
                    
                    .supplier-info {
                        font-size: 13px;
                        color: #191c1d;
                        margin-top: 16px;
                        padding-top: 16px;
                        border-top: 1px solid #e7e8e9;
                    }
                </style>
            </head>
            <body>
                <div class="email-wrapper">
                    <div class="email-card">
                        <div class="email-header">
                            <span class="email-badge">Contract Award Notice</span>
                            <h1>Congratulations!</h1>
                            <p class="email-header-subtitle">Your bid has been selected for award</p>
                        </div>
                        
                        <div class="email-body">
                            <h2 class="supplier-greeting">Dear %s,</h2>
                            
                            <div class="congratulations-text">
                                ✦ You have been awarded the contract for this procurement
                            </div>
                            
                            <p style="margin: 0 0 8px; color: #727784; font-size: 13px;">
                                We are pleased to inform you that %s has been selected as the successful bidder.
                            </p>
                            
                            <div class="award-details">
                                <div class="detail-row">
                                    <span class="detail-label">Award Reference</span>
                                    <span class="detail-value">%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Tender</span>
                                    <span class="detail-value">%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Award Date</span>
                                    <span class="detail-value">%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Contract Value</span>
                                    <span class="detail-value highlight">$%s</span>
                                </div>
                            </div>
                            
                            <div style="text-align: center;">
                                <a href="#" class="action-button">View Contract Details →</a>
                            </div>
                            
                            <p style="font-size: 13px; color: #727784; margin: 24px 0 0;">
                                Please log in to the ProcureGov portal to access the complete contract 
                                documentation and proceed with the next steps.
                            </p>
                            
                            <div class="supplier-info">
                                <strong>%s</strong><br>
                                %s<br>
                                %s • %s
                            </div>
                        </div>
                        
                        <div class="email-footer">
                            <p class="footer-text">ProcureGov — Government Procurement System</p>
                            <div class="footer-links">
                                <a href="#">Portal</a> • 
                                <a href="#">Support</a> • 
                                <a href="#">Contact</a>
                            </div>
                            <p style="font-size: 10px; color: #727784; margin-top: 16px;">
                                This is an automated notification. Please do not reply to this email.<br>
                                © 2024 ProcureGov. All rights reserved.
                            </p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
                supplier.getBusiness_name(),
                supplier.getBusiness_name(),
                awardRef,
                tenderTitle,
                awardDate,
                String.format("%,.2f", awardAmount),
                supplier.getBusiness_name(),
                supplier.getAddress() != null ? supplier.getAddress() : "Address not provided",
                supplier.getPhone_number() != null ? supplier.getPhone_number() : "Phone not provided",
                supplier.getReg_number() != null ? supplier.getReg_number() : ""
        );
    }
}