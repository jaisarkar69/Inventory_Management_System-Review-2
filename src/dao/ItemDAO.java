package dao;

import model.Item;
import util.FileUtil;

import java.util.*;

public class ItemDAO {

    // Gets the inventory file path for the given username
    private String getFileForUser(String username) {
        return "resources/" + username.trim().toLowerCase() + "_inventory.txt";
    }

    // Adds an item to the user's inventory
    public void addItem(String username, Item item) {
        String file = getFileForUser(username);
        FileUtil.writeToFile(file, item.toString(), true);
    }

    // Retrieves all items in the user's inventory
    public List<Item> getAllItems(String username) {
        String file = getFileForUser(username);
        List<String> lines = FileUtil.readFromFile(file);
        List<Item> items = new ArrayList<>();
        for (String line : lines) {
        	Item item = Item.fromString(line);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    // Updates an existing item by name
    public boolean updateItem(String username, String name, int quantity, double price) {
        String file = getFileForUser(username);
        List<Item> items = getAllItems(username);
        boolean updated = false;
        String trimmedName = name.trim();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getItemName().equalsIgnoreCase(trimmedName)) {
                items.set(i, new Item(trimmedName, quantity, price));
                updated = true;
                break;
            }
        }

        if (updated) {
            overwriteItems(file, items);
        }

        return updated;
    }

    // Deletes an item by name
    public boolean deleteItem(String username, String name) {
        String file = getFileForUser(username);
        List<Item> items = getAllItems(username);
        String trimmedName = name.trim();

        int initialSize = items.size();
        items.removeIf(item -> item.getItemName().equalsIgnoreCase(trimmedName));
        boolean removed = items.size() < initialSize;

        if (removed) {
            overwriteItems(file, items);
        }

        return removed;
    }

    // Overwrites the inventory file with updated item list
    private void overwriteItems(String file, List<Item> items) {
        List<String> lines = new ArrayList<>();
        for (Item item : items) {
            lines.add(item.toString());
        }
        FileUtil.overwriteFile(file, lines);
    }
}
