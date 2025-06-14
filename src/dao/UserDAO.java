package dao;

import model.User;
import util.FileUtil;
import util.SecurityUtil;

import java.util.*;

public class UserDAO {
    private static final String FILE = "resources/users.txt";

    // Registers a new user if username doesn't exist
    public boolean registerUser(String username, String password) {
        username = username.trim();
        if (isUserExists(username)) return false;

        String hashedPassword = SecurityUtil.hashPassword(password);
        User user = new User(username, hashedPassword);
        FileUtil.writeToFile(FILE, user.toString(), true);
        return true;
    }

    // Authenticates user based on username and password
    public boolean loginUser(String username, String password) {
        username = username.trim();
        List<String> users = FileUtil.readFromFile(FILE);
        String hashedInput = SecurityUtil.hashPassword(password);
        for (String line : users) {
            User user = User.fromString(line);
            if (user != null &&
                user.getUsername().equals(username) &&
                user.getHashedPassword().equals(hashedInput)) {
                return true; 
            }
        }
        return false;
    }

    // Checks if the user already exists
    public boolean isUserExists(String username) {
        username = username.trim();
        List<String> users = FileUtil.readFromFile(FILE);
        for (String line : users) {
            User user = User.fromString(line);
            if (user != null && user.getUsername().equals(username)) {
                return true; 
            }
        }
        return false;
    }
}
