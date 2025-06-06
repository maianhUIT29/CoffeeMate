package com.coffeemate.utils;

import com.coffeemate.model.Employee;

public class SessionManager {

    private static SessionManager instance;  // Biến singleton
    private Employee loggedInEmployee;       // Lưu trữ thông tin người dùng đã đăng nhập

    // Constructor riêng tư để tránh khởi tạo từ bên ngoài
    private SessionManager() {
    }

    // Phương thức để lấy instance duy nhất của SessionManager
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Lấy thông tin người dùng đã đăng nhập (alias cho getLoggedInEmployee)
    public Employee getLoggedInUser() {
        return loggedInEmployee;
    }

    // Lấy thông tin người dùng đã đăng nhập (tên cũ)
    public Employee getLoggedInEmployee() {
        return loggedInEmployee;
    }

    // Thiết lập người dùng đăng nhập
    public void setLoggedInUser(Employee loggedInEmployee) {
        this.loggedInEmployee = loggedInEmployee;
    }

    // Đăng xuất (reset trạng thái người dùng)
    public void logout() {
        this.loggedInEmployee = null;
    }

    // Kiểm tra trạng thái đăng nhập
    public boolean isUserLoggedIn() {
        return loggedInEmployee != null;
    }
}
