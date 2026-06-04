# CS420 Stock Portfolio Management System

This project is a Java Swing and MySQL desktop application for tracking investors, companies, stocks, and stock trades.

## Files included

- `docs/ER_Diagram.drawio` - ER diagram for draw.io
- `docs/Relational_Schema.md` - relational schema with PKs, FKs, and constraints
- `sql/StockPortfolio.sql` - two-step SQL script with CREATE TABLE statements, ALTER TABLE constraints, and sample data
- `src/main/java/stockportfolio/DatabaseConnection.java` - JDBC singleton connection class
- `src/main/java/stockportfolio/GUI.java` - main Swing GUI
- `src/main/java/stockportfolio/InsertionPanel.java` - insert Investor, Company, and Stock
- `src/main/java/stockportfolio/ModificationPanel.java` - search and update Investor, Company, and Stock
- `src/main/java/stockportfolio/QueryPanel.java` - query screens with JTable results
- `src/main/java/stockportfolio/TestConnection.java` - database connection test
- `docs/Screenshot_Checklist.md` - exact screenshot checklist



```java
private static final String PASSWORD = "password";
```

6. Run `TestConnection.java` first.
7. Run `GUI.java` to open the app.

## Screenshot reminder

The assignment requires screenshots for every function. Use `docs/Screenshot_Checklist.md` and save screenshots in the `screenshots` folder.
