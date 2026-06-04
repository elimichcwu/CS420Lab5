# Relational Schema - Stock Portfolio Management System

## INVESTOR
Investor(**InvestorID**, FirstName, LastName, Email, Country, Phone)

- Primary Key: InvestorID
- Unique: Email
- Required: FirstName, LastName, Email, Country
- Optional: Phone

## BROKERAGEACCOUNT
BrokerageAccount(**AccountID**, InvestorID, AccountType, BrokerName, Balance)

- Primary Key: AccountID
- Foreign Key: InvestorID references Investor(InvestorID)
- Required: InvestorID, AccountType, BrokerName, Balance
- Constraint: Balance must be greater than or equal to 0

## COMPANY
Company(**CompanyID**, CompanyName, Industry, Country, Headquarters, FoundedYear)

- Primary Key: CompanyID
- Unique: CompanyName
- Required: CompanyName, Industry, Country
- Optional: Headquarters, FoundedYear
- Constraint: FoundedYear must be null or between 1800 and 2100

## STOCK
Stock(**StockID**, CompanyID, TickerSymbol, ExchangeName, CurrentPrice)

- Primary Key: StockID
- Foreign Key: CompanyID references Company(CompanyID)
- Unique: TickerSymbol
- Required: CompanyID, TickerSymbol, ExchangeName, CurrentPrice
- Constraint: CurrentPrice must be greater than or equal to 0

## TRADETRANSACTION
TradeTransaction(**TransactionID**, InvestorID, AccountID, StockID, TradeDate, TradeType, Quantity, PricePerShare)

- Primary Key: TransactionID
- Foreign Key: InvestorID references Investor(InvestorID)
- Foreign Key: AccountID references BrokerageAccount(AccountID)
- Foreign Key: StockID references Stock(StockID)
- Required: InvestorID, AccountID, StockID, TradeDate, TradeType, Quantity, PricePerShare
- Constraint: TradeType must be BUY or SELL
- Constraint: Quantity must be greater than 0
- Constraint: PricePerShare must be greater than or equal to 0

## Cardinalities

- One Investor can have many BrokerageAccounts.
- One Investor can have many TradeTransactions.
- One BrokerageAccount can have many TradeTransactions.
- One Company can issue many Stocks.
- One Stock can appear in many TradeTransactions.
