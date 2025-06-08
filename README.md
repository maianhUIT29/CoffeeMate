 <p align="center">
  <a href="https://www.uit.edu.vn/" title="Trường Đại học Công nghệ Thông tin" style="border: none;">
    <img src="https://i.imgur.com/WmMnSRt.png" alt="Trường Đại học Công nghệ Thông tin | University of Information Technology">
  </a>
</p>



## GIỚI THIỆU ĐỒ ÁN

-    **Đề tài:** Xây dựng hệ thống quản lý quán cà phê

# CoffeeMate

**CoffeeMate** là ứng dụng quản lý quán cà phê viết bằng Java Swing, hỗ trợ đầy đủ nghiệp vụ: quản lý danh mục món, nhân viên, bán hàng (POS), hóa đơn và thống kê.

## 📌 Công nghệ sử dụng

* **IDE:** [Apache NetBeans](https://netbeans.apache.org/download/index.html)
* **Backend (JDK):** [Java SE 8+](https://www.java.com/en/download/)
* **Giao diện (Frontend):** [Java Swing](https://docs.oracle.com/javase/tutorial/uiswing/) (đi kèm JDK)
* **Cơ sở dữ liệu:** [Oracle Database 19c](https://www.oracle.com/database/technologies/oracle19c.html)
* **Báo cáo & Chart:** [JFreeChart](https://www.jfree.org/jfreechart/download.html)
* **Xuất Excel:** [Apache POI](https://poi.apache.org/download.html)

## 🚀 Tính năng chính

1. **Quản lý món**: Thêm, sửa, xóa, tìm kiếm, phân trang.
2. **Quản lý nhân viên**: Thêm, sửa, xóa, tìm kiếm.
3. **Bán hàng (Cashier POS)**: Chọn món, sửa số lượng, xóa món, tính tổng, thanh toán.
4. **Quản lý hóa đơn**: Sửa, xóa, xem chi tiết từng hóa đơn, xuất Excel.
5. **Admin Dashboard**: Hiển thị số liệu tổng quan và biểu đồ doanh thu, món bán chạy, top nhân viên, phân phối vai trò.
6. **Xác thực & phân quyền**: Đăng nhập theo vai trò (Manager, Cashier, Bartender).

## ⚙️ Yêu cầu môi trường

* Java SE Development Kit (JDK) 8 trở lên
* Oracle Database (12c hoặc cao hơn)
* Maven (nếu sử dụng build quản lý)

## 🛠️ Cài đặt & chạy nhanh

1. **Chuẩn bị**: Tạo schema và import file `schema.sql` vào Oracle.
2. **Cấu hình**: Cập nhật thông tin kết nối DB trong `configs/DBConnection.java`.
3. **Build**:

   ```bash
   mvn clean package
   ```
4. **Chạy**:

   ```bash
   java -jar target/CoffeeMate.jar
   ```

## 📂 Cấu trúc dự án

```
/src
 ├─ component/    # Các panel Swing (MenuPanel, CashierView, AdminDashBoard...)
 ├─ controller/   # Xử lý nghiệp vụ, giao tiếp DAO
 ├─ dao/          # Truy vấn DB
 ├─ model/        # Định nghĩa Entity
 ├─ utils/        # Hỗ trợ Session, UIHelper
 └─ resources/    # Hình ảnh, icon, tài nguyên tĩnh
```

## 📈 Diagram thiết kế (đặt trong `docs/diagrams`)

* **Class Diagram**: `class_diagram.png`
* **Sequence Diagram**: `sequence_diagram.png`
* **Use Case Diagram**: `usecase_diagram.png`

## 🔮 Hướng phát triển

* In hóa đơn, sao lưu dữ liệu, cấu hình theme/ngôn ngữ.
* Xuất báo cáo PDF, tích hợp thanh toán trực tuyến.
* Responsive Web/Mobile version (React, JavaFX).


## THÀNH VIÊN NHÓM

| STT | MSSV     | Họ và Tên            | GitHub                            | Email                  |
| :-- | :------- | :------------------- | :-------------------------------- | :--------------------- |
| 1   | 23520039 | Đào Thị Quỳnh Anh    | https://github.com/Umashu-QA      | 23520039@gm.uit.edu.vn |
| 2   | 23520065 | Nguyễn Ngọc Mai Anh  | https://github.com/maianhUIT29    | 23520065@gm.uit.edu.vn |
| 3   | 23520052 | Mai Lan Anh          | https://github.com/23520052       | 23520052@gm.uit.edu.vn |
