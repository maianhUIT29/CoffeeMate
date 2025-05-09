/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coffeemate.model;

/**
 *
 * @author meiln
 */
public class MenuItem {
    private int MenuItemID;
    private String ItemName;
    private float Price;
    private String Description;
    private String Status = "Available";
    
    public MenuItem() {
        super();
    }
    
    public MenuItem(int MenuItemID, String ItemName, float Price, String Description, String Status){
        super();
        this.MenuItemID = MenuItemID;
        this.ItemName = ItemName;
        this.Price = Price;
        this.Description = Description;
        setStatus(Status);
    }
    
    // Thêm constructor mới
    public MenuItem(String ItemName, float Price, String Description, String Status){
        super();
        this.ItemName = ItemName;
        this.Price = Price;
        this.Description = Description;
        setStatus(Status);
    }
    
    public String getStatus() {
        return Status;
    }
    
    public void setStatus(String Status) {
        if ("Available".equals(Status) || "Unavailable".equals(Status)) {
            this.Status = Status;
        } else {
            throw new IllegalArgumentException("Status must be 'Available' or 'Unavailable'");
        }
        
    }
    
    public int getMenuItemID() {
        return MenuItemID;
    }
    
    public void setMenuItemID(int MenuItemID){
        this.MenuItemID = MenuItemID;
    }
    
    public String getItemName() {
        return ItemName;
    }
    
    public void setItemName(String ItemName){
        this.ItemName = ItemName;
    }
    
    public float getPrice() {
        return Price;
    }
    
    public void setPrice(float Price){
        this.Price = Price;
    }
    
    public String getDescription() {
        return Description;
    }
    
    public void setDescription(String Description){
        this.Description = Description;
    }
    
    @Override
    public String toString() {
        return "MenuItem [MenuItemID = " + MenuItemID + ", ItemName: " + ItemName + ", Price = " + Price + ", Description: " + Description + ", Status: " + Status + "]";
    }
}
