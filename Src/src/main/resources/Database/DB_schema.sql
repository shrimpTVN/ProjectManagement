-- 1. USER Table (Profile information)
CREATE TABLE USER (
                      User_id VARCHAR(6) PRIMARY KEY,
                      User_name VARCHAR(100) NOT NULL,
                      User_dateOfBirth DATE,
                      User_gender BOOLEAN,
                      User_phoneNumber VARCHAR(10)
);

-- 2. ACCOUNT Table (Primary identity)
CREATE TABLE ACCOUNT (
                         Acc_id VARCHAR(6) PRIMARY KEY,
                         Acc_userName VARCHAR(50) NOT NULL UNIQUE,
                         Acc_password VARCHAR(255) NOT NULL,
                         User_id VARCHAR(6),
                         foreign key (User_id) references USER(User_id)
);

-- 3. PROJECT_ROLE Table (Admin, Manager, Member roles [cite: 22])
CREATE TABLE PROJECT_ROLE (
                              Role_id VARCHAR(6)  PRIMARY KEY,
                              Role_name VARCHAR(50) NOT NULL,
                              Role_description TEXT
);

-- 4. PROJECT Table
CREATE TABLE PROJECT (
                         Pro_id VARCHAR(6) PRIMARY KEY,
                         Pro_name VARCHAR(200) NOT NULL,
                         Pro_startDate DATETIME,
                         Pro_endDate DATETIME,
                         Pro_description TEXT
);

-- 5. PROJECT_JOINING (Associative table for User-Project relationship)
CREATE TABLE PROJECT_JOINING (
                                 PJo_dateJoin DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 User_id VARCHAR(6),
                                 Pro_id VARCHAR(6),
                                 Role_id VARCHAR(6),
                                 PRIMARY KEY (User_id, Pro_id, PJo_dateJoin),
                                 FOREIGN KEY (User_id) REFERENCES USER(User_id),
                                 FOREIGN KEY (Pro_id) REFERENCES PROJECT(Pro_id),
                                 FOREIGN KEY (Role_id) REFERENCES PROJECT_ROLE(Role_id)
);

-- 6. TASK_STATUS Table (To Do, In Progress, Done [cite: 21])
CREATE TABLE TASK_STATUS (
                             Sta_id VARCHAR(6) PRIMARY KEY,
                             Sta_name VARCHAR(50) NOT NULL,
                             Sta_description TEXT
);

-- 7. TASK Table
CREATE TABLE TASK (
                      Task_id VARCHAR(6) PRIMARY KEY,
                      Task_name VARCHAR(200) NOT NULL,
                      Task_description TEXT,
                      Task_startDate DATETIME,
                      Task_endDate DATETIME,
                      Pro_id VARCHAR(6),
                      User_id VARCHAR(6), -- Assigned User
                      FOREIGN KEY (Pro_id) REFERENCES PROJECT(Pro_id) ON DELETE CASCADE,
                      FOREIGN KEY (User_id) REFERENCES USER(User_id)
);

-- 8. COMMENT Table (Self-referencing for replies)
CREATE TABLE COMMENT (
                         Com_id VARCHAR(6)  PRIMARY KEY,
                         Com_description TEXT NOT NULL,
                         Com_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                         User_id VARCHAR(6),
                         Task_id VARCHAR(6),
                         Previous_Com_id VARCHAR(6), -- For 'phan_hoi' (replies)
                         FOREIGN KEY (User_id) REFERENCES USER(User_id),
                         FOREIGN KEY (Task_id) REFERENCES TASK(Task_id) ON DELETE CASCADE,
                         FOREIGN KEY (Previous_Com_id) REFERENCES COMMENT(Com_id)
);

-- 9. STATUS_UPDATING (History of task changes)
CREATE TABLE STATUS_UPDATING (
                                 StU_id VARCHAR(6) PRIMARY KEY,
                                 StU_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 StU_content TEXT,
                                 Task_id VARCHAR(6),
                                 Sta_id VARCHAR(6),
                                 FOREIGN KEY (Task_id) REFERENCES TASK(Task_id) ON DELETE CASCADE,
                                 FOREIGN KEY (Sta_id) REFERENCES TASK_STATUS(Sta_id)
);

-- 10. NOTIFICATION Table
CREATE TABLE NOTIFICATION (
                              Not_id VARCHAR(6)  PRIMARY KEY,
                              Not_description TEXT NOT NULL,
                              Not_isRead BOOLEAN DEFAULT FALSE,
                              StU_id VARCHAR(6),
                              FOREIGN KEY (StU_id) REFERENCES STATUS_UPDATING(StU_id) ON DELETE CASCADE
);