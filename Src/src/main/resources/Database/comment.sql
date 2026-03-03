INSERT INTO COMMENT (Com_id, Com_description, Com_date, User_id, Task_id, Previous_Com_id)
VALUES (1, 'Toi da hoan thanh giao dien dang nhap, moi nguoi review giup',
        '2026-03-03 10:00:00', 2, 1, NULL),

       (2, 'Giao dien tot, nhung can chinh lai nut dang nhap',
        '2026-03-03 10:15:00', 1, 1, 1),

       (3, 'API dang nhap da ket noi database thanh cong',
        '2026-03-04 09:00:00', 1, 2, NULL),

       (4, 'Ban nho them validate password',
        '2026-03-04 09:20:00', 3, 2, 3),

       (5, 'Database schema da hoan thanh, moi nguoi kiem tra',
        '2026-03-04 11:00:00', 3, 3, NULL),

       (6, 'Schema hop ly, co the them bang comment va status',
        '2026-03-04 11:30:00', 4, 3, 5);
