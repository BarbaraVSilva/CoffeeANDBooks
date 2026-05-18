package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class SecurityUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    /**
     * Hashes a password using the SHA-256 cryptographic algorithm.
     * Returns a 64-character hexadecimal representation of the hash.
     */
    public static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Mathematically validates a Brazilian CPF (Cadastro de Pessoas Físicas).
     * Strips non-digits, ensures length of 11, excludes repetitive patterns,
     * and validates both verification digits.
     */
    public static boolean isValidCpf(String cpf) {
        if (cpf == null) return false;
        
        // Remove all non-digits
        String cleanCpf = cpf.replaceAll("\\D", "");
        
        if (cleanCpf.length() != 11) return false;

        // Exclude common repetitive CPFs that pass the math test but are invalid
        if (cleanCpf.matches("(\\d)\\1{10}")) return false;

        try {
            // Calculate 1st verification digit
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cleanCpf.charAt(i) - '0') * (10 - i);
            }
            int r1 = (sum * 10) % 11;
            if (r1 == 10 || r1 == 11) r1 = 0;
            if (r1 != (cleanCpf.charAt(9) - '0')) return false;

            // Calculate 2nd verification digit
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cleanCpf.charAt(i) - '0') * (11 - i);
            }
            int r2 = (sum * 10) % 11;
            if (r2 == 10 || r2 == 11) r2 = 0;
            return r2 == (cleanCpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates if a string matches a standard email format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates if a Brazilian phone number contains the correct number of digits.
     * Acceptable: 10 digits (fixed line: DDD + 8 digits) or 11 digits (mobile: DDD + 9 digits).
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String cleanPhone = phone.replaceAll("\\D", "");
        return cleanPhone.length() == 10 || cleanPhone.length() == 11;
    }

    /**
     * Normalizes a CPF to the format 000.000.000-00.
     */
    public static String formatCpf(String cpf) {
        if (cpf == null) return null;
        String clean = cpf.replaceAll("\\D", "");
        if (clean.length() != 11) return cpf; // Return original if not 11 digits
        return String.format("%s.%s.%s-%s", 
            clean.substring(0, 3),
            clean.substring(3, 6),
            clean.substring(6, 9),
            clean.substring(9, 11)
        );
    }

    /**
     * Normalizes a Brazilian phone number to the standard format (XX) XXXXX-XXXX.
     */
    public static String formatPhone(String phone) {
        if (phone == null) return null;
        String clean = phone.replaceAll("\\D", "");
        if (clean.length() == 10) {
            return String.format("(%s) %s-%s", 
                clean.substring(0, 2),
                clean.substring(2, 6),
                clean.substring(6, 10)
            );
        } else if (clean.length() == 11) {
            return String.format("(%s) %s-%s", 
                clean.substring(0, 2),
                clean.substring(2, 7),
                clean.substring(7, 11)
            );
        }
        return phone;
    }
}
