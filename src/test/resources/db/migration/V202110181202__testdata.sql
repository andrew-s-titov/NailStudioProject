INSERT INTO users (password, first_name, last_name, phone, e_mail)
VALUES ('password1', 'Andrew', 'T', '+375293000000', 'wow@gmail.com'),
       ('mrSmith', 'Brad', 'Pitt', '+18465463222', 'bp@yahoo.com'),
       ('4D4M4NTIUM', 'Logan', 'Wolverine', '+1946484888', 'hughjackman@gmail.com'),
       ('password1', 'Staff', 'Senior', '+375295055055', 'staff1@nailstudio.com');

INSERT INTO users_roles
VALUES (1, 3),
       (1, 2),
       (2, 3),
       (3, 3),
       (4, 1),
       (4, 2);

INSERT INTO records (client_id, staff_id, date, time)
VALUES (1, 4, '2021-10-18', 'NINE'),
       (1, 4, '2021-11-01', 'SEVENTEEN'),
       (3, 1, '2021-10-18', 'THIRTEEN'),
       (2, 1, '2021-12-31', 'SEVENTEEN');

COMMIT;