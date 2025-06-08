-- ====================================
-- 1. BẢNG MÓN (MenuItem)
-- Mô tả: Lưu trữ thông tin các món ăn trong hệ thống quản lý bán hàng.
-- ====================================
CREATE TABLE MenuItem ( 
    MenuItemID NUMBER PRIMARY KEY,             -- Khóa chính, định danh duy nhất cho mỗi món ăn
    ItemName VARCHAR2(100) NOT NULL,            -- Tên món ăn, không thể để trống
    Price NUMBER(10,2) NOT NULL,                -- Giá của món ăn, không thể âm
    Description VARCHAR2(200),                  -- Mô tả chi tiết về món ăn
    Status VARCHAR2(20) DEFAULT 'Available' CHECK (Status IN ('Available', 'Unavailable')) -- Trạng thái món ăn (Available hoặc Unavailable)
);

-- Ràng buộc: Đảm bảo giá món ăn phải >= 0 và trạng thái món phải là "Available" hoặc "Unavailable"
ALTER TABLE MenuItem ADD CONSTRAINT menuitem_status_check CHECK (Status IN ('Available', 'Unavailable'));
ALTER TABLE MenuItem ADD CONSTRAINT menuitem_price_check CHECK (Price >= 0);

-- ====================================
-- 2. BẢNG NHÂN VIÊN (Employee)
-- Mô tả: Lưu trữ thông tin cá nhân và công việc của nhân viên trong hệ thống.
-- ====================================
CREATE TABLE Employee (
    EmployeeID NUMBER PRIMARY KEY,
    FullName   VARCHAR2(100) NOT NULL,
    Role       VARCHAR2(50)  NOT NULL
               CONSTRAINT employee_role_check CHECK (Role IN ('Manager','Bartender','Cashier')),
    Phone      VARCHAR2(20),
    Email      VARCHAR2(100),
    HireDate   DATE          NOT NULL,
    Password   VARCHAR2(50)  DEFAULT '123456789' NOT NULL,
    Status     VARCHAR2(20)  DEFAULT 'active'
               CONSTRAINT employee_status_check CHECK (Status IN ('active','inactive'))
);


-- ====================================
-- 3. BẢNG HÓA ĐƠN (Invoice)
-- Mô tả: Lưu trữ thông tin về hóa đơn thanh toán.
-- ====================================
CREATE TABLE Invoice (
    InvoiceID NUMBER PRIMARY KEY,              -- Khóa chính, định danh duy nhất cho hóa đơn
    EmployeeID NUMBER,                         -- Khóa ngoại liên kết với bảng Employee
    TotalAmount NUMBER(10,2),                  -- Tổng số tiền của hóa đơn
    PaymentStatus VARCHAR2(20) CHECK (PaymentStatus IN ('Paid', 'Unpaid')), -- Trạng thái thanh toán của hóa đơn
    IssueDate DATE NOT NULL,                   -- Ngày xuất hóa đơn
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID) -- Liên kết với bảng Employee
);

-- Ràng buộc: Đảm bảo rằng trạng thái thanh toán chỉ có thể là "Paid" hoặc "Unpaid"
ALTER TABLE Invoice ADD CONSTRAINT invoice_payment_status_check CHECK (PaymentStatus IN ('Paid', 'Unpaid'));
ALTER TABLE Invoice ADD CONSTRAINT invoice_total_amount_check CHECK (TotalAmount >= 0);

-- ====================================
-- 4. BẢNG CHI TIẾT HÓA ĐƠN (Detail)
-- Mô tả: Lưu trữ chi tiết các món ăn trong mỗi hóa đơn.
-- ====================================
CREATE TABLE Detail (
    OrderDetailID NUMBER PRIMARY KEY,          -- Khóa chính, định danh duy nhất cho chi tiết hóa đơn
    InvoiceID NUMBER,                          -- Khóa ngoại liên kết với bảng Invoice
    MenuItemID NUMBER,                         -- Khóa ngoại liên kết với bảng MenuItem
    Quantity NUMBER NOT NULL CHECK (Quantity > 0), -- Số lượng món ăn trong hóa đơn, không thể âm
    UnitPrice NUMBER(10,2) NOT NULL,           -- Giá của mỗi món ăn trong chi tiết hóa đơn
    FOREIGN KEY (InvoiceID) REFERENCES Invoice(InvoiceID), -- Liên kết với bảng Invoice
    FOREIGN KEY (MenuItemID) REFERENCES MenuItem(MenuItemID) -- Liên kết với bảng MenuItem
);

