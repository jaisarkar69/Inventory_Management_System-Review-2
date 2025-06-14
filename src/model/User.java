package model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String hashedPassword;

    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    @Override
    public String toString() {
        return username + "," + hashedPassword;
    }

    public static User fromString(String data) {
        try {
            String[] parts = data.split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid user format: " + data);
            }

            String username = parts[0].trim();
            String hashedPassword = parts[1].trim();

            return new User(username, hashedPassword);
        } catch (Exception e) {
            System.out.println("⚠️ Failed to parse user line: '" + data + "'. Error: " + e.getMessage());
            return null;
        }
    }

}
