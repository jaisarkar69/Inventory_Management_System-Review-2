package ui;

import dao.UserDAO;
import dao.ItemDAO;
import model.Item;
import util.ValidationUtil;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDAO userDAO = new UserDAO();
    private static final ItemDAO itemDAO = new ItemDAO();
    private static String currentUser;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Inventory Management System ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        String password, confirm;

        System.out.print("Enter password: ");
        password = scanner.nextLine().trim();
        System.out.print("Confirm password: ");
        confirm = scanner.nextLine().trim();

        if (username.isEmpty() || password.length() < 4) {
            System.out.println("Invalid input. Username cannot be empty, and password must be at least 4 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            System.out.println("Passwords do not match.");
            return;
        }
        if (!ValidationUtil.isStrongPassword(password)) {
            System.out.println("Password must be at least 6 chars and include uppercase, lowercase, digit, and special char.");
            return;
        }

        if (userDAO.registerUser(username, password)) {
            System.out.println("User registered successfully.");
        } else {
            System.out.println("Username already exists. Try a different one.");
        }
    }

    private static void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (userDAO.loginUser(username, password)) {
            currentUser = username;
            System.out.println("Login successful.");
            dashboard();
        } else {
            System.out.println("Login failed. Check credentials.");
        }
    }

    private static void dashboard() {
        while (true) {
            System.out.println("\nWelcome, " + currentUser);
            System.out.println("1. Add Item");
            System.out.println("2. View Inventory");
            System.out.println("3. Update Item");
            System.out.println("4. Delete Item");
            System.out.println("5. Generate Receipt");
            System.out.println("6. Show Total Inventory Value");
            System.out.println("7. Search / Sort Items");
            System.out.println("8. Logout"); 
            System.out.print("Choose option: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> addItem();
                case "2" -> viewItems();
                case "3" -> updateItem();
                case "4" -> deleteItem();
                case "5" -> generateReceipt();
                case "6" -> showTotalValue();
                case "7" -> searchSortItems();
                case "8" -> { currentUser = null; return; }


                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void searchSortItems() {
        List<Item> items = itemDAO.getAllItems(currentUser);
        if (items.isEmpty()) {
            System.out.println("No items to search or sort.");
            return;
        }

        System.out.println("\n1. Search by name");
        System.out.println("2. Sort by price");
        System.out.println("3. Sort by quantity");
        System.out.print("Choose option: ");
        String opt = scanner.nextLine().trim();

        switch (opt) {
            case "1":
                System.out.print("Enter name substring: ");
                String sub = scanner.nextLine().trim().toLowerCase();
                items.stream()
                     .filter(i -> i.getItemName().toLowerCase().contains(sub))
                     .sorted(Comparator.comparing(Item::getItemName, String.CASE_INSENSITIVE_ORDER))
                     .forEach(i -> System.out.printf("- %s: %d units @ ₹%.2f\n",
                             i.getItemName(), i.getQuantity(), i.getPrice()));
                break;
            case "2":
                items.stream()
                     .sorted(Comparator.comparingDouble(Item::getPrice))
                     .forEach(i -> System.out.printf("- %s: %d units @ ₹%.2f\n",
                             i.getItemName(), i.getQuantity(), i.getPrice()));
                break;
            case "3":
                items.stream()
                     .sorted(Comparator.comparingInt(Item::getQuantity))
                     .forEach(i -> System.out.printf("- %s: %d units @ ₹%.2f\n",
                             i.getItemName(), i.getQuantity(), i.getPrice()));
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    
    
    private static void showTotalValue() {
        List<Item> items = itemDAO.getAllItems(currentUser);
        if (items.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        double totalValue = 0;
        for (Item item : items) {
            totalValue += item.getQuantity() * item.getPrice();
        }
        System.out.printf("Total inventory value: ₹%.2f\n", totalValue);
    }

    private static void addItem() {
        System.out.print("Item name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Item name cannot be empty.");
            return;
        }

        System.out.print("Quantity: ");
        int qty = readInt();
        System.out.print("Price: ");
        double price = readDouble();

        if (qty < 0 || price < 0) {
            System.out.println("Quantity and price must be non-negative.");
            return;
        }

        itemDAO.addItem(currentUser, new Item(name, qty, price));
        System.out.println("Item added.");
    }

    private static void viewItems() {
        List<Item> items = itemDAO.getAllItems(currentUser);
        if (items.isEmpty()) {
            System.out.println("No items in inventory.");
        } else {
            items.sort(Comparator.comparing(Item::getItemName, String.CASE_INSENSITIVE_ORDER));
            System.out.println("\nInventory:");
            for (Item item : items) {
                System.out.printf("- %s: %d units @ ₹%.2f\n", item.getItemName(), item.getQuantity(), item.getPrice());
            }
        }
    }

    private static void updateItem() {
        System.out.print("Enter item name to update: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Item name cannot be empty.");
            return;
        }

        System.out.print("New quantity: ");
        int qty = readInt();
        System.out.print("New price: ");
        double price = readDouble();

        if (qty < 0 || price < 0) {
            System.out.println("Quantity and price must be non-negative.");
            return;
        }

        System.out.print("Are you sure you want to update the item '" + name + "'? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes")) {
            System.out.println("Update cancelled.");
            return;
        }

        boolean updated = itemDAO.updateItem(currentUser, name, qty, price);
        if (updated) {
            System.out.println("Item updated.");
        } else {
            System.out.println("Item not found.");
        }
    }

    private static void deleteItem() {
        System.out.print("Enter item name to delete: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Item name cannot be empty.");
            return;
        }

        System.out.print("Are you sure you want to delete the item '" + name + "'? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes")) {
            System.out.println("Delete cancelled.");
            return;
        }

        boolean deleted = itemDAO.deleteItem(currentUser, name);
        if (deleted) {
            System.out.println("Item deleted.");
        } else {
            System.out.println("Item not found.");
        }
    }

    private static void generateReceipt() {
        List<Item> items = itemDAO.getAllItems(currentUser);
        if (items.isEmpty()) {
            System.out.println("No items in inventory to generate a receipt.");
            return;
        }

        double total = 0;
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Receipt</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; }");
        html.append("table { border-collapse: collapse; width: 100%; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("th { background-color: #f2f2f2; }");
        html.append("button { margin-bottom: 20px; padding: 10px 20px; font-size: 16px; }");
        html.append("</style>");
        html.append("<script>function printPage() { window.print(); }</script>");
        html.append("</head><body onload=\"printPage()\">");

        html.append("<button onclick=\"printPage()\">🖨️ Print Receipt</button>");
        html.append("<h2>Inventory Receipt</h2>");
        html.append("<p>User: ").append(currentUser).append("</p>");
        html.append("<p>Date: ").append(new Date()).append("</p>");

        html.append("<table>");
        html.append("<tr><th>Item Name</th><th>Quantity</th><th>Price</th><th>Subtotal</th></tr>");

        for (Item item : items) {
            double subtotal = item.getQuantity() * item.getPrice();
            total += subtotal;
            html.append("<tr>")
                .append("<td>").append(item.getItemName()).append("</td>")
                .append("<td>").append(item.getQuantity()).append("</td>")
                .append("<td>₹").append(String.format("%.2f", item.getPrice())).append("</td>")
                .append("<td>₹").append(String.format("%.2f", subtotal)).append("</td>")
                .append("</tr>");
        }

        html.append("</table>");
        html.append("<h3>Total Amount: ₹").append(String.format("%.2f", total)).append("</h3>");
        html.append("</body></html>");

        String fileName = "receipt_" + currentUser + ".html";
        File file = new File(fileName);
        try (PrintWriter out = new PrintWriter(file)) {
            out.println(html.toString());
        } catch (IOException e) {
            System.out.println("Error saving receipt: " + e.getMessage());
            return;
        }

        System.out.println("Receipt saved to " + file.getAbsolutePath());

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(file.toURI());
            } else {
                System.out.println("Automatic opening not supported. Open manually: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Failed to open browser: " + e.getMessage());
        }
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a valid integer: ");
            }
        }
    }

    private static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a valid number: ");
            }
        }
    }
}
