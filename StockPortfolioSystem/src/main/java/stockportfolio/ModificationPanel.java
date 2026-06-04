package stockportfolio;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModificationPanel extends JPanel {
    private final Map<String, Integer> companyMap = new LinkedHashMap<>();

    public ModificationPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Investor", createInvestorModifyPanel());
        tabs.addTab("Company", createCompanyModifyPanel());
        tabs.addTab("Stock", createStockModifyPanel());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createInvestorModifyPanel() {
        JPanel panel = formPanel();
        JTextField id = new JTextField(8);
        JTextField first = new JTextField(20);
        JTextField last = new JTextField(20);
        JTextField email = new JTextField(20);
        JTextField country = new JTextField(20);
        JTextField phone = new JTextField(20);
        JButton search = new JButton("Search");
        JButton update = new JButton("Update Investor");
        setEnabled(false, first, last, email, country, phone); update.setEnabled(false); id.setEditable(true);

        addRow(panel, "Investor ID", id);
        addRow(panel, "", search);
        addRow(panel, "First Name *", first);
        addRow(panel, "Last Name *", last);
        addRow(panel, "Email *", email);
        addRow(panel, "Country *", country);
        addRow(panel, "Phone", phone);
        addRow(panel, "", update);

        search.addActionListener(e -> {
            Integer investorId = parseInt(id.getText().trim(), "Investor ID must be a whole number.");
            if (investorId == null) return;
            String sql = "SELECT * FROM Investor WHERE InvestorID = ?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setInt(1, investorId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    first.setText(rs.getString("FirstName")); last.setText(rs.getString("LastName"));
                    email.setText(rs.getString("Email")); country.setText(rs.getString("Country"));
                    phone.setText(rs.getString("Phone"));
                    setEnabled(true, first, last, email, country, phone); update.setEnabled(true); id.setEditable(false);
                } else showError("Investor not found.");
            } catch (SQLException ex) { showError(ex.getMessage()); }
        });
        update.addActionListener(e -> {
            if (isBlank(first, last, email, country)) { showError("First name, last name, email, and country are required."); return; }
            String sql = "UPDATE Investor SET FirstName=?, LastName=?, Email=?, Country=?, Phone=? WHERE InvestorID=?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setString(1, first.getText().trim()); ps.setString(2, last.getText().trim());
                ps.setString(3, email.getText().trim()); ps.setString(4, country.getText().trim());
                ps.setString(5, phone.getText().trim().isEmpty() ? null : phone.getText().trim());
                ps.setInt(6, Integer.parseInt(id.getText().trim())); ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Investor updated successfully.");
                clear(id, first, last, email, country, phone); setEnabled(false, first, last, email, country, phone); update.setEnabled(false); id.setEditable(true);
            } catch (SQLException ex) { showError(ex.getMessage()); }
        });
        return wrap(panel);
    }

    private JPanel createCompanyModifyPanel() {
        JPanel panel = formPanel();
        JTextField id = new JTextField(8);
        JTextField name = new JTextField(20);
        JTextField industry = new JTextField(20);
        JTextField country = new JTextField(20);
        JTextField headquarters = new JTextField(20);
        JTextField founded = new JTextField(20);
        JButton search = new JButton("Search");
        JButton update = new JButton("Update Company");
        setEnabled(false, name, industry, country, headquarters, founded); update.setEnabled(false); id.setEditable(true);

        addRow(panel, "Company ID", id); addRow(panel, "", search); addRow(panel, "Company Name *", name);
        addRow(panel, "Industry *", industry); addRow(panel, "Country *", country); addRow(panel, "Headquarters", headquarters);
        addRow(panel, "Founded Year", founded); addRow(panel, "", update);

        search.addActionListener(e -> {
            Integer companyId = parseInt(id.getText().trim(), "Company ID must be a whole number."); if (companyId == null) return;
            String sql = "SELECT * FROM Company WHERE CompanyID = ?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setInt(1, companyId); ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    name.setText(rs.getString("CompanyName")); industry.setText(rs.getString("Industry")); country.setText(rs.getString("Country"));
                    headquarters.setText(rs.getString("Headquarters")); founded.setText(rs.getString("FoundedYear"));
                    setEnabled(true, name, industry, country, headquarters, founded); update.setEnabled(true); id.setEditable(false);
                } else showError("Company not found.");
            } catch (SQLException ex) { showError(ex.getMessage()); }
        });
        update.addActionListener(e -> {
            if (isBlank(name, industry, country)) { showError("Company name, industry, and country are required."); return; }
            Integer year = parseOptionalInt(founded.getText().trim(), "Founded year must be a whole number."); if (year == -1) return;
            String sql = "UPDATE Company SET CompanyName=?, Industry=?, Country=?, Headquarters=?, FoundedYear=? WHERE CompanyID=?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setString(1, name.getText().trim()); ps.setString(2, industry.getText().trim()); ps.setString(3, country.getText().trim());
                ps.setString(4, headquarters.getText().trim().isEmpty() ? null : headquarters.getText().trim());
                if (year == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, year);
                ps.setInt(6, Integer.parseInt(id.getText().trim())); ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Company updated successfully.");
                clear(id, name, industry, country, headquarters, founded); setEnabled(false, name, industry, country, headquarters, founded); update.setEnabled(false); id.setEditable(true);
            } catch (SQLException ex) { showError(ex.getMessage()); }
        });
        return wrap(panel);
    }

    private JPanel createStockModifyPanel() {
        JPanel panel = formPanel();
        JTextField id = new JTextField(8);
        JTextField ticker = new JTextField(20);
        JTextField exchange = new JTextField(20);
        JTextField price = new JTextField(20);
        JComboBox<String> companies = new JComboBox<>();
        JButton search = new JButton("Search");
        JButton update = new JButton("Update Stock");
        setEnabled(false, ticker, exchange, price); companies.setEnabled(false); update.setEnabled(false); id.setEditable(true);

        addRow(panel, "Stock ID", id); addRow(panel, "", search); addRow(panel, "Ticker Symbol *", ticker);
        addRow(panel, "Exchange *", exchange); addRow(panel, "Current Price *", price); addRow(panel, "Company *", companies); addRow(panel, "", update);

        search.addActionListener(e -> {
            loadCompanies(companies);
            Integer stockId = parseInt(id.getText().trim(), "Stock ID must be a whole number."); if (stockId == null) return;
            String sql = "SELECT s.*, c.CompanyName FROM Stock s JOIN Company c ON s.CompanyID=c.CompanyID WHERE s.StockID=?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setInt(1, stockId); ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    ticker.setText(rs.getString("TickerSymbol")); exchange.setText(rs.getString("ExchangeName")); price.setText(rs.getString("CurrentPrice"));
                    companies.setSelectedItem(rs.getString("CompanyName")); setEnabled(true, ticker, exchange, price); companies.setEnabled(true); update.setEnabled(true); id.setEditable(false);
                } else showError("Stock not found.");
            } catch (SQLException ex) { showError(ex.getMessage()); }
        });
        update.addActionListener(e -> {
            if (isBlank(ticker, exchange, price) || companies.getSelectedItem() == null) { showError("Ticker, exchange, price, and company are required."); return; }
            Double p = parseDouble(price.getText().trim(), "Current price must be a number."); if (p == null) return;
            String company = companies.getSelectedItem().toString();
            String sql = "UPDATE Stock SET CompanyID=?, TickerSymbol=?, ExchangeName=?, CurrentPrice=? WHERE StockID=?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setInt(1, companyMap.get(company)); ps.setString(2, ticker.getText().trim().toUpperCase());
                ps.setString(3, exchange.getText().trim()); ps.setDouble(4, p); ps.setInt(5, Integer.parseInt(id.getText().trim())); ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Stock updated successfully.");
                clear(id, ticker, exchange, price); setEnabled(false, ticker, exchange, price); companies.setEnabled(false); update.setEnabled(false); id.setEditable(true);
            } catch (SQLException ex) { showError(ex.getMessage()); }
        });
        return wrap(panel);
    }

    private void loadCompanies(JComboBox<String> combo) {
        companyMap.clear(); combo.removeAllItems();
        String sql = "SELECT CompanyID, CompanyName FROM Company ORDER BY CompanyName";
        try (Statement st = DatabaseConnection.getConnection().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) { String n = rs.getString("CompanyName"); companyMap.put(n, rs.getInt("CompanyID")); combo.addItem(n); }
        } catch (SQLException ex) { showError("Could not load companies: " + ex.getMessage()); }
    }

    private JPanel formPanel() { JPanel p = new JPanel(new GridBagLayout()); p.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); return p; }
    private JPanel wrap(JPanel panel) { JPanel w = new JPanel(new BorderLayout()); w.add(panel, BorderLayout.NORTH); return w; }
    private void addRow(JPanel p, String label, JComponent comp) { GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(8,8,8,8); c.anchor = GridBagConstraints.WEST; c.gridx = 0; c.gridy = p.getComponentCount()/2; p.add(new JLabel(label), c); c.gridx = 1; p.add(comp, c); }
    private boolean isBlank(JTextField... fields) { for (JTextField f : fields) if (f.getText().trim().isEmpty()) return true; return false; }
    private void clear(JTextField... fields) { for (JTextField f : fields) f.setText(""); }
    private void setEnabled(boolean enabled, JTextField... fields) { for (JTextField f : fields) f.setEnabled(enabled); }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
    private Integer parseInt(String text, String error) { try { return Integer.parseInt(text); } catch (NumberFormatException ex) { showError(error); return null; } }
    private Integer parseOptionalInt(String text, String error) { if (text.isEmpty()) return null; try { return Integer.parseInt(text); } catch (NumberFormatException ex) { showError(error); return -1; } }
    private Double parseDouble(String text, String error) { try { return Double.parseDouble(text); } catch (NumberFormatException ex) { showError(error); return null; } }
}
