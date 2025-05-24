package com.coffeemate.dao;

import java.util.ArrayList;

public interface DAOInterface<T> {
    public int insert(T t);
    
    public int update(T t);
    
    public int delete(T t);
    
    public int deleteByID(int MenuItemID);
    
    public ArrayList<T> selectAll();
    
    public T selectById(T t);
      
    public ArrayList<T> selectByCondition(String condition);

    public boolean checkIfExists(String ItemName);
    
    public ArrayList<T> searchByName(String ItemName);
    
    public boolean deleteOrDisableMenuItem(int menuItemId);
}
