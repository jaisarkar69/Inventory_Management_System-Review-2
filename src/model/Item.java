package model;

import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemName;
    private int quantity;
    private double price;

    public Item(String itemName, int quantity, double price) {
        this.itemName = itemName.trim();
        this.quantity = quantity;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return itemName + "," + quantity + "," + price;
    }

    // Parses a string into an Item object
    public static Item fromString(String data) {
        try {
            String[] parts = data.split(",");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid item format: " + data);
            }

            String name = parts[0].trim();
            int quantity = Integer.parseInt(parts[1].trim());
            double price = Double.parseDouble(parts[2].trim());

            return new Item(name, quantity, price);
        } catch (Exception e) {
            System.out.println("⚠️ Failed to parse item line: '" + data + "'. Error: " + e.getMessage());
            return null; // return null to skip invalid lines
        }
    }

}
