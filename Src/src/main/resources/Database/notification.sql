INSERT INTO NOTIFICATION (Not_title, Not_description, Not_date, Not_isRead, User_id)
VALUES 
    -- User 1
    ('Chào mừng hệ thống', 'Chào mừng bạn đến với phần mềm Quản lý Dự án.', '2026-03-10 08:00:00', 1, 1),
    ('Dự án mới', 'Bạn đã được thêm vào dự án Phát triển Website E-commerce.', '2026-03-11 09:15:00', 1, 1),
    ('Nhiệm vụ mới', 'Bạn được phân công task: Thiết kế Database.', '2026-03-12 10:30:00', 0, 1),
    ('Nhắc nhở Deadline', 'Task Thiết kế Database sẽ hết hạn vào ngày mai. Vui lòng cập nhật tiến độ.', '2026-03-17 14:00:00', 0, 1),

    -- User 2
    ('Cập nhật quyền hạn', 'Bạn đã được thăng cấp lên vai trò Project Manager.', '2026-03-10 08:30:00', 1, 2),
    ('Dự án mới', 'Bạn đã được thêm vào dự án Mobile App Khách hàng.', '2026-03-12 09:00:00', 1, 2),
    ('Báo cáo tuần', 'Đã đến lúc nộp báo cáo tiến độ tuần này.', '2026-03-15 16:00:00', 0, 2),
    ('Bình luận mới', 'User_1 vừa bình luận vào Task bạn đang theo dõi.', '2026-03-18 09:45:00', 0, 2),

    -- User 3
    ('Chào mừng hệ thống', 'Chào mừng bạn đến với phần mềm Quản lý Dự án.', '2026-03-10 09:00:00', 1, 3),
    ('Nhiệm vụ mới', 'Bạn được phân công task: Viết API đăng nhập.', '2026-03-13 11:20:00', 1, 3),
    ('Thay đổi trạng thái', 'Task "Viết API đăng nhập" đã được chuyển sang trạng thái IN PROGRESS.', '2026-03-14 15:10:00', 0, 3),
    ('Lỗi phát sinh', 'Phát hiện bug ở module thanh toán, vui lòng kiểm tra gấp.', '2026-03-18 10:05:00', 0, 3),

    -- User 4
    ('Thông báo họp', 'Họp Kick-off dự án mới vào lúc 2h chiều nay.', '2026-03-11 08:15:00', 1, 4),
    ('Nhiệm vụ mới', 'Bạn được phân công task: Thiết kế giao diện Login.', '2026-03-13 14:00:00', 1, 4),
    ('Hoàn thành nhiệm vụ', 'Task "Thiết kế giao diện Login" đã được duyệt thành công.', '2026-03-16 10:20:00', 0, 4),
    ('Nhắc nhở cập nhật', 'Bạn chưa cập nhật trạng thái công việc trong 3 ngày qua.', '2026-03-18 08:30:00', 0, 4),

    -- User 5
    ('Bảo trì hệ thống', 'Hệ thống sẽ bảo trì vào 00:00 đêm nay. Vui lòng lưu công việc.', '2026-03-14 17:00:00', 1, 5),
    ('Dự án mới', 'Bạn đã được thêm vào dự án Nâng cấp Server.', '2026-03-15 09:00:00', 1, 5),
    ('Nhiệm vụ mới', 'Bạn được phân công task: Cấu hình môi trường CentOS.', '2026-03-16 08:45:00', 0, 5),
    ('Thông báo khen thưởng', 'Dự án Nâng cấp Server hoàn thành xuất sắc trước thời hạn.', '2026-03-18 11:30:00', 0, 5);