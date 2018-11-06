package frames;

import entity.Apartment;
import entity.Client;
import util.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RentFrame extends JFrame {
    private PreparedStatement state = null;
    private Connection conn = null;

    private List<Apartment> apartmentsList = new ArrayList<>();
    private List<Client> clientsList = new ArrayList<>();

    private JTable table = new JTable();

    //use arrayListName.toArray() in the JComboBox's constructor
    private JComboBox<String> apartmentCB = new JComboBox<>();
    private JComboBox<String> clientCB = new JComboBox<>();

    public RentFrame() throws SQLException {

        PFrame.getRentPanel().setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        PFrame.getRentPanel().add(mainPanel, BorderLayout.CENTER);
        JPanel navPanel = new JPanel();
        PFrame.getRentPanel().add(navPanel, BorderLayout.SOUTH);

        mainPanel.setLayout(new GridLayout(2, 1));
        JPanel fieldsPanel = new JPanel();
        mainPanel.add(fieldsPanel);
        JPanel tablePanel = new JPanel();
        mainPanel.add(tablePanel);

        //change rows according to the fields of the tables
        fieldsPanel.setLayout(new GridLayout(4, 1));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel apartmentID = new JLabel("Apartment");
        fieldsPanel.add(apartmentID);
        fieldsPanel.add(apartmentCB);

        getAllFreeApartments();
        apartmentCBValuesInsert();

        JLabel clientID = new JLabel("Client");
        fieldsPanel.add(clientID);
        fieldsPanel.add(clientCB);

        getAllClients();
        clientCBValuesInsert();

        JScrollPane scroller = new JScrollPane(table);
        tablePanel.add(scroller);
        scroller.setPreferredSize(new Dimension(500, 220));

        navPanel.setLayout(new FlowLayout());
        navPanel.setBackground(new Color(90, 161, 184));
        JButton addButton = new JButton("Add");
        navPanel.add(addButton);
        JButton deleteButton = new JButton("Delete");
        navPanel.add(deleteButton);

        addButton.addActionListener(new addRent());
        deleteButton.addActionListener(new deleteRent());

        table.setModel(DBUtil.getAllRents());
    }

    private void getAllFreeApartments() throws SQLException {
        apartmentsList.clear();
        ResultSet result = DBUtil.getAllFreeApartments();
        while (result.next()) {
            int id = result.getInt(1);
            String address = result.getString(2);
            Apartment apartment = new Apartment(id, address);
            apartmentsList.add(apartment);
        }
    }

    private void apartmentCBValuesInsert() {
        int index = 1;
        for (Apartment apartment : apartmentsList) {
            String apartmentText = createApartmentString(index, apartment.getId(), apartment.getAddress());
            apartmentCB.addItem(apartmentText);
            index++;
        }
    }

    private String createApartmentString(int index, int id, String address) {
        StringBuilder text = new StringBuilder();
        text.append(index);
        text.append("| ID: ");
        text.append(id);
        text.append(" ");
        text.append(address);

        return String.valueOf(text);
    }

    private void getAllClients() throws SQLException {
        ResultSet result = DBUtil.getAllResultSet("CLIENTS");
        while (result.next()) {
            int id = result.getInt(1);
            String firstName = result.getString(2);
            String lastName = result.getString(3);
            String personalNumber = result.getString(5);
            Client client = new Client(id, firstName, lastName, personalNumber);
            clientsList.add(client);
        }
    }

    private void clientCBValuesInsert() {
        int index = 1;
        for (Client client : clientsList) {
            String clientText = createClientString(index, client.getId(), client.getFirstName(), client.getLastName(), client.getPersonalNumber());
            clientCB.addItem(clientText);
            index++;
        }
    }

    private String createClientString(int index, int id, String firstName, String lastName, String personalNumber) {
        StringBuilder text = new StringBuilder();
        text.append(index);
        text.append("| ID: ");
        text.append(id);
        text.append(" ");
        text.append(firstName);
        text.append(" ");
        text.append(lastName);
        text.append(", ЕГН: ");
        text.append(personalNumber, 0, 6);
        text.append("...");

        return String.valueOf(text);
    }

    private class addRent implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int clientId = getComboBoxID(clientCB);
            int apartmentId = getComboBoxID(apartmentCB);

            if (apartmentId == -1) {
                String message = "There are no free apartments at the moment! Please, try again!";
                JOptionPane.showMessageDialog(null, message, "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                conn = DBUtil.getConnected();
                String sql = "INSERT INTO RENT VALUES(NULL,?,?);";
                state = conn.prepareStatement(sql);
                state.setInt(1, apartmentId);
                state.setInt(2, clientId);

                state.execute();
            } catch (Exception ex) {
                System.out.println("Invalid values");
                String message = "You have entered invalid values! Please, try again!";
                JOptionPane.showMessageDialog(null, message, "Error! Invalid values.", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }

            table.setModel(DBUtil.getAllRents());

            try {
                conn = DBUtil.getConnected();
                String sql = "UPDATE APARTMENTS\n" +
                        "SET STATUS = 'taken'\n" +
                        "WHERE APARTMENT_ID = " + apartmentId + ";";
                state = conn.prepareStatement(sql);
                state.execute();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    System.out.println("The connection has not been opened!");
                }
            }


            apartmentCB.removeAllItems();

            try {
                getAllFreeApartments();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            apartmentCBValuesInsert();
        }

    }

    private class deleteRent implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            int rentId = Integer.parseInt(String.valueOf(table.getValueAt(row, 0)));

            try {
                conn = DBUtil.getConnected();
                String sqlGetApartmentID = "SELECT * FROM RENT WHERE RENT_ID = " + rentId + ";";
                state = conn.prepareStatement(sqlGetApartmentID);
                ResultSet set = state.executeQuery();

                int apartmentID = 0;
                while (set.next()) {
                    apartmentID = set.getInt("APARTMENT_ID");
                }

                if (apartmentID == 0) {
                    return;
                }

                String sqlDelete = "DELETE FROM RENT WHERE RENT_ID = " + rentId + ";";
                state = conn.prepareStatement(sqlDelete);
                state.execute();

                String sqlUpdate = "UPDATE APARTMENTS SET STATUS = 'free' WHERE APARTMENT_ID = " + apartmentID + ";";
                state = conn.prepareStatement(sqlUpdate);
                state.execute();

                String message = "The rent with ID " + rentId + " has been successfully deleted from the database";
                JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException a) {
                a.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }

            table.setModel(DBUtil.getAllRents());

            apartmentCB.removeAllItems();

            try {
                getAllFreeApartments();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            apartmentCBValuesInsert();
        }

    }

    private int getComboBoxID(JComboBox<String> comboBox) {
        String stringId;
        try {
            stringId = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
        } catch (NullPointerException e) {
            return -1;
        }
        String[] array = stringId.split(" ");

        return Integer.parseInt(array[2]);
    }
}