package stockportfolio;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class GUI extends JFrame {
    public GUI() {
        setTitle("Stock Portfolio Management System");
        setSize(1050, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.addTab("Data Insertion", new InsertionPanel());
        mainTabs.addTab("Data Modification", new ModificationPanel());
        mainTabs.addTab("Data Query", new QueryPanel());
        add(mainTabs);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}