-- Ràng buộc: Đảm bảo rằng số lượng món ăn và giá đơn vị phải hợp lệ
ALTER TABLE Detail ADD CONSTRAINT detail_quantity_check CHECK (Quantity > 0);
ALTER TABLE Detail ADD CONSTRAINT detail_unit_price_check CHECK (UnitPrice > 0);

-- ====================================
-- SEQUENCE và TRIGGER
-- ====================================

-- SEQUENCE và TRIGGER cho MenuItem (Món ăn)
CREATE SEQUENCE menuitem_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE OR REPLACE TRIGGER menuitem_bi
    BEFORE INSERT ON MenuItem
    FOR EACH ROW
BEGIN
    :NEW.MenuItemID := menuitem_seq.NEXTVAL;
END;
/

-- SEQUENCE và TRIGGER cho Employee (Nhân viên)
CREATE SEQUENCE employee_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE OR REPLACE TRIGGER employee_bi
    BEFORE INSERT ON Employee
    FOR EACH ROW
BEGIN
    :NEW.EmployeeID := employee_seq.NEXTVAL;
END;
/


-- SEQUENCE và TRIGGER cho Invoice (Hóa đơn)
CREATE SEQUENCE invoice_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE OR REPLACE TRIGGER invoice_bi
    BEFORE INSERT ON Invoice
    FOR EACH ROW
BEGIN
    :NEW.InvoiceID := invoice_seq.NEXTVAL;
END;
/

-- SEQUENCE và TRIGGER cho Detail (Chi tiết hóa đơn)
CREATE SEQUENCE detail_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE OR REPLACE TRIGGER detail_bi
    BEFORE INSERT ON Detail
    FOR EACH ROW
BEGIN
    :NEW.OrderDetailID := detail_seq.NEXTVAL;
END;
/

-- Thêm 20 món nước vào bảng MenuItem
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Cà Phê Sữa Đá', 20000, 'Cà phê đen pha với sữa đặc, đá viên.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Cà Phê Trứng', 25000, 'Cà phê hòa quyện với lòng đỏ trứng, kem sữa đặc.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Nước Mía', 18000, 'Nước mía tươi ép nguyên chất.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Trà Sữa', 22000, 'Trà xanh pha cùng sữa tươi, thạch trái cây.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Trà Đào', 20000, 'Trà xanh pha với đào, đá viên mát lạnh.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Trà Sen Vàng', 22000, 'Trà sen với hương thơm đặc trưng, ngọt thanh.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Nước Dừa', 25000, 'Nước dừa tươi nguyên chất, ngọt mát tự nhiên.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Cà Phê Đen', 15000, 'Cà phê đen nguyên chất, đậm đà.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Cà Phê Sữa', 18000, 'Cà phê đen pha với sữa đặc, ngọt ngào.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Sinh Tố Bơ', 25000, 'Sinh tố bơ mịn màng, thơm ngậy.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Sinh Tố Dưa Hấu', 23000, 'Sinh tố dưa hấu mát lạnh, ngọt tự nhiên.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Nước Ép Cà Rốt', 22000, 'Nước ép cà rốt tươi, ngọt thanh, tốt cho sức khỏe.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Nước Ép Dứa', 20000, 'Nước ép dứa tươi, thanh mát, thơm ngọt.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Iced Americano', 22000, 'Cà phê đen lạnh, đậm vị.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Iced Latte', 25000, 'Cà phê latte lạnh, pha sữa tươi.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Mocha', 26000, 'Cà phê mocha với sô cô la và sữa đặc.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Matcha Latte', 27000, 'Trà matcha pha sữa tươi, thơm ngon.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Cacao', 24000, 'Cacao nóng ngọt ngào, béo thơm.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Cà Phê Bạc Xỉu', 22000, 'Cà phê sữa pha với một ít đá viên, dễ uống.', 'Available');
INSERT INTO MenuItem (ItemName, Price, Description, Status) VALUES ('Sữa Chua Dẻo', 20000, 'Sữa chua dẻo với trái cây tươi và mật ong.', 'Available');
–Them 10 NV
INSERT INTO Employee (EmployeeID, FullName, Role, Phone, Email, HireDate, Password) VALUES
  (11, 'Nguyễn Thị F',   'Manager',   '0906789012', 'ntf@gmail.com', TO_DATE('01-JUN-23','DD-MON-YY'), '123456789');

