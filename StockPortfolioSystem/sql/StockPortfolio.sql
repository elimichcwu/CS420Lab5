DROP DATABASE IF EXISTS StockPortfolioDB;
CREATE DATABASE StockPortfolioDB;
USE StockPortfolioDB;

-- =====================================================
-- STEP 1: BARE CREATE TABLE STATEMENTS
-- No primary keys, foreign keys, or auto increment yet
-- =====================================================

CREATE TABLE Investor (
    InvestorID INT NOT NULL,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Email VARCHAR(100) NOT NULL,
    Country VARCHAR(60) NOT NULL,
    Phone VARCHAR(30)
);

CREATE TABLE BrokerageAccount (
    AccountID INT NOT NULL,
    InvestorID INT NOT NULL,
    AccountType VARCHAR(50) NOT NULL,
    BrokerName VARCHAR(100) NOT NULL,
    Balance DECIMAL(12,2) NOT NULL
);

CREATE TABLE Company (
    CompanyID INT NOT NULL,
    CompanyName VARCHAR(100) NOT NULL,
    Industry VARCHAR(80) NOT NULL,
    Country VARCHAR(60) NOT NULL,
    Headquarters VARCHAR(100),
    FoundedYear INT
);

CREATE TABLE Stock (
    StockID INT NOT NULL,
    CompanyID INT NOT NULL,
    TickerSymbol VARCHAR(10) NOT NULL,
    ExchangeName VARCHAR(50) NOT NULL,
    CurrentPrice DECIMAL(10,2) NOT NULL
);

CREATE TABLE TradeTransaction (
    TransactionID INT NOT NULL,
    AccountID INT NOT NULL,
    StockID INT NOT NULL,
    TradeDate DATE NOT NULL,
    TradeType VARCHAR(10) NOT NULL,
    Quantity INT NOT NULL,
    PricePerShare DECIMAL(10,2) NOT NULL
);

-- =====================================================
-- STEP 2: ADD PRIMARY KEYS
-- =====================================================

ALTER TABLE Investor
ADD CONSTRAINT PK_Investor PRIMARY KEY (InvestorID);

ALTER TABLE BrokerageAccount
ADD CONSTRAINT PK_BrokerageAccount PRIMARY KEY (AccountID);

ALTER TABLE Company
ADD CONSTRAINT PK_Company PRIMARY KEY (CompanyID);

ALTER TABLE Stock
ADD CONSTRAINT PK_Stock PRIMARY KEY (StockID);

ALTER TABLE TradeTransaction
ADD CONSTRAINT PK_TradeTransaction PRIMARY KEY (TransactionID);

-- =====================================================
-- STEP 3: ADD AUTO_INCREMENT AFTER PRIMARY KEYS
-- =====================================================

ALTER TABLE Investor
MODIFY InvestorID INT NOT NULL AUTO_INCREMENT;

ALTER TABLE BrokerageAccount
MODIFY AccountID INT NOT NULL AUTO_INCREMENT;

ALTER TABLE Company
MODIFY CompanyID INT NOT NULL AUTO_INCREMENT;

ALTER TABLE Stock
MODIFY StockID INT NOT NULL AUTO_INCREMENT;

ALTER TABLE TradeTransaction
MODIFY TransactionID INT NOT NULL AUTO_INCREMENT;

-- =====================================================
-- STEP 4: ADD FOREIGN KEYS
-- =====================================================

ALTER TABLE BrokerageAccount
ADD CONSTRAINT FK_BrokerageAccount_Investor
FOREIGN KEY (InvestorID) REFERENCES Investor(InvestorID);

ALTER TABLE Stock
ADD CONSTRAINT FK_Stock_Company
FOREIGN KEY (CompanyID) REFERENCES Company(CompanyID);

ALTER TABLE TradeTransaction
ADD CONSTRAINT FK_TradeTransaction_Account
FOREIGN KEY (AccountID) REFERENCES BrokerageAccount(AccountID);

ALTER TABLE TradeTransaction
ADD CONSTRAINT FK_TradeTransaction_Stock
FOREIGN KEY (StockID) REFERENCES Stock(StockID);

-- =====================================================
-- STEP 5: ADD SAMPLE DATA
-- =====================================================

INSERT INTO Investor (FirstName, LastName, Email, Country, Phone) VALUES
('Eli', 'Michel', 'eli@example.com', 'USA', '253-111-1111'),
('Maria', 'Lopez', 'maria@example.com', 'USA', '253-222-2222'),
('Carlos', 'Garcia', 'carlos@example.com', 'Mexico', '253-333-3333'),
('Ana', 'Martinez', 'ana@example.com', 'USA', '253-444-4444'),
('David', 'Smith', 'david@example.com', 'Canada', '253-555-5555');

INSERT INTO BrokerageAccount (InvestorID, AccountType, BrokerName, Balance) VALUES
(1, 'Individual', 'Fidelity', 15000.00),
(2, 'Retirement', 'Charles Schwab', 22000.00),
(3, 'Individual', 'Robinhood', 8000.00),
(4, 'Education', 'Vanguard', 12000.00),
(5, 'Individual', 'E-Trade', 18000.00);

INSERT INTO Company (CompanyName, Industry, Country, Headquarters, FoundedYear) VALUES
('Apple Inc.', 'Technology', 'USA', 'Cupertino, CA', 1976),
('Microsoft Corporation', 'Technology', 'USA', 'Redmond, WA', 1975),
('Tesla Inc.', 'Automotive', 'USA', 'Austin, TX', 2003),
('Amazon.com Inc.', 'E-Commerce', 'USA', 'Seattle, WA', 1994),
('Nike Inc.', 'Retail', 'USA', 'Beaverton, OR', 1964),
('Coca-Cola Company', 'Beverage', 'USA', 'Atlanta, GA', 1892);

INSERT INTO Stock (CompanyID, TickerSymbol, ExchangeName, CurrentPrice) VALUES
(1, 'AAPL', 'NASDAQ', 195.50),
(2, 'MSFT', 'NASDAQ', 420.25),
(3, 'TSLA', 'NASDAQ', 245.80),
(4, 'AMZN', 'NASDAQ', 180.10),
(5, 'NKE', 'NYSE', 95.75),
(6, 'KO', 'NYSE', 62.40);

INSERT INTO TradeTransaction (AccountID, StockID, TradeDate, TradeType, Quantity, PricePerShare) VALUES
(1, 1, '2026-01-05', 'BUY', 10, 190.00),
(1, 2, '2026-01-10', 'BUY', 5, 415.00),
(1, 3, '2026-01-15', 'SELL', 3, 250.00),
(2, 1, '2026-02-01', 'BUY', 20, 192.50),
(2, 4, '2026-02-05', 'BUY', 15, 178.00),
(3, 3, '2026-02-12', 'BUY', 8, 240.00),
(3, 5, '2026-02-18', 'SELL', 4, 98.00),
(4, 2, '2026-03-01', 'BUY', 12, 418.00),
(4, 6, '2026-03-05', 'BUY', 30, 61.50),
(5, 4, '2026-03-10', 'BUY', 10, 181.00),
(5, 1, '2026-03-15', 'SELL', 5, 197.00),
(5, 6, '2026-03-20', 'BUY', 25, 62.00);

-- =====================================================
-- TEST SELECTS
-- =====================================================

SELECT * FROM Investor;
SELECT * FROM BrokerageAccount;
SELECT * FROM Company;
SELECT * FROM Stock;
SELECT * FROM TradeTransaction;
