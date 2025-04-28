package test;

import dao.MenuItemDAO;
import java.util.ArrayList;
import model.MenuItem;

public class TestMenuItemDAO {
    public static void main(String[] args) {
        //MenuItem menuItem1 = new MenuItem("Milk Coffee", 20000, "Coffee with milk", "Available");
        //MenuItem menuItem2 = new MenuItem("Cacao Milk Coffee", 25000, "Green Tea with milk and cacao", "Unavailable");
        
        //MenuItemDAO.getInstance().insert(menuItem1);
        //MenuItemDAO.getInstance().insert(menuItem2);
        
        //MenuItem menuItem1 = new MenuItem(8, "Ice Cream", 20000, "Vani", "Available");
        //MenuItemDAO.getInstance().update(menuItem1);
        
        //MenuItemDAO.getInstance().delete(menuItem1);
        // khi delete có thể bị trigger lỗi thì để ALTER TRIGGER LOGMENUITEMACTIONS DISABLE; 
        // để tắt trigger trước khi xóa => sau khi xóa thì ALTER TRIGGER LOGMENUITEMACTIONS ENABLE; 
        // để bật lại trigger 
        
        ArrayList<MenuItem> list = MenuItemDAO.getInstance().selectAll();
        for (MenuItem menuItem : list) {
            System.out.println(menuItem.toString());
        }
        
        System.out.println("------------------------------------");
        MenuItem find = new MenuItem();
        find.setMenuItemID(7);
        MenuItem menuItem = MenuItemDAO.getInstance().selectById(find);
        System.out.println(menuItem);
        
        System.out.println("------------------------------------");
        ArrayList<MenuItem> list2 = MenuItemDAO.getInstance().selectByCondition("Price < 30000");
        for (MenuItem menu : list2) {
            System.out.println(menu.toString());
        }
    }
}
