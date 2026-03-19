INSERT INTO PROJECT (Pro_id, Pro_name, Pro_startDate, Pro_endDate, Pro_description)
VALUES
-- User 1 (Doan Tai Loc) - Admin of Pro 1-5
(1,  'Website Quan Ly Gym',           '2026-03-01 08:00:00', '2026-06-01 17:00:00', 'Phat trien he thong quan ly phong gym cho admin va nhan vien'),
(2,  'He thong Quan ly Cong viec',    '2026-03-05 09:00:00', '2026-07-15 17:00:00', 'Ung dung quan ly task va du an cho nhom'),
(3,  'Ung dung Quan ly Thu vien',     '2026-05-01 08:00:00', '2026-10-01 17:00:00', 'Quan ly sach va muon tra trong thu vien'),
(4,  'Ung dung Quan ly Sinh vien',    '2026-04-01 07:30:00', '2026-08-01 17:00:00', 'Quan ly thong tin sinh vien va lop hoc'),
(5,  'Website Ban hang Online',       '2026-03-15 09:00:00', '2026-09-01 18:00:00', 'Xay dung website thuong mai dien tu co gio hang va thanh toan'),
-- User 2 (Vo Le Tu Anh) - Admin of Pro 6-10
(6,  'He thong Dat ve May bay',       '2026-06-01 09:00:00', '2026-11-01 17:00:00', 'Ung dung dat ve may bay va quan ly chuyen bay'),
(7,  'Website Quan ly Nha hang',      '2026-07-01 08:00:00', '2026-12-01 17:00:00', 'He thong quan ly don hang va ban an trong nha hang'),
(8,  'Ung dung Hoc truc tuyen',       '2026-08-01 07:30:00', '2026-12-15 17:00:00', 'Nen tang hoc tap truc tuyen cho sinh vien'),
(9,  'He thong Quan ly Kho hang',     '2026-09-01 09:00:00', '2027-01-01 17:00:00', 'Quan ly ton kho va don hang trong kho'),
(10, 'Ung dung Quan ly Benh vien',    '2026-10-01 08:00:00', '2027-02-01 17:00:00', 'Quan ly benh nhan, lich kham va ho so y te'),
-- User 3 (Tran Van Nghia) - Admin of Pro 11-15
(11, 'He thong Quan ly Nhan su',      '2026-03-01 08:00:00', '2026-07-01 17:00:00', 'Quan ly ho so nhan vien, luong va nghi phep'),
(12, 'Ung dung Dat kham Bac si',      '2026-04-01 08:00:00', '2026-08-01 17:00:00', 'Dat lich kham bac si truc tuyen'),
(13, 'Website Tin tuc Cong nghe',     '2026-05-01 08:00:00', '2026-09-01 17:00:00', 'Trang tin tuc cong nghe va review san pham'),
(14, 'He thong Quan ly Tai san',      '2026-06-01 08:00:00', '2026-10-01 17:00:00', 'Quan ly tai san va thiet bi cua cong ty'),
(15, 'Ung dung Giao do an',           '2026-07-01 08:00:00', '2026-11-01 17:00:00', 'Ung dung dat va giao do an truc tuyen'),
-- User 4 (Dao Quoc Viet) - Admin of Pro 16-20
(16, 'He thong Quan ly Du an',        '2026-03-10 08:00:00', '2026-07-10 17:00:00', 'Quan ly tien do va nguon luc du an'),
(17, 'Website Tim viec lam',          '2026-04-10 08:00:00', '2026-08-10 17:00:00', 'Nen tang ket noi nha tuyen dung va ung vien'),
(18, 'Ung dung Quan ly Chi tieu',     '2026-05-10 08:00:00', '2026-09-10 17:00:00', 'Theo doi thu chi ca nhan va gia dinh'),
(19, 'He thong Quan ly Khach san',    '2026-06-10 08:00:00', '2026-10-10 17:00:00', 'Quan ly phong, dat phong va dich vu khach san'),
(20, 'Website Khoa hoc truc tuyen',   '2026-07-10 08:00:00', '2026-11-10 17:00:00', 'Nen tang hoc va day truc tuyen'),

-- User 5 (Nguyen Ngoc Duc Phat) - Admin of Pro 21-25
(21, 'He thong Quan ly Xe',           '2026-03-20 08:00:00', '2026-07-20 17:00:00', 'Quan ly dich vu sua chua va bao duong xe'),
(22, 'Ung dung Mua sam Online',       '2026-04-20 08:00:00', '2026-08-20 17:00:00', 'Nen tang mua sam va thanh toan truc tuyen'),
(23, 'Website Bao cao Tai chinh',     '2026-05-20 08:00:00', '2026-09-20 17:00:00', 'He thong bao cao va phan tich tai chinh'),
(24, 'He thong Quan ly Truong hoc',   '2026-06-20 08:00:00', '2026-10-20 17:00:00', 'Quan ly lop hoc, giao vien va hoc sinh'),
(25, 'Ung dung Xem phim truc tuyen',  '2026-07-20 08:00:00', '2026-11-20 17:00:00', 'Nen tang xem phim va truyen hinh truc tuyen'),

