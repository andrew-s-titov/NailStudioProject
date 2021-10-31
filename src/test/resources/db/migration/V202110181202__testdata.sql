INSERT INTO users (password, first_name, last_name, phone, e_mail)
VALUES (CAST('0001' AS BINARY(160)), 'Andrew', 'T', '+375293000000', 'wow@gmail.com'),
       (CAST('0002' AS BINARY(160)), 'Brad', 'Pitt', '+18465463222', 'bp@yahoo.com'),
       (CAST('0003' AS BINARY(160)), 'Logan', 'Wolverine', '+1946484888', 'hughjackman@gmail.com');

INSERT INTO records (user_id, date, time)
VALUES (1, '2021-10-18', 'NINE'),
       (1, '2021-11-01', 'SEVENTEEN'),
       (3, '2021-10-18', 'THIRTEEN'),
       (2, '2021-12-31', 'SEVENTEEN');

INSERT INTO users_roles
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (3, 3);

COMMIT;