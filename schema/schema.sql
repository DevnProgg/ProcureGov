drop database procure_gov;
CREATE DATABASE IF NOT EXISTS procure_gov;
USE procure_gov;

CREATE TABLE Roles (
    role_id         INT             NOT NULL AUTO_INCREMENT,
    name            VARCHAR(50)     NOT NULL,           
    privilege_level VARCHAR(50)     NOT NULL,
    PRIMARY KEY (role_id),
    UNIQUE KEY uq_role_name (name)
);

CREATE TABLE Employees (
    employee_id     INT             NOT NULL AUTO_INCREMENT,
    full_names      VARCHAR(150)    NOT NULL,
    phone_number    VARCHAR(20)     NULL,
    gender          VARCHAR(20)     NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (employee_id)
);

CREATE TABLE Suppliers (
    supplier_id     INT             NOT NULL AUTO_INCREMENT,
    business_name   VARCHAR(150)    NOT NULL,
    email           VARCHAR(150)    NOT NULL,
    address         VARCHAR(255)    NULL,
    phone_number    VARCHAR(20)     NULL,
    reg_number      VARCHAR(50)     NULL,              
    PRIMARY KEY (supplier_id),
    UNIQUE KEY uq_supplier_email (email)
);


CREATE TABLE Users (
    user_id         INT             NOT NULL AUTO_INCREMENT,
    employee_id     INT             NULL,              
    supplier_id     INT             NULL,              
    PRIMARY KEY (user_id),
    CONSTRAINT fk_users_employee FOREIGN KEY (employee_id) REFERENCES Employees(employee_id),
    CONSTRAINT fk_users_supplier FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id),
    CONSTRAINT chk_user_type CHECK (
        (employee_id IS NOT NULL AND supplier_id IS NULL) OR
        (employee_id IS NULL     AND supplier_id IS NOT NULL)
    )
);

CREATE TABLE Accounts (
    account_id      INT             NOT NULL AUTO_INCREMENT,
    role_id         INT             NOT NULL,
    user_id INT NOT NULL,
    username        VARCHAR(150)    NOT NULL,
    password_hash   VARCHAR(255)     NOT NULL,
    active_status   TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (account_id),
    UNIQUE KEY uq_username (username),
    UNIQUE KEY uq_user_id (user_id),
    CONSTRAINT fk_accounts_role FOREIGN KEY (role_id) REFERENCES Roles(role_id),
    CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE TenderOffers (
    tender_id           INT             NOT NULL AUTO_INCREMENT,
    reference_number    VARCHAR(20)     NOT NULL,      
    title               VARCHAR(255)    NOT NULL,
    description         TEXT            NOT NULL,
    publish_datetime    DATETIME        NULL,           
    expiry_datetime     DATETIME        NOT NULL,      
    created_by          INT             NOT NULL,       
    status              ENUM(
                            'DRAFT',
                            'OPEN',
                            'CLOSED',
                            'UNDER_EVALUATION',
                            'EVALUATED',
                            'AWARDED'
                        )               NOT NULL DEFAULT 'DRAFT',
    category            ENUM(
                            'Construction',
                            'Roads',
                            'Electrical',
                            'Plumbing',
                            'General Services'
                        )               NOT NULL,
    estimated_value     DECIMAL(15,2)   NOT NULL,
    notice_file_path    VARCHAR(500)    NULL,           -- 
    PRIMARY KEY (tender_id),
    UNIQUE KEY uq_reference_number (reference_number),
    CONSTRAINT fk_tenders_created_by FOREIGN KEY (created_by) REFERENCES Employees(employee_id)
);

CREATE TABLE TenderBids (
    bid_id                  INT             NOT NULL AUTO_INCREMENT,
    tender_id               INT             NOT NULL,
    supplier_id             INT             NOT NULL,  
    price                   DECIMAL(15,2)   NOT NULL,
    compliance_statement    VARCHAR(600)    NOT NULL,   
    document_file_path      VARCHAR(500)    NULL,      
    delivery_days           INT             NOT NULL,   
    submitted_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (bid_id),
    UNIQUE KEY uq_one_bid_per_tender (tender_id, supplier_id),
    CONSTRAINT fk_bids_tender   FOREIGN KEY (tender_id)   REFERENCES TenderOffers(tender_id),
    CONSTRAINT fk_bids_supplier FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id)
);

CREATE TABLE EvaluatorBidLogs (
    log_id                      INT             NOT NULL AUTO_INCREMENT,
    bid_id                      INT             NOT NULL,
    employee_id                 INT             NOT NULL,
    price_score                 DECIMAL(6,2)    NOT NULL,  
    technical_compliance_score  DECIMAL(6,2)    NOT NULL,
    delivery_timeline_score     DECIMAL(6,2)    NOT NULL, 
    weighted_total              DECIMAL(6,2)    NOT NULL,   
    evaluated_at                TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (log_id),
    UNIQUE KEY uq_evaluator_bid (bid_id, employee_id),
    CONSTRAINT fk_logs_bid      FOREIGN KEY (bid_id)      REFERENCES TenderBids(bid_id),
    CONSTRAINT fk_logs_employee FOREIGN KEY (employee_id) REFERENCES Employees(employee_id)
);