-- User 6 (Le Minh Khoa) - Admin of Pro 26-30
(26, 'He thong Quan ly Toa nha',      '2026-03-01 08:00:00', '2026-07-01 17:00:00', 'Quan ly can ho, thue va dich vu toa nha'),
(27, 'Ung dung Giao thong Thong minh','2026-04-01 08:00:00', '2026-08-01 17:00:00', 'He thong giam sat va dieu phoi giao thong'),
(28, 'Website Thuong mai Quoc te',    '2026-05-01 08:00:00', '2026-09-01 17:00:00', 'Nen tang thuong mai dien tu quoc te'),
(29, 'He thong CRM Khach hang',       '2026-06-01 08:00:00', '2026-10-01 17:00:00', 'Quan ly quan he va cham soc khach hang'),
(30, 'Ung dung Suc khoe ca nhan',     '2026-07-01 08:00:00', '2026-11-01 17:00:00', 'Theo doi suc khoe, the duc va dinh duong'),

-- User 7 (Pham Thi Hoa) - Admin of Pro 31-35
(31, 'He thong Quan ly Su kien',      '2026-03-05 08:00:00', '2026-07-05 17:00:00', 'Quan ly to chuc su kien va hoi nghi'),
(32, 'Website Thiet ke Noi that',     '2026-04-05 08:00:00', '2026-08-05 17:00:00', 'Nen tang thiet ke noi that truc tuyen'),
(33, 'Ung dung Dich vu Giat ui',      '2026-05-05 08:00:00', '2026-09-05 17:00:00', 'Dat lich va quan ly dich vu giat la'),
(34, 'He thong Quan ly Phong kham',   '2026-06-05 08:00:00', '2026-10-05 17:00:00', 'Quan ly benh nhan va lich kham bac si'),
(35, 'Website Rao vat Online',        '2026-07-05 08:00:00', '2026-11-05 17:00:00', 'Nen tang mua ban do cu va rao vat'),

-- User 8 (Nguyen Van Hung) - Admin of Pro 36-40
(36, 'He thong Quan ly Nong trai',    '2026-03-10 08:00:00', '2026-07-10 17:00:00', 'Quan ly san xuat va tieu thu nong san'),
(37, 'Ung dung Dat tour Du lich',     '2026-04-10 08:00:00', '2026-08-10 17:00:00', 'Dat tour du lich va khach san truc tuyen'),
(38, 'Website Ban le Thoi trang',     '2026-05-10 08:00:00', '2026-09-10 17:00:00', 'Cua hang thoi trang truc tuyen'),
(39, 'He thong Quan ly Nha may',      '2026-06-10 08:00:00', '2026-10-10 17:00:00', 'Giam sat san xuat va kiem soat chat luong'),
(40, 'Ung dung Hoc ngoai ngu',        '2026-07-10 08:00:00', '2026-11-10 17:00:00', 'Hoc ngoai ngu qua video va bai tap tuong tac'),

-- User 9 (Tran Thi Mai) - Admin of Pro 41-45
(41, 'He thong Quan ly Chuoi cung ung','2026-03-15 08:00:00','2026-07-15 17:00:00', 'Quan ly nha cung cap, kho va van chuyen'),
(42, 'Ung dung Quan ly Den dien',     '2026-04-15 08:00:00', '2026-08-15 17:00:00', 'Giam sat tieu thu dien va tiet kiem nang luong'),
(43, 'Website Dat ban Nha hang',      '2026-05-15 08:00:00', '2026-09-15 17:00:00', 'Dat ban an va dat do an truc tiep tu nha hang'),
(44, 'He thong Quan ly Buu dien',     '2026-06-15 08:00:00', '2026-10-15 17:00:00', 'Quan ly gui nhan buu pham va theo doi van chuyen'),
(45, 'Ung dung Thue xe tu lai',       '2026-07-15 08:00:00', '2026-11-15 17:00:00', 'Dat va quan ly dich vu thue xe tu lai'),

-- User 10 (Bui Quang Huy) - Admin of Pro 46-50
(46, 'He thong Quan ly Bao hiem',     '2026-03-20 08:00:00', '2026-07-20 17:00:00', 'Quan ly hop dong va yeu cau boi thuong bao hiem'),
(47, 'Ung dung Quan ly Tap gym',      '2026-04-20 08:00:00', '2026-08-20 17:00:00', 'Quan ly hoi vien, giao an tap luyen va diem danh'),
(48, 'Website Thuong mai B2B',        '2026-05-20 08:00:00', '2026-09-20 17:00:00', 'Nen tang ket noi doanh nghiep va cung ung hang hoa'),
(49, 'He thong Quan ly Sach giao khoa','2026-06-20 08:00:00','2026-10-20 17:00:00', 'Quan ly phan phoi va dat mua sach giao khoa'),
(50, 'Ung dung Quan ly Nha tro',      '2026-07-20 08:00:00', '2026-11-20 17:00:00', 'Quan ly phong tro, hop dong thue va thu tien');
