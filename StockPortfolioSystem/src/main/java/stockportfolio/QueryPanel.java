package stockportfolio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueryPanel extends JPanel {
    private final Map<String, Integer> investorMap = new LinkedHashMap<>();
    private final JComboBox<String> investorCombo = new JComboBox<>();
    private final JComboBox<String> tradeTypeCombo = new JComboBox<>(new String[]{"All", "BUY", "SELL"});
    private final JComboBox<String> sortCombo = new JComboBox<>(new String[]{"Date newest first", "Date oldest first", "Quantity high to low"});
    private final JTable query1Table = new JTable();
    private final JLabel query1Count = new JLabel("Rows: 0");

    private final JComboBox<String> industryCombo = new JComboBox<>();
    private final JComboBox<String> topNCombo = new JComboBox<>(new String[]{"5", "15", "25", "All"});
    private final JTable query2Table = new JTable();
    private final JLabel query2Count = new JLabel("Rows: 0");

    public QueryPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Transactions by Investor", createTransactionsByInvestorPanel());
        tabs.addTab("Top N Stocks by Volume", createTopStocksPanel());
        tabs.addChangeListener(e -> {
            loadInvestors();
            loadIndustries();
        });
        add(tabs, BorderLayout.CENTER);
        loadInvestors();
        loadIndustries();
    }

    private JPanel createTransactionsByInvestorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton run = new JButton("Run Query 1");
        filters.add(new JLabel("Investor:")); filters.add(investorCombo);
        filters.add(new JLabel("Trade Type:")); filters.add(tradeTypeCombo);
        filters.add(new JLabel("Sort:")); filters.add(sortCombo);
        filters.add(run); filters.add(query1Count);
        panel.add(filters, BorderLayout.NORTH);
        panel.add(new JScrollPane(query1Table), BorderLayout.CENTER);
        run.addActionListener(e -> runTransactionsByInvestor());
        return panel;
    }

    private JPanel createTopStocksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton run = new JButton("Run Query 2");
        filters.add(new JLabel("Industry:")); filters.add(industryCombo);
        filters.add(new JLabel("Top N:")); filters.add(topNCombo);
        filters.add(run); filters.add(query2Count);
        panel.add(filters, BorderLayout.NORTH);
        panel.add(new JScrollPane(query2Table), BorderLayout.CENTER);
        run.addActionListener(e -> runTopStocks());
        return panel;
    }

    private void runTransactionsByInvestor() {
        if (investorCombo.getSelectedItem() == null) {
            showError("Please select an investor."); return;
        }
        int investorId = investorMap.get(investorCombo.getSelectedItem().toString());
        String tradeType = tradeTypeCombo.getSelectedItem().toString();
        String orderBy = switch (sortCombo.getSelectedItem().toString()) {
            case "Date oldest first" -> "t.TradeDate ASC";
            case "Quantity high to low" -> "t.Quantity DESC";
            default -> "t.TradeDate DESC";
        };
       String sql = "SELECT t.TradeDate, t.TradeType, s.TickerSymbol, c.CompanyName, c.Industry, " +
        "t.Quantity, t.PricePerShare " +
        "FROM Investor i " +
        "JOIN BrokerageAccount ba ON i.InvestorID = ba.InvestorID " +
        "JOIN TradeTransaction t ON ba.AccountID = t.AccountID " +
        "JOIN Stock s ON t.StockID = s.StockID " +
        "JOIN Company c ON s.CompanyID = c.CompanyID " +
        "WHERE i.InvestorID = ? AND (? = 'All' OR t.TradeType = ?) " +
        "ORDER BY " + orderBy;
        
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, investorId); ps.setString(2, tradeType); ps.setString(3, tradeType);
            fillTable(ps, query1Table, query1Count,
                    new String[]{"Trade Date", "Trade Type", "Ticker Symbol", "Company Name", "Industry", "Quantity", "Price per Share"});
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void runTopStocks() {
        String industry = industryCombo.getSelectedItem() == null ? "All" : industryCombo.getSelectedItem().toString();
        String topN = topNCombo.getSelectedItem().toString();
        String limitText = topN.equals("All") ? "" : " LIMIT " + Integer.parseInt(topN);
        String sql = "SELECT s.TickerSymbol, c.CompanyName, c.Industry, " +
                "SUM(t.Quantity) AS TotalSharesTraded, COUNT(t.TransactionID) AS NumberOfTransactions " +
                "FROM Stock s " +
                "JOIN TradeTransaction t ON s.StockID = t.StockID " +
                "JOIN Company c ON s.CompanyID = c.CompanyID " +
                "WHERE (? = 'All' OR c.Industry = ?) " +
                "GROUP BY s.StockID, s.TickerSymbol, c.CompanyName, c.Industry " +
                "ORDER BY TotalSharesTraded DESC" + limitText;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, industry); ps.setString(2, industry);
            ResultSet rs = ps.executeQuery();
            DefaultTableModel model = new DefaultTableModel(new String[]{"Rank", "Ticker Symbol", "Company Name", "Industry", "Total Shares Traded", "Number of Transactions"}, 0);
            int rank = 1;
            while (rs.next()) {
                model.addRow(new Object[]{rank++, rs.getString("TickerSymbol"), rs.getString("CompanyName"), rs.getString("Industry"), rs.getInt("TotalSharesTraded"), rs.getInt("NumberOfTransactions")});
            }
            query2Table.setModel(model); query2Count.setText("Rows: " + model.getRowCount());
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void fillTable(PreparedStatement ps, JTable table, JLabel countLabel, String[] columns) throws SQLException {
        ResultSet rs = ps.executeQuery();
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        while (rs.next()) {
            Object[] row = new Object[columns.length];
            for (int i = 0; i < columns.length; i++) row[i] = rs.getObject(i + 1);
            model.addRow(row);
        }
        table.setModel(model);
        countLabel.setText("Rows: " + model.getRowCount());
    }

    private void loadInvestors() {
        Object selected = investorCombo.getSelectedItem();
        investorMap.clear(); investorCombo.removeAllItems();
        String sql = "SELECT InvestorID, FirstName, LastName FROM Investor ORDER BY LastName, FirstName";
        try (Statement st = DatabaseConnection.getConnection().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("FirstName") + " " + rs.getString("LastName") + " (ID " + rs.getInt("InvestorID") + ")";
                investorMap.put(name, rs.getInt("InvestorID")); investorCombo.addItem(name);
            }
            if (selected != null) investorCombo.setSelectedItem(selected);
        } catch (SQLException ex) { showError("Could not load investors: " + ex.getMessage()); }
    }

    private void loadIndustries() {
        Object selected = industryCombo.getSelectedItem();
        industryCombo.removeAllItems(); industryCombo.addItem("All");
        String sql = "SELECT DISTINCT Industry FROM Company ORDER BY Industry";
        try (Statement st = DatabaseConnection.getConnection().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) industryCombo.addItem(rs.getString("Industry"));
            if (selected != null) industryCombo.setSelectedItem(selected);
        } catch (SQLException ex) { showError("Could not load industries: " + ex.getMessage()); }
    }

    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
}
