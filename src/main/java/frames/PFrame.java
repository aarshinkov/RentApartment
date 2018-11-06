package frames;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.sql.SQLException;

public class PFrame extends JFrame {
    private Object object;

    private static JPanel apartmentsPanel = new JPanel();
    private static JPanel clientsPanel = new JPanel();
    private static JPanel rentPanel = new JPanel();


    public PFrame() {
        this.setSize(600, 600);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Apartment rental software");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.add("Apartments", apartmentsPanel);
        tabs.add("Clients", clientsPanel);
        tabs.add("Rent", rentPanel);

        JFrame frame = new ApartmentFrame();

        tabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                JTabbedPane pane = (JTabbedPane) evt.getSource();

                int selectedIndex = pane.getSelectedIndex();
                switch (selectedIndex) {
                    case 1:
                        clearPanel(clientsPanel);
                        object = new ClientFrame();
                        break;
                    case 2:
                        clearPanel(rentPanel);
                        try {
                            object = new RentFrame();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        clearPanel(apartmentsPanel);
                        object = new ApartmentFrame();
                        break;
                }
                object = null;
            }
        });


        this.add(tabs);
        this.setVisible(true);
    }

    private void clearPanel(JPanel currentPanel) {
        currentPanel.removeAll();
        currentPanel.revalidate();
        currentPanel.repaint();
    }

    public static JPanel getApartmentsPanel() {
        return apartmentsPanel;
    }

    public static JPanel getClientsPanel() {
        return clientsPanel;
    }

    public static JPanel getRentPanel() {
        return rentPanel;
    }
}