CREATE TABLE Awards (
    award_id            INT             NOT NULL AUTO_INCREMENT,
    tender_id           INT             NOT NULL,
    bid_id              INT             NOT NULL,      
    awarded_value       DECIMAL(15,2)   NOT NULL,     
    officer_justification TEXT          NOT NULL,
    award_date          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    awarded_by          INT             NOT NULL,     
    PRIMARY KEY (award_id),
    UNIQUE KEY uq_one_award_per_tender (tender_id),
    CONSTRAINT fk_awards_tender     FOREIGN KEY (tender_id)  REFERENCES TenderOffers(tender_id),
    CONSTRAINT fk_awards_bid        FOREIGN KEY (bid_id)     REFERENCES TenderBids(bid_id),
    CONSTRAINT fk_awards_awarded_by FOREIGN KEY (awarded_by) REFERENCES Employees(employee_id)
);

-- Bid Evaluations table
CREATE TABLE BidEvaluations (
                                evaluation_id INT PRIMARY KEY AUTO_INCREMENT,
                                bid_id INT NOT NULL,
                                tender_id INT NOT NULL,
                                evaluator_id INT NOT NULL,
                                price_score DECIMAL(5,2) NOT NULL,
                                technical_score DECIMAL(5,2) NOT NULL,
                                delivery_score DECIMAL(5,2) NOT NULL,
                                weighted_total DECIMAL(5,2) NOT NULL,
                                evaluated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (bid_id) REFERENCES TenderBids(bid_id),
                                FOREIGN KEY (tender_id) REFERENCES TenderOffers(tender_id),
                                FOREIGN KEY (evaluator_id) REFERENCES Employees(employee_id),
                                UNIQUE KEY unique_evaluation (bid_id, evaluator_id)
);

-- Email Sender Task Message Queue
CREATE TABLE EmailMessageQueue(
    email_id int primary key auto_increment,
    supplier_email varchar(100) not null,
    subject varchar(100) not null,
    email_body varchar(255) not null,
    queued_at timestamp default current_timestamp,
    sent boolean default false,
    error_message varchar(255),
    retry_count int default 0
);

CREATE OR REPLACE VIEW view_employee_data AS
SELECT
    a.account_id,
    a.username,
    a.active_status,
    r.name            AS role_name,
    r.privilege_level,
    u.user_id,
    e.employee_id,
    e.full_names,
    e.phone_number,
    e.gender,
    e.created_at
FROM Accounts      a
JOIN Roles         r ON r.role_id    = a.role_id
JOIN Users         u ON u.user_id = a.user_id
JOIN Employees     e ON e.employee_id = u.employee_id
WHERE u.employee_id IS NOT NULL;

CREATE OR REPLACE VIEW view_supplier_data AS
SELECT
    a.account_id,
    a.username,
    a.active_status,
    r.name            AS role_name,
    u.user_id,
    s.supplier_id,
    s.business_name,
    s.email,
    s.address,
    s.phone_number,
    s.reg_number
FROM Accounts      a
JOIN Roles         r ON r.role_id    = a.role_id
JOIN Users         u ON u.user_id = a.user_id
JOIN Suppliers     s ON s.supplier_id = u.supplier_id
WHERE u.supplier_id IS NOT NULL;

INSERT INTO Roles (name, privilege_level) VALUES
('PROCUREMENT_OFFICER',   'STAFF'),
('BOARD_MEMBER', 'STAFF'),
('SUPPLIER',  'EXTERNAL');

-- Seed Data for STAFF Users
-- 1 Procurement Officer and 2 Board Members

-- Insert Employees
INSERT INTO Employees (full_names, phone_number, gender) VALUES
                                                             ('Khalapa Mokoena', '+266 5123 4567', 'Male'),
                                                             ('Cookie Mofokeng', '+266 5234 5678', 'Female'),
                                                             ('Dolphie Ntsoele', '+266 5345 6789', 'Male');

-- Insert Users
INSERT INTO Users (employee_id, supplier_id) VALUES
                                                 (1, NULL),
                                                 (2, NULL),
                                                 (3, NULL);

INSERT INTO Accounts (role_id, user_id, username, password_hash, active_status) VALUES
-- Khalapa Mokoena - Procurement Officer
(1, 1, 'khalapa.mokoena@procure.gov.ls', 'NgLYGeukSgG7DkBtvEvLHA==:H08Q9pIdYCQu8/WmueoNFpzpf2OCrzaOetyrKgqJ7ag=', 1),

-- Cookie Mofokeng - Board Member
(2, 2, 'cookie.mofokeng@procure.gov.ls', 'DKs7tM7lgur31roIz8RBMA==:yh8zXnWvTCbA6LgkzS1NmKbODNmxYxE3uxkFm+NgRwM=', 1),

-- Dolphie Ntsoele - Board Member
(2, 3, 'dolphie.ntsoele@procure.gov.ls', 'TeltuL8+04lkZcZTae3bpA==:tU5ymx9nOZxLmGiKf4sof6v4HwoTF4NT+Z0rvRzThn0=', 1);