INSERT INTO Employee (EmployeeID, FullName, Role, Phone, Email, HireDate, Password) VALUES
  (12, 'Trần Minh G',    'Bartender', '0907890123', 'tmg@gmail.com', TO_DATE('10-JUN-23','DD-MON-YY'), '123456789');

INSERT INTO Employee (EmployeeID, FullName, Role, Phone, Email, HireDate, Password) VALUES
  (13, 'Lê Thanh H',     'Cashier',   '0908901234', 'lth@gmail.com', TO_DATE('01-JUL-23','DD-MON-YY'), '123456789');

INSERT INTO Employee (EmployeeID, FullName, Role, Phone, Email, HireDate, Password) VALUES
  (14, 'Bùi Hồng I',     'Bartender', '0909012345', 'bhi@gmail.com', TO_DATE('05-JUL-23','DD-MON-YY'), '123456789');

INSERT INTO Employee (EmployeeID, FullName, Role, Phone, Email, HireDate, Password) VALUES
  (15, 'Phạm Thiện J',   'Manager',   '0900123456', 'ptj@gmail.com', TO_DATE('10-JUL-23','DD-MON-YY'), '123456789');

INSERT INTO Employee (EmployeeID, FullName, Role, Phone, Email, HireDate, Password) VALUES
  (1,  'Nguyễn Văn A',   'Manager',   '0901234567', 'nva@gmail.com', TO_DATE('01-JAN-23','DD-MON-YY'), '123456789');

INSERT INTO Employee (EmployeeID, FullName, Role, Phone, Email, HireDate, Password) VALUES
  (2,  'Trần Thị B',     'Bartender', '0902345678', 'ttb@gmail.com', TO_DATE('01-FEB-23','DD-MON-YY'), '123456789');

INSERT INTO Employee (EmployeeID, FullName, Role, Phone, Email, HireDate, Password) VALUES
  (3,  'Lê Quang C',     'Cashier',   '0903456789', 'lqc@gmail.com', TO_DATE('01-MAR-23','DD-MON-YY'), '123456789');

INSERT INTO Employee (EmployeeID, FullName, Role, Phone, Email, HireDate, Password) VALUES
  (4,  'Phan Thị D',     'Bartender', '0904567890', 'ptd@gmail.com', TO_DATE('01-APR-23','DD-MON-YY'), '123456789');

INSERT INTO Employee (EmployeeID, FullName, Role, Phone, Email, HireDate, Password) VALUES
  (5,  'Bùi Minh E',     'Manager',   '0905678901', 'bme@gmail.com', TO_DATE('01-MAY-23','DD-MON-YY'), '123456789');


