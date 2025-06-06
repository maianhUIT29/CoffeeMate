package com.coffeemate.controller;

import com.coffeemate.dao.MenuItemDAO;
import com.coffeemate.model.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuItemController {

    private MenuItemDAO menuItemDAO;

    public MenuItemController() {
        this.menuItemDAO = MenuItemDAO.getInstance(); // Initialize the DAO
    }

    // Create a new MenuItem
    public int addMenuItem(MenuItem menuItem) {
        return menuItemDAO.insert(menuItem);
    }

    // Update an existing MenuItem
    public int updateMenuItem(MenuItem menuItem) {
        return menuItemDAO.update(menuItem);
    }

    // Delete a MenuItem
    public int deleteMenuItem(MenuItem menuItem) {
        return menuItemDAO.delete(menuItem);
    }

    // Get all MenuItems
    public List<MenuItem> getAllMenuItems() {
        return menuItemDAO.selectAll();
    }

    // Get a MenuItem by its ID
    public MenuItem getMenuItemById(int menuItemId) {
        MenuItem menuItem = new MenuItem();
        menuItem.setMenuItemID(menuItemId);
        return menuItemDAO.selectById(menuItem);
    }

    // Search MenuItems by name (case-insensitive)
    public List<MenuItem> searchMenuItemsByName(String itemName) {
        return menuItemDAO.searchByName(itemName);
    }

    // Check if a MenuItem with a specific name exists
    public boolean checkIfMenuItemExists(String itemName) {
        return menuItemDAO.checkIfExists(itemName);
    }

    // Delete or disable a MenuItem based on if it has been sold
    public boolean deleteOrDisableMenuItem(int menuItemId) {
        return menuItemDAO.deleteOrDisableMenuItem(menuItemId);
    }

    // Delete a MenuItem by its ID directly
    public int deleteMenuItemById(int menuItemId) {
        return menuItemDAO.deleteByID(menuItemId);
    }
    
    // Trả về tổng số MenuItem trong cơ sở dữ liệu
 
    public int countMenuItem() {
        return menuItemDAO.countMenuItem();
    }
    
    //tra về top selling
    public List<Map<String, Object>> getTopSellingMenuItems(int topN) {
        return menuItemDAO.getTopSellingMenuItems(topN);
    }
}
