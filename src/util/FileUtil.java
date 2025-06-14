package util;

import java.io.*;
import java.util.*;

public class FileUtil {

    // Writes content to a file (append mode)
    public static void writeToFile(String filename, String content, boolean append) {
        try {
            File file = new File(filename);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, append)))) {
                out.println(content);
            }
        } catch (IOException e) {
            System.out.println("Error writing to file '" + filename + "': " + e.getMessage());
        }
    }

    // Reads all lines from a file
    public static List<String> readFromFile(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return lines;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading from file '" + filename + "': " + e.getMessage());
        }

        return lines;
    }

    // Overwrites a file with new contents
    public static void overwriteFile(String filename, List<String> contents) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (String line : contents) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error writing to file '" + filename + "': " + e.getMessage());
        }
    }
}