–THEM 5000 HOA DON
SET SERVEROUTPUT ON
DECLARE
  ----------------------------------------------------------------
  -- Biến chung
  ----------------------------------------------------------------
  v_inv_id         NUMBER;         -- InvoiceID vừa được tạo
  v_emp_id         NUMBER;         -- EmployeeID được chọn ngẫu nhiên
  v_status         VARCHAR2(20);   -- PaymentStatus ('Paid' hoặc 'Unpaid')
  v_issue_date     DATE;           -- IssueDate ngẫu nhiên
  v_total_amount   NUMBER(12,2);   -- Tổng tiền của hóa đơn đang tính
  ----------------------------------------------------------------
  -- Danh sách tạm thời lưu các món đã chọn cho 1 hóa đơn
  TYPE t_price_map      IS TABLE OF NUMBER INDEX BY PLS_INTEGER;
  v_selected_items      t_price_map;    -- KEY = MenuItemID, VALUE = Price
  ----------------------------------------------------------------
  -- Dữ liệu tĩnh của MenuItem: lưu MenuItemID và Price
  v_menuitem_ids        t_price_map;    -- index -> MenuItemID
  v_menuitem_prices     t_price_map;    -- index -> Price
  v_menuitem_count      NUMBER;         -- tổng số món trong MenuItem
  ----------------------------------------------------------------
  -- Biến phụ
  v_rand_index    PLS_INTEGER;   -- chỉ mục ngẫu nhiên để chọn món
  v_try_count     PLS_INTEGER;   -- số lượt thử chọn món
  v_item_id       NUMBER;        -- MenuItemID tạm
  v_price         NUMBER;        -- Giá tương ứng
  ----------------------------------------------------------------
