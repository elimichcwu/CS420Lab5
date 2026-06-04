package stockportfolio;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class InsertionPanel extends JPanel {
    private final JTabbedPane tabs = new JTabbedPane();
    private final JComboBox<String> stockCompanyCombo = new JComboBox<>();
    private final Map<String, Integer> companyMap = new LinkedHashMap<>();

    public InsertionPanel() {
        setLayout(new BorderLayout());
        tabs.addTab("Investor", createInvestorInsertPanel());
        tabs.addTab("Company", createCompanyInsertPanel());
        tabs.addTab("Stock", createStockInsertPanel());
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 2) {
                loadCompanies(stockCompanyCombo);
            }
        });
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createInvestorInsertPanel() {
        JPanel panel = formPanel();
        JTextField firstName = new JTextField(20);
        JTextField lastName = new JTextField(20);
        JTextField email = new JTextField(20);
        JTextField country = new JTextField(20);
        JTextField phone = new JTextField(20);
        JButton add = new JButton("Insert Investor");

        addRow(panel, "First Name *", firstName);
        addRow(panel, "Last Name *", lastName);
        addRow(panel, "Email *", email);
        addRow(panel, "Country *", country);
        addRow(panel, "Phone", phone);
        addRow(panel, "", add);

        add.addActionListener(e -> {
            if (isBlank(firstName, lastName, email, country)) {
                showError("First name, last name, email, and country are required.");
                return;
            }
            String sql = "INSERT INTO Investor (FirstName, LastName, Email, Country, Phone) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setString(1, firstName.getText().trim());
                ps.setString(2, lastName.getText().trim());
                ps.setString(3, email.getText().trim());
                ps.setString(4, country.getText().trim());
                ps.setString(5, phone.getText().trim().isEmpty() ? null : phone.getText().trim());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Investor inserted successfully.");
                clear(firstName, lastName, email, country, phone);
            } catch (SQLException ex) {
                showError(ex.getMessage());
            }
        });
        return wrap(panel);
    }

    private JPanel createCompanyInsertPanel() {
        JPanel panel = formPanel();
        JTextField name = new JTextField(20);
        JTextField industry = new JTextField(20);
        JTextField country = new JTextField(20);
        JTextField headquarters = new JTextField(20);
        JTextField foundedYear = new JTextField(20);
        JButton add = new JButton("Insert Company");

        addRow(panel, "Company Name *", name);
        addRow(panel, "Industry *", industry);
        addRow(panel, "Country *", country);
        addRow(panel, "Headquarters", headquarters);
        addRow(panel, "Founded Year", foundedYear);
        addRow(panel, "", add);

        add.addActionListener(e -> {
            if (isBlank(name, industry, country)) {
                showError("Company name, industry, and country are required.");
                return;
            }
            Integer year = parseOptionalInt(foundedYear.getText().trim(), "Founded year must be a whole number.");
            if (year == -1) return;
            String sql = "INSERT INTO Company (CompanyName, Industry, Country, Headquarters, FoundedYear) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setString(1, name.getText().trim());
                ps.setString(2, industry.getText().trim());
                ps.setString(3, country.getText().trim());
                ps.setString(4, headquarters.getText().trim().isEmpty() ? null : headquarters.getText().trim());
                if (year == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, year);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Company inserted successfully.");
                clear(name, industry, country, headquarters, foundedYear);
            } catch (SQLException ex) {
                showError(ex.getMessage());
            }
        });
        return wrap(panel);
    }

    private JPanel createStockInsertPanel() {
        JPanel panel = formPanel();
        JTextField ticker = new JTextField(20);
        JTextField exchange = new JTextField(20);
        JTextField price = new JTextField(20);
        JButton add = new JButton("Insert Stock");

        loadCompanies(stockCompanyCombo);
        addRow(panel, "Ticker Symbol *", ticker);
        addRow(panel, "Exchange *", exchange);
        addRow(panel, "Current Price *", price);
        addRow(panel, "Company *", stockCompanyCombo);
        addRow(panel, "", add);

        add.addActionListener(e -> {
            if (isBlank(ticker, exchange, price) || stockCompanyCombo.getSelectedItem() == null) {
                showError("Ticker symbol, exchange, current price, and company are required.");
                return;
            }
            Double currentPrice = parseDouble(price.getText().trim(), "Current price must be a number.");
            if (currentPrice == null) return;
            String companyName = stockCompanyCombo.getSelectedItem().toString();
            Integer companyId = companyMap.get(companyName);
            String sql = "INSERT INTO Stock (CompanyID, TickerSymbol, ExchangeName, CurrentPrice) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setInt(1, companyId);
                ps.setString(2, ticker.getText().trim().toUpperCase());
                ps.setString(3, exchange.getText().trim());
                ps.setDouble(4, currentPrice);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Stock inserted successfully.");
                clear(ticker, exchange, price);
            } catch (SQLException ex) {
                showError(ex.getMessage());
            }
        });
        return wrap(panel);
    }

    private void loadCompanies(JComboBox<String> combo) {
        companyMap.clear();
        combo.removeAllItems();
        String sql = "SELECT CompanyID, CompanyName FROM Company ORDER BY CompanyName";
        try (Statement st = DatabaseConnection.getConnection().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("CompanyName");
                companyMap.put(name, rs.getInt("CompanyID"));
                combo.addItem(name);
            }
        } catch (SQLException ex) {
            showError("Could not load companies: " + ex.getMessage());
        }
    }

    private JPanel formPanel() { JPanel p = new JPanel(new GridBagLayout()); p.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); return p; }
    private JPanel wrap(JPanel panel) { JPanel w = new JPanel(new BorderLayout()); w.add(panel, BorderLayout.NORTH); return w; }
    private void addRow(JPanel p, String label, JComponent comp) { GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(8,8,8,8); c.anchor = GridBagConstraints.WEST; c.gridx = 0; c.gridy = p.getComponentCount()/2; p.add(new JLabel(label), c); c.gridx = 1; p.add(comp, c); }
    private boolean isBlank(JTextField... fields) { for (JTextField f : fields) if (f.getText().trim().isEmpty()) return true; return false; }
    private void clear(JTextField... fields) { for (JTextField f : fields) f.setText(""); }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
    private Integer parseOptionalInt(String text, String error) { if (text.isEmpty()) return null; try { return Integer.parseInt(text); } catch (NumberFormatException ex) { showError(error); return -1; } }
    private Double parseDouble(String text, String error) { try { return Double.parseDouble(text); } catch (NumberFormatException ex) { showError(error); return null; } }
}
