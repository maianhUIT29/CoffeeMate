-- -------------------------------------- 
-- I. CREATE TABLES + SEQUENCES + INDEX 
-- -------------------------------------- 
-- Tạo SEQUENCE cho khóa chính 
CREATE SEQUENCE MenuItem_seq START WITH 1 INCREMENT BY 1; 
CREATE SEQUENCE Employee_seq START WITH 1 INCREMENT BY 1; 
CREATE SEQUENCE Shift_seq START WITH 1 INCREMENT BY 1; 
CREATE SEQUENCE CafeOrder_seq START WITH 1 INCREMENT BY 1; 
CREATE SEQUENCE OrderDetail_seq START WITH 1 INCREMENT BY 1; 
CREATE SEQUENCE OrderInvoice_seq START WITH 1 INCREMENT BY 1; 
CREATE SEQUENCE InventoryReceipt_seq START WITH 1 INCREMENT BY 1; 
CREATE SEQUENCE MaterialReport_seq START WITH 1 INCREMENT BY 1; 
CREATE SEQUENCE Payroll_seq START WITH 1 INCREMENT BY 1; 

-- Bảng Món 
CREATE TABLE MenuItem ( 
MenuItemID NUMBER PRIMARY KEY, 
ItemName VARCHAR2(100) NOT NULL, 
Price NUMBER(10,2) NOT NULL, 
Description VARCHAR2(200), 
Status VARCHAR2(20) DEFAULT 'Available' CHECK (Status IN ('Available', 'Unavailable')) 
); 
-- Bảng Nhân viên 
CREATE TABLE Employee ( 
EmployeeID NUMBER PRIMARY KEY, 
FullName VARCHAR2(100) NOT NULL, 
Role VARCHAR2(50) NOT NULL CHECK (Role IN ('Manager', 'Bartender', 'Cashier')), 
Phone VARCHAR2(20), 
Email VARCHAR2(100), 
HireDate DATE NOT NULL,
Password VARCHAR(50) DEFAULT '123456789' NOT NULL
); 
-- Bảng Ca làm việc 
CREATE TABLE Shift ( 
ShiftID NUMBER PRIMARY KEY, 
EmployeeID NUMBER, 
StartTime DATE NOT NULL, 
EndTime DATE NOT NULL, 
SalaryPerHour NUMBER(10,2), 
FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID) 
); 
-- Bảng Đơn hàng 
CREATE TABLE CafeOrder ( 
OrderID NUMBER PRIMARY KEY, 
EmployeeID NUMBER, 
OrderDate DATE NOT NULL, 
TotalAmount NUMBER(10,2), 
OrderStatus VARCHAR2(20) DEFAULT 'Pending' CHECK (OrderStatus IN ('Pending', 'Confirmed', 'Cancelled', 'Completed')), 
FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID) 
); 
-- Bảng Chi tiết đơn hàng 
CREATE TABLE OrderDetail ( 
OrderDetailID NUMBER PRIMARY KEY, 
OrderID NUMBER, 
MenuItemID NUMBER, 
Quantity NUMBER NOT NULL CHECK (Quantity > 0), 
UnitPrice NUMBER(10,2) NOT NULL, 
FOREIGN KEY (OrderID) REFERENCES CafeOrder(OrderID), 
FOREIGN KEY (MenuItemID) REFERENCES MenuItem(MenuItemID) 
); 
-- Bảng Hóa đơn 
CREATE TABLE OrderInvoice ( 
InvoiceID NUMBER PRIMARY KEY, 
OrderID NUMBER, 
IssueDate DATE NOT NULL, 
TotalAmount NUMBER(10,2), 
PaymentStatus VARCHAR2(20) DEFAULT 'Unpaid' CHECK (PaymentStatus IN ('Paid', 'Unpaid')), 
FOREIGN KEY (OrderID) REFERENCES CafeOrder(OrderID) 
); 
-- Bảng Phiếu nhập kho 
CREATE TABLE InventoryReceipt ( 
ReceiptID NUMBER PRIMARY KEY, 
EmployeeID NUMBER, 
ReceiptDate DATE NOT NULL, 
TotalAmount NUMBER(10,2), 
Description VARCHAR2(200), 
FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID) 
); 
-- Bảng Báo cáo sử dụng nguyên liệu 
CREATE TABLE MaterialReport ( 
ReportID NUMBER PRIMARY KEY, 
EmployeeID NUMBER, 
ReportDate DATE NOT NULL, 
MaterialName VARCHAR2(100) NOT NULL, 
QuantityUsed NUMBER NOT NULL CHECK (QuantityUsed >= 0), 
Notes VARCHAR2(200), 
FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID) 
); 
-- Bảng Lương 
CREATE TABLE Payroll ( 
PayrollID NUMBER PRIMARY KEY, 
EmployeeID NUMBER, 
ShiftID NUMBER, 
TotalHours NUMBER(5,2), 
TotalSalary NUMBER(10,2), 
PayrollDate DATE NOT NULL, 
FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID), 
FOREIGN KEY (ShiftID) REFERENCES Shift(ShiftID) 
); 
-- Bảng Log hành động 
CREATE TABLE ActionLog ( 
LogID NUMBER PRIMARY KEY, 
TableName VARCHAR2(50) NOT NULL, 
RecordID NUMBER NOT NULL, 
Action VARCHAR2(50) NOT NULL CHECK (Action IN ('Inserted', 'Deleted', 'Updated')), 
ActionDate DATE NOT NULL, 
EmployeeID NUMBER, 
Details VARCHAR2(4000), 
FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID) 
); 
-- Thêm chỉ mục để tối ưu 
CREATE INDEX idx_actionlog_date ON ActionLog(ActionDate); 
CREATE INDEX idx_orderdetail_orderid ON OrderDetail(OrderID); 
CREATE INDEX idx_cafeorder_date ON CafeOrder(OrderDate); 
CREATE INDEX idx_shift_employeeid ON Shift(EmployeeID); 
-- -------------------------------------- 
-- II. INSERT DATA 
-- -------------------------------------- 
-- Dữ liệu cho bảng MenuItem 
INSERT INTO MenuItem VALUES (MenuItem_seq.NEXTVAL, 'Black Coffee', 30000, 'Pure black coffee', 'Available'); 
INSERT INTO MenuItem VALUES (MenuItem_seq.NEXTVAL, 'Milk Tea', 35000, 'Green tea with milk', 'Available'); 
INSERT INTO MenuItem VALUES (MenuItem_seq.NEXTVAL, 'Croissant', 25000, 'Fresh croissant', 'Available'); 
INSERT INTO MenuItem VALUES (MenuItem_seq.NEXTVAL, 'Latte', 40000, 'Espresso with milk', 'Unavailable'); -- Kiểm tra món không sẵn 
-- Dữ liệu cho bảng Employee 
INSERT INTO Employee VALUES (Employee_seq.NEXTVAL, 'Nguyen Van A', 'Manager', '0901234567', 'nva@gmail.com', TO_DATE('2023-01-01', 'YYYY-MM-DD')); 
INSERT INTO Employee VALUES (Employee_seq.NEXTVAL, 'Tran Thi B', 'Bartender', '0912345678', 'ttb@gmail.com', TO_DATE('2023-06-01', 'YYYY-MM-DD')); 
INSERT INTO Employee VALUES (Employee_seq.NEXTVAL, 'Le Van C', 'Cashier', '0923456789', 'lvc@gmail.com', TO_DATE('2024-01-01', 'YYYY-MM-DD')); 
-- Dữ liệu cho bảng Shift 
INSERT INTO Shift VALUES (Shift_seq.NEXTVAL, 1, TO_DATE('2025-04-25 08:00', 'YYYY-MM-DD HH24:MI'), TO_DATE('2025-04-25 16:00', 'YYYY-MM-DD HH24:MI'), 50000); 
INSERT INTO Shift VALUES (Shift_seq.NEXTVAL, 2, TO_DATE('2025-04-25 16:00', 'YYYY-MM-DD HH24:MI'), TO_DATE('2025-04-26 00:00', 'YYYY-MM-DD HH24:MI'), 45000); 
INSERT INTO Shift VALUES (Shift_seq.NEXTVAL, 3, TO_DATE('2025-04-25 18:00', 'YYYY-MM-DD HH24:MI'), TO_DATE('2025-04-26 02:00', 'YYYY-MM-DD HH24:MI'), 40000); 
-- Dữ liệu cho bảng CafeOrder 
INSERT INTO CafeOrder VALUES (CafeOrder_seq.NEXTVAL, 3, TO_DATE('2025-04-25 07:00', 'YYYY-MM-DD HH24:MI'), 55000, 'Confirmed'); 
INSERT INTO CafeOrder VALUES (CafeOrder_seq.NEXTVAL, 3, TO_DATE('2025-04-25 13:00', 'YYYY-MM-DD HH24:MI'), 70000, 'Completed'); 
INSERT INTO CafeOrder VALUES (CafeOrder_seq.NEXTVAL, 3, TO_DATE('2025-04-26 16:00', 'YYYY-MM-DD HH24:MI'), 30000, 'Pending'); 
-- Dữ liệu cho bảng OrderDetail 
INSERT INTO OrderDetail VALUES (OrderDetail_seq.NEXTVAL, 1, 1, 1, 30000); 
INSERT INTO OrderDetail VALUES (OrderDetail_seq.NEXTVAL, 1, 3, 1, 25000); 
INSERT INTO OrderDetail VALUES (OrderDetail_seq.NEXTVAL, 2, 2, 2, 35000); 
INSERT INTO OrderDetail VALUES (OrderDetail_seq.NEXTVAL, 3, 1, 1, 30000); 
-- Dữ liệu cho bảng OrderInvoice 
INSERT INTO OrderInvoice VALUES (OrderInvoice_seq.NEXTVAL, 1, TO_DATE('2025-04-25 06:00', 'YYYY-MM-DD HH24:MI'), 55000, 'Paid'); 
INSERT INTO OrderInvoice VALUES (OrderInvoice_seq.NEXTVAL, 2, TO_DATE('2025-04-25 13:00', 'YYYY-MM-DD HH24:MI'), 70000, 'Paid'); 
INSERT INTO OrderInvoice VALUES (OrderInvoice_seq.NEXTVAL, 3, TO_DATE('2025-04-26 16:00', 'YYYY-MM-DD HH24:MI'), 30000, 'Unpaid'); 
-- Dữ liệu cho bảng InventoryReceipt 
INSERT INTO InventoryReceipt VALUES (InventoryReceipt_seq.NEXTVAL, 1, TO_DATE('2025-04-25 16:00', 'YYYY-MM-DD HH24:MI'), 5000000, 'Coffee beans'); 
INSERT INTO InventoryReceipt VALUES (InventoryReceipt_seq.NEXTVAL, 1, TO_DATE('2025-04-26 16:00', 'YYYY-MM-DD HH24:MI'), 3000000, 'Milk and sugar'); 
-- Dữ liệu cho bảng MaterialReport 
INSERT INTO MaterialReport VALUES (MaterialReport_seq.NEXTVAL, 1, TO_DATE('2025-04-25 16:00', 'YYYY-MM-DD HH24:MI'), 'Coffee Beans', 0.5, 'Used for black coffee'); 
INSERT INTO MaterialReport VALUES (MaterialReport_seq.NEXTVAL, 1, TO_DATE('2025-04-26 15:00', 'YYYY-MM-DD HH24:MI'), 'Milk', 0.4, 'Used for milk tea'); 
INSERT INTO MaterialReport VALUES (MaterialReport_seq.NEXTVAL, 1, TO_DATE('2025-04-26 17:00', 'YYYY-MM-DD HH24:MI'), 'Flour', 0.1, 'Used for croissant'); 
-- Dữ liệu cho bảng Payroll 
INSERT INTO Payroll VALUES (Payroll_seq.NEXTVAL, 1, 1, 8, 400000, TO_DATE('2025-04-25 22:00', 'YYYY-MM-DD HH24:MI')); 
INSERT INTO Payroll VALUES (Payroll_seq.NEXTVAL, 2, 2, 8, 360000, TO_DATE('2025-04-25 22:00', 'YYYY-MM-DD HH24:MI')); 
INSERT INTO Payroll VALUES (Payroll_seq.NEXTVAL, 3, 3, 8, 320000, TO_DATE('2025-04-26 22:00', 'YYYY-MM-DD HH24:MI')); 
-- -------------------------------------- 
-- III. CREATE TRIGGERS 
-- -------------------------------------- 
-- Trigger kiểm tra định dạng khi thêm món 
/* 
- ItemName: không rỗng và chỉ chứa chữ, số, hoặc khoảng trắng, không chứa kí tự đặc biệt 
- Price: >0 
- Description: không được để trống 
*/ 
CREATE OR REPLACE TRIGGER CheckMenuItemFormat 
BEFORE INSERT OR UPDATE ON MenuItem 
FOR EACH ROW 
BEGIN 
-- Kiểm tra ItemName không rỗng và đúng định dạng 
IF :NEW.ItemName IS NULL OR LENGTH(TRIM(:NEW.ItemName)) = 0 THEN 
RAISE_APPLICATION_ERROR(-20001, 'Error: ItemName cannot be empty.'); 
END IF; 
IF NOT REGEXP_LIKE(:NEW.ItemName, '^[a-zA-Z0-9 ]+$') THEN 
RAISE_APPLICATION_ERROR(-20002, 'Error: ItemName can only contain letters, numbers, and spaces.'); 
END IF; 
-- Kiểm tra Price > 0 
IF :NEW.Price <= 0 THEN 
RAISE_APPLICATION_ERROR(-20003, 'Error: Price must be greater than 0.'); 
END IF; 
-- Kiểm tra Description không rỗng 
IF :NEW.Description IS NULL OR LENGTH(TRIM(:NEW.Description)) = 0 THEN 
RAISE_APPLICATION_ERROR(-20004, 'Error: Description cannot be empty.'); 
END IF; 
END; 
/ 
-- Trigger kiểm tra món có sẵn trước khi thêm vào đơn hàng 
CREATE OR REPLACE TRIGGER CheckMenuItemAvailability 
BEFORE INSERT ON OrderDetail 
FOR EACH ROW 
DECLARE 
v_status VARCHAR2(20); 
BEGIN 
SELECT Status INTO v_status 
FROM MenuItem 
WHERE MenuItemID = :NEW.MenuItemID; 
IF v_status = 'Unavailable' THEN 
RAISE_APPLICATION_ERROR(-20005, 'Menu item is unavailable.'); 
END IF; 
END; 
/ 
-- Trigger tính tổng tiền đơn hàng 
CREATE OR REPLACE TRIGGER CalculateOrderTotal 
AFTER INSERT OR UPDATE ON OrderDetail 
FOR EACH ROW 
DECLARE 
v_total NUMBER(10,2); 
BEGIN 
SELECT SUM(UnitPrice * Quantity) INTO v_total 
FROM OrderDetail 
WHERE OrderID = :NEW.OrderID; 
UPDATE CafeOrder 
SET TotalAmount = v_total 
WHERE OrderID = :NEW.OrderID; 
END; 
/ 
-- Trigger tự động tính lương khi thêm ca làm việc 
CREATE OR REPLACE TRIGGER CalculatePayroll 
AFTER INSERT ON Shift 
FOR EACH ROW 
DECLARE 
v_hours NUMBER(5,2); 
BEGIN 
v_hours := (TO_DATE(TO_CHAR(:NEW.EndTime, 'YYYY-MM-DD HH24:MI'), 'YYYY-MM-DD HH24:MI') 
- TO_DATE(TO_CHAR(:NEW.StartTime, 'YYYY-MM-DD HH24:MI'), 'YYYY-MM-DD HH24:MI')) * 24; 
INSERT INTO Payroll (PayrollID, EmployeeID, ShiftID, TotalHours, TotalSalary, PayrollDate) 
VALUES (Payroll_seq.NEXTVAL, :NEW.EmployeeID, :NEW.ShiftID, v_hours, v_hours * :NEW.SalaryPerHour, TRUNC(:NEW.EndTime)); 
END; 
/ 
-- Trigger kiểm tra tồn kho trước khi thêm đơn hàng 
CREATE OR REPLACE TRIGGER CheckInventoryBeforeOrder 
BEFORE INSERT ON OrderDetail 
FOR EACH ROW 
DECLARE 
v_material_name VARCHAR2(100); 
v_quantity_needed NUMBER; 
v_quantity_available NUMBER; 
BEGIN 
CASE :NEW.MenuItemID 
WHEN 1 THEN v_material_name := 'Coffee Beans'; v_quantity_needed := :NEW.Quantity * 0.1; -- 100g/món 
WHEN 2 THEN v_material_name := 'Milk'; v_quantity_needed := :NEW.Quantity * 0.2; -- 200ml/món 
WHEN 3 THEN v_material_name := 'Flour'; v_quantity_needed := :NEW.Quantity * 0.05; -- 50g/món 
ELSE RAISE_APPLICATION_ERROR(-20006, 'Unknown menu item.'); 
END CASE; 
SELECT SUM(QuantityUsed) INTO v_quantity_available 
FROM MaterialReport 
WHERE MaterialName = v_material_name; 
IF v_quantity_available IS NULL THEN v_quantity_available := 0; END IF; 
IF v_quantity_needed > v_quantity_available THEN 
RAISE_APPLICATION_ERROR(-20007, 'Insufficient ' || v_material_name || '. Needed: ' || v_quantity_needed || ', Available: ' || v_quantity_available); 
END IF; 
END; 
/ 
-- Trigger ghi log hành động cho MenuItem 
CREATE OR REPLACE TRIGGER LogMenuItemActions 
AFTER INSERT OR UPDATE OR DELETE ON MenuItem 
FOR EACH ROW 
BEGIN 
IF INSERTING THEN 
INSERT INTO ActionLog (LogID, TableName, RecordID, Action, ActionDate, EmployeeID, Details) 
VALUES (ActionLog_seq.NEXTVAL, 'MenuItem', :NEW.MenuItemID, 'Inserted', SYSDATE, NULL, 
'ItemName: ' || :NEW.ItemName || ', Price: ' || :NEW.Price); 
ELSIF UPDATING THEN 
INSERT INTO ActionLog (LogID, TableName, RecordID, Action, ActionDate, EmployeeID, Details) 
VALUES (ActionLog_seq.NEXTVAL, 'MenuItem', :NEW.MenuItemID, 'Updated', SYSDATE, NULL, 
'Old: ItemName=' || :OLD.ItemName || ', Price=' || :OLD.Price || 
'; New: ItemName=' || :NEW.ItemName || ', Price=' || :NEW.Price); 
ELSIF DELETING THEN 
-- Ghi log hành động xóa 
INSERT INTO ActionLog (LogID, TableName, RecordID, Action, ActionDate, EmployeeID, Details) 
VALUES (ActionLog_seq.NEXTVAL, 'MenuItem', :OLD.MenuItemID, 'Deleted', SYSDATE, NULL, 
'ItemName: ' || :OLD.ItemName || ', Price: ' || :OLD.Price); 
-- Cảnh báo (mô phỏng thông báo) 
RAISE_APPLICATION_ERROR(-20002, 'Warning: MenuItem ' || :OLD.ItemName || ' (ID: ' || :OLD.MenuItemID || ') is being deleted.'); 
END IF; 
END; 
/ 
-- Trigger ghi log hành động cho Employee 
CREATE OR REPLACE TRIGGER LogEmployeeActions 
AFTER INSERT OR UPDATE OR DELETE ON Employee 
FOR EACH ROW 
BEGIN 
IF INSERTING THEN 
INSERT INTO ActionLog (LogID, TableName, RecordID, Action, ActionDate, EmployeeID, Details) 
VALUES (ActionLog_seq.NEXTVAL, 'Employee', :NEW.EmployeeID, 'Inserted', SYSDATE, :NEW.EmployeeID, 
'FullName: ' || :NEW.FullName || ', Role: ' || :NEW.Role); 
ELSIF UPDATING THEN 
INSERT INTO ActionLog (LogID, TableName, RecordID, Action, ActionDate, EmployeeID, Details) 
VALUES (ActionLog_seq.NEXTVAL, 'Employee', :NEW.EmployeeID, 'Updated', SYSDATE, :NEW.EmployeeID, 
'Old: FullName=' || :OLD.FullName || ', Role=' || :OLD.Role || 
'; New: FullName=' || :NEW.FullName || ', Role=' || :NEW.Role); 
ELSIF DELETING THEN 
-- Ghi log hành động xóa 
INSERT INTO ActionLog (LogID, TableName, RecordID, Action, ActionDate, EmployeeID, Details) 
VALUES (ActionLog_seq.NEXTVAL, 'Employee', :OLD.EmployeeID, 'Deleted', SYSDATE, :OLD.EmployeeID, 
'FullName: ' || :OLD.FullName || ', Role: ' || :OLD.Role); 
-- Cảnh báo 
RAISE_APPLICATION_ERROR(-20003, 'Warning: Employee ' || :OLD.FullName || ' (ID: ' || :OLD.EmployeeID || ') is being deleted.'); 
END IF; 
END; 
/ 
-- Trigger ghi log hành động cho InventoryReceipt 
CREATE OR REPLACE TRIGGER LogInventoryReceiptActions 
AFTER INSERT OR UPDATE OR DELETE ON InventoryReceipt 
FOR EACH ROW 
BEGIN 
IF INSERTING THEN 
INSERT INTO ActionLog (LogID, TableName, RecordID, Action, ActionDate, EmployeeID, Details) 
VALUES (ActionLog_seq.NEXTVAL, 'InventoryReceipt', :NEW.ReceiptID, 'Inserted', SYSDATE, :NEW.EmployeeID, 
'TotalAmount: ' || :NEW.TotalAmount || ', Description: ' || :NEW.Description); 
ELSIF UPDATING THEN 
INSERT INTO ActionLog (LogID, TableName, RecordID, Action, ActionDate, EmployeeID, Details) 
VALUES (ActionLog_seq.NEXTVAL, 'InventoryReceipt', :NEW.ReceiptID, 'Updated', SYSDATE, :NEW.EmployeeID, 
'Old: TotalAmount=' || :OLD.TotalAmount || ', Description=' || :OLD.Description || 
'; New: TotalAmount=' || :NEW.TotalAmount || ', Description=' || :NEW.Description); 
ELSIF DELETING THEN 
-- Ghi log hành động xóa 
INSERT INTO ActionLog (LogID, TableName, RecordID, Action, ActionDate, EmployeeID, Details) 
VALUES (ActionLog_seq.NEXTVAL, 'InventoryReceipt', :OLD.ReceiptID, 'Deleted', SYSDATE, :OLD.EmployeeID, 
'TotalAmount: ' || :OLD.TotalAmount || ', Description: ' || :OLD.Description); 
-- Cảnh báo 
RAISE_APPLICATION_ERROR(-20004, 'Warning: InventoryReceipt (ID: ' || :OLD.ReceiptID || ') is being deleted.'); 
END IF; 
END; 
/ 
-- -------------------------------------- 
-- IV. CREATE TABLES + SEQUENCES + INDEX 
-- -------------------------------------- 
-- View tổng hợp lịch sử hành động 
CREATE OR REPLACE VIEW ActionLogSummary AS 
SELECT TableName, Action, COUNT(*) AS ActionCount, MIN(ActionDate) AS FirstAction, MAX(ActionDate) AS LastAction 
FROM ActionLog 
GROUP BY TableName, Action; 
-- View báo cáo sử dụng nguyên liệu 
CREATE OR REPLACE VIEW MaterialUsageReport AS 
SELECT MaterialName, SUM(QuantityUsed) AS TotalUsed, COUNT(DISTINCT ReportDate) AS DaysUsed 
FROM MaterialReport 
GROUP BY MaterialName;

select * from MenuItem;

ALTER TRIGGER LOGMENUITEMACTIONS DISABLE;
ALTER TRIGGER LOGMENUITEMACTIONS ENABLE;

DESC MenuItem;