BEGIN
  ----------------------------------------------------------------
  -- 1. Nạp toàn bộ MenuItem vào mảng để chọn ngẫu nhiên
  ----------------------------------------------------------------
  SELECT COUNT(*) 
    INTO v_menuitem_count 
    FROM MenuItem;

  IF v_menuitem_count = 0 THEN
    RAISE_APPLICATION_ERROR(-20001, 'Bảng MenuItem hiện đang trống.');
  END IF;

  DECLARE
    CURSOR c_menu IS
      SELECT MenuItemID, Price
        FROM MenuItem;
    idx PLS_INTEGER := 0;
  BEGIN
    FOR r IN c_menu LOOP
      idx := idx + 1;
      v_menuitem_ids(idx)    := r.MenuItemID;
      v_menuitem_prices(idx) := r.Price;
    END LOOP;
  END;

  ----------------------------------------------------------------
  -- 2. Bắt đầu loop tạo 5000 hóa đơn
  ----------------------------------------------------------------
  FOR i IN 1..5000 LOOP
    --------------------------------------------------------------
    -- 2.1 Chọn ngẫu nhiên một EmployeeID (trong bảng Employee)
    --------------------------------------------------------------
    SELECT EmployeeID
      INTO v_emp_id
      FROM (
        SELECT EmployeeID
          FROM Employee
         ORDER BY DBMS_RANDOM.VALUE
      )
     WHERE ROWNUM = 1;

    --------------------------------------------------------------
    -- 2.2 Chọn ngẫu nhiên PaymentStatus: 80% là 'Paid', 20% 'Unpaid'
    --------------------------------------------------------------
    IF DBMS_RANDOM.VALUE(0,1) < 0.8 THEN
      v_status := 'Paid';
    ELSE
      v_status := 'Unpaid';
    END IF;

    --------------------------------------------------------------
    -- 2.3 Tạo IssueDate ngẫu nhiên trong 5 năm gần nhất
    --------------------------------------------------------------
    v_issue_date := TRUNC(SYSDATE - DBMS_RANDOM.VALUE(0, 5*365));

    --------------------------------------------------------------
    -- 2.4 Chọn các MenuItem để cộng dồn v_total_amount sao cho
    --     100.000 <= v_total_amount <= 1.000.000
    --------------------------------------------------------------
    v_total_amount := 0;
    v_selected_items.DELETE;   -- xóa danh sách cũ (nếu có)
    v_try_count := 0;

    -- Bắt đầu chọn món cho đến khi tổng >= 100.000 hoặc đã thử quá nhiều lần
    LOOP
      EXIT WHEN v_total_amount >= 100000;
      v_rand_index := TRUNC(DBMS_RANDOM.VALUE(1, v_menuitem_count+1));
      v_item_id := v_menuitem_ids(v_rand_index);
      v_price   := v_menuitem_prices(v_rand_index);

      -- Nếu chưa chọn món này ở v_selected_items mới thêm
      IF NOT v_selected_items.EXISTS(v_item_id) THEN
        v_selected_items(v_item_id) := v_price;
        v_total_amount := v_total_amount + v_price;
      END IF;

      v_try_count := v_try_count + 1;
      -- Nếu đã thử quá nhiều lần, thoát để tránh vòng lặp vô tận
      IF v_try_count > v_menuitem_count * 2 THEN
        EXIT;
      END IF;
    END LOOP;

    -- Nếu sau vòng trên, tổng < 100.000 (trường hợp menu item giá thấp),
    -- ta bắt buộc thêm món đắt nhất để đảm bảo >= 100.000
    IF v_total_amount < 100000 THEN
      DECLARE
        v_max_price NUMBER;
        v_max_id    NUMBER;
      BEGIN
        SELECT MenuItemID, Price
          INTO v_max_id, v_max_price
          FROM (
            SELECT MenuItemID, Price
              FROM MenuItem
             ORDER BY Price DESC
          )
         WHERE ROWNUM = 1;

        IF NOT v_selected_items.EXISTS(v_max_id) THEN
          v_selected_items(v_max_id) := v_max_price;
          v_total_amount := v_total_amount + v_max_price;
        END IF;
      END;
    END IF;

    -- Nếu tổng > 1.000.000, phải bỏ bớt món có giá cao nhất cho đến khi <= 1.000.000
    WHILE v_total_amount > 1000000 LOOP
      DECLARE
        v_max_price_in_list NUMBER := 0;
        v_max_key           NUMBER := NULL;
      BEGIN
        -- Scan qua v_selected_items để tìm món có giá lớn nhất
        v_max_price_in_list := 0;
        v_max_key := NULL;
        FOR idx IN v_selected_items.FIRST .. v_selected_items.LAST LOOP
          IF v_selected_items.EXISTS(idx) THEN
            IF v_selected_items(idx) > v_max_price_in_list THEN
              v_max_price_in_list := v_selected_items(idx);
              v_max_key           := idx;
            END IF;
          END IF;
        END LOOP;

        IF v_max_key IS NOT NULL THEN
          v_total_amount := v_total_amount - v_max_price_in_list;
          v_selected_items.DELETE(v_max_key);
        ELSE
          EXIT; -- không còn món nào để xóa
        END IF;
      END;
    END LOOP;

    --------------------------------------------------------------
    -- 2.5 Chèn bản ghi vào Invoice với TotalAmount = v_total_amount
    --------------------------------------------------------------
    INSERT INTO Invoice (EmployeeID, TotalAmount, PaymentStatus, IssueDate)
    VALUES (v_emp_id, v_total_amount, v_status, v_issue_date);
    v_inv_id := invoice_seq.CURRVAL;

    --------------------------------------------------------------
    -- 2.6 Chèn từng dòng Detail tương ứng với mỗi món trong v_selected_items
    --------------------------------------------------------------
    FOR idx IN v_selected_items.FIRST .. v_selected_items.LAST LOOP
      IF v_selected_items.EXISTS(idx) THEN
        INSERT INTO Detail (InvoiceID, MenuItemID, Quantity, UnitPrice)
        VALUES (v_inv_id, idx, 1, v_selected_items(idx));
      END IF;
    END LOOP;

    --------------------------------------------------------------
    -- 2.7 In tiến độ sau mỗi 500 hóa đơn
    --------------------------------------------------------------
    IF MOD(i, 500) = 0 THEN
      DBMS_OUTPUT.PUT_LINE('Đã chèn xong ' || i || ' / 5000 hóa đơn.');
    END IF;

  END LOOP; -- kết thúc FOR i IN 1..5000

  COMMIT;
  DBMS_OUTPUT.PUT_LINE('Hoàn tất: 5000 Invoice và Detail đã được tạo thành công.');

EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
    DBMS_OUTPUT.PUT_LINE('Lỗi xảy ra: ' || SQLERRM);
END;
/





