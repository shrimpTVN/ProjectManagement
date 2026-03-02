INSERT INTO COMMENT (Com_id, Com_description, Com_date, User_id, Task_id, Previous_Com_id)
VALUES ('C00001', 'Toi da hoan thanh giao dien dang nhap, moi nguoi review giup',
        '2026-03-03 10:00:00', 'U00002', 'T00001', NULL),

       ('C00002', 'Giao dien tot, nhung can chinh lai nut dang nhap',
        '2026-03-03 10:15:00', 'U00001', 'T00001', 'C00001'),

       ('C00003', 'API dang nhap da ket noi database thanh cong',
        '2026-03-04 09:00:00', 'U00001', 'T00002', NULL),

       ('C00004', 'Ban nho them validate password',
        '2026-03-04 09:20:00', 'U00003', 'T00002', 'C00003'),

       ('C00005', 'Database schema da hoan thanh, moi nguoi kiem tra',
        '2026-03-04 11:00:00', 'U00003', 'T00003', NULL),

       ('C00006', 'Schema hop ly, co the them bang comment va status',
        '2026-03-04 11:30:00', 'U00004', 'T00003', 'C00005');