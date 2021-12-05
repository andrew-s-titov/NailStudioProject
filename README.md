**APP DESCRIPTION**

NailStudio app is a web service for creating and storing appointments to a nail salon workers. 


Any nail salon worker can have only 3 appointments for a day: at 09:00, 13:00 and 17:00.

Only registered users can use app's full functionality, therefore every user should register and log in.
A user's phone number serves as a unique login that is used for authorization.

Every user can have 3 roles: admin, staff and client.
"Client" role is granted by default after the registration. Other roles may be granted by "Admin".

*Every registered user can:*
- change credentials (name, last name, e-mail);
- get information about available time slots for a chosen salon worker for every day in 3 months from today;  
- create an appointment to a salon worker;
- browse created appointments as a client;
- delete any of his appointments.

*Every salon worker (staff) additionally can:*
- browse his appointments as a worker (to-do tasks);
- browse all clients;
- browse any client's personal information;  
- change client discount.

*Every admin additionally can:*
- browse all roles;
- get role info by id;
- get role info by name;
- grant a role for a user;
- revoke a roles of a user;
- browse all appointments;
- delete any user without active (future) appointments.


**HOW TO RUN:**
1) via IDE: src\main\java\org\itrex\App.java
2) via terminal:
   - gradle bootRun
   - gradlew bootRun
    
port: `http://localhost:8080` 
