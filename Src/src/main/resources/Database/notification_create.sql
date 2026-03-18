-- 1. Xóa bảng cũ (nếu có)
-- Lưu ý: Việc này sẽ xóa toàn bộ dữ liệu thông báo đang có trong bảng!
DROP TABLE IF EXISTS NOTIFICATION;

-- 2. Tạo lại bảng mới theo đúng sơ đồ
CREATE TABLE NOTIFICATION (
    Not_id INT AUTO_INCREMENT PRIMARY KEY,       -- Khóa chính, tự động tăng
    Not_title VARCHAR(255) NOT NULL,             -- Tiêu đề thông báo
    Not_description TEXT,                        -- Nội dung chi tiết (dùng TEXT để chứa được chuỗi dài)
    Not_date DATETIME DEFAULT CURRENT_TIMESTAMP, -- Ngày giờ thông báo (mặc định lấy giờ hiện tại)
    Not_isRead TINYINT(1) DEFAULT 0,             -- Trạng thái đọc: 0 là chưa đọc, 1 là đã đọc
    User_id INT NOT NULL,                        -- Khóa ngoại liên kết với bảng USER
    
    -- Thiết lập ràng buộc khóa ngoại
    CONSTRAINT fk_notification_user 
        FOREIGN KEY (User_id) REFERENCES USER(User_id) 
        ON DELETE CASCADE                        -- Nếu User bị xóa, thông báo của User đó cũng tự động bay màu
);