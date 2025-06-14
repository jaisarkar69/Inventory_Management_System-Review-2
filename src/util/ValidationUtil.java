package util;

public class ValidationUtil {

    public static boolean isStrongPassword(String password) {
        // At least 6 characters, includes uppercase, lowercase, number, and special character
        if (password.length() < 6) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUpper = true;
            else if (Character.isLowerCase(ch)) hasLower = true;
            else if (Character.isDigit(ch)) hasDigit = true;
            else hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
