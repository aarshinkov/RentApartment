package frames;

import util.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ApartmentFrame extends JFrame {
    private PreparedStatement state = null;
    private Connection conn = null;

    private int id = 0;

    private JTable table = new JTable();

    private JTextField addressTField = new JTextField();
    private JTextField priceTField = new JTextField();
    private JTextField areaTField = new JTextField();
    private JTextField floorTField = new JTextField();
    private JTextField roomTField = new JTextField();

    public ApartmentFrame() {
        resetFields();

        PFrame.getApartmentsPanel().setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        PFrame.getApartmentsPanel().add(mainPanel, BorderLayout.CENTER);
        JPanel navPanel = new JPanel();
        PFrame.getApartmentsPanel().add(navPanel, BorderLayout.SOUTH);

        mainPanel.setLayout(new GridLayout(2, 1));
        JPanel fieldsPanel = new JPanel();
        mainPanel.add(fieldsPanel);
        JPanel tablePanel = new JPanel();
        mainPanel.add(tablePanel);

        //change rows according to the fields of the tables
        fieldsPanel.setLayout(new GridLayout(5, 2, -300, 0));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel addressLabel = new JLabel("Address");
        fieldsPanel.add(addressLabel);
        fieldsPanel.add(addressTField);

        JLabel priceLabel = new JLabel("Price");
        fieldsPanel.add(priceLabel);
        fieldsPanel.add(priceTField);

        JLabel areaLabel = new JLabel("Area");
        fieldsPanel.add(areaLabel);
        fieldsPanel.add(areaTField);

        JLabel floorLabel = new JLabel("Floor");
        fieldsPanel.add(floorLabel);
        fieldsPanel.add(floorTField);

        JLabel roomsLabel = new JLabel("Rooms");
        fieldsPanel.add(roomsLabel);
        fieldsPanel.add(roomTField);

        JScrollPane scroller = new JScrollPane(table);
        tablePanel.add(scroller);
        scroller.setPreferredSize(new Dimension(500, 220));

        navPanel.setLayout(new FlowLayout());
        navPanel.setBackground(new Color(90, 161, 184));
        JButton addButton = new JButton("Add");
        navPanel.add(addButton);
        JButton editButton = new JButton("Edit");
        navPanel.add(editButton);
        JButton deleteButton = new JButton("Delete");
        navPanel.add(deleteButton);
        JButton showButton = new JButton("Show");
        navPanel.add(showButton);

        addButton.addActionListener(new addApartment());
        editButton.addActionListener(new editApartment());
        deleteButton.addActionListener(new deleteApartment());
        showButton.addActionListener(new showApartmentRow());

        table.setModel(DBUtil.getAllModel("APARTMENTS"));

    }

    private class addApartment implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String address = addressTField.getText();
            double price;
            double area;
            int floor;
            int rooms;
            String status = "free";

            try {
                price = Double.parseDouble(priceTField.getText());
                area = Double.parseDouble(areaTField.getText());
                floor = Integer.parseInt(floorTField.getText());
                rooms = Integer.parseInt(roomTField.getText());
            } catch (NumberFormatException e2) {
                showErrorMessage();
                return;
            }

            if (address.isEmpty() || price < 0 || area < 0 || rooms < 0) {
                showErrorMessage();
                return;
            }

            try {
                conn = DBUtil.getConnected();
                String sql = "INSERT INTO APARTMENTS VALUES(NULL,?,?,?,?,?,?);";
                state = conn.prepareStatement(sql);
                state.setString(1, address);
                state.setDouble(2, price);
                state.setDouble(3, area);
                state.setInt(4, floor);
                state.setInt(5, rooms);
                state.setString(6, status);

                state.execute();
            } catch (Exception ex) {
                showErrorMessage();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    System.out.println("The connection has not been opened!");
                }
                resetFields();
            }

            table.setModel(DBUtil.getAllModel("APARTMENTS"));
        }
    }

    private class editApartment implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String address = addressTField.getText();
            double price;
            double area;
            int floor;
            int rooms;

            try {
                price = checkPrice();
                area = checkArea();
                floor = checkFloor();
                rooms = checkRooms();
            } catch (IllegalArgumentException e2) {
                showErrorMessage();
                return;
            }

            try {
                if (id == 0) {
                    throw new Exception();
                }
                conn = DBUtil.getConnected();
                String sql = createSQLString(address, price, area, floor, rooms);
                state = conn.prepareStatement(sql);

                state.execute();

                String message = "The apartment with ID " + id + " has been successfully updated";
                JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                id = 0;
            } catch (Exception ex) {
                showErrorMessage();
            } finally {
                try {
                    conn.close();
                    resetFields();
                } catch (SQLException e1) {
                    System.out.println("The connection has not been opened!");
                }
            }

            table.setModel(DBUtil.getAllModel("APARTMENTS"));
        }

        private String createSQLString(String address, double price, double area, int floor, int rooms) {
            StringBuilder builder = new StringBuilder();

            builder.append("UPDATE APARTMENTS SET ADDRESS = '");
            builder.append(address);
            builder.append("', PRICE_PER_MONTH = ");
            builder.append(price);
            builder.append(", AREA = ");
            builder.append(area);
            builder.append(", FLOOR = ");
            builder.append(floor);
            builder.append(", ROOMS = ");
            builder.append(rooms);
            builder.append(" WHERE APARTMENT_ID = ");
            builder.append(id);
            builder.append(";");

            return String.valueOf(builder);
        }

        private double checkPrice() throws IllegalArgumentException {
            double price = 0.00;

            price = getPrice(price, priceTField);
            return price;
        }

        private double getPrice(double price, JTextField priceTField) {
            if (!priceTField.getText().equals("")) {
                try {
                    price = Double.parseDouble(priceTField.getText());
                } catch (Exception e) {
                    throw new IllegalArgumentException();
                }
                if (price < 0.00) {
                    throw new IllegalArgumentException();
                }
            }
            return price;
        }

        private double checkArea() throws IllegalArgumentException {
            double area = 0.00;

            area = getPrice(area, areaTField);
            return area;
        }

        private int checkFloor() throws IllegalArgumentException {
            int floor = -1;

            if (!floorTField.getText().equals("")) {
                try {
                    floor = Integer.parseInt(floorTField.getText());
                } catch (Exception e) {
                    throw new IllegalArgumentException();
                }
                if (floor < -1) {
                    throw new IllegalArgumentException();
                }
            }
            return floor;
        }

        private int checkRooms() throws IllegalArgumentException {
            int rooms = 0;

            if (!roomTField.getText().equals("")) {
                try {
                    rooms = Integer.parseInt(roomTField.getText());
                } catch (Exception e) {
                    throw new IllegalArgumentException();
                }
                if (rooms < 0) {
                    throw new IllegalArgumentException();
                }
            }
            return rooms;
        }

//        private String createSqlString(String address, double price, double area, int floor, int rooms, int id) {
//            StringBuilder builder = new StringBuilder();
//
//            boolean hasMember = false;
//
//            String sql = "UPDATE APARTMENTS SET ";
//            builder.append(sql);
//
//            if (address.equals("") && price == 0.00 && area == 0.00 && floor == -1 && rooms == 0) {
//                return null;
//            }
//
//            if (!address.equals("")) {
//                builder.append("ADDRESS = '");
//                builder.append(address);
//                builder.append("'");
//                hasMember = true;
//            }
//
//            if (price != 0.00) {
//                if (hasMember) {
//                    builder.append(", ");
//                }
//                builder.append("PRICE_PER_MONTH = ");
//                builder.append(price);
//                hasMember = true;
//            }
//
//            if (area != 0.00) {
//                if (hasMember) {
//                    builder.append(", ");
//                }
//                builder.append("AREA = ");
//                builder.append(area);
//                hasMember = true;
//            }
//
//            if (floor != -1) {
//                if (hasMember) {
//                    builder.append(", ");
//                }
//                builder.append("FLOOR = ");
//                builder.append(floor);
//                hasMember = true;
//            }
//
//            if (rooms != 0) {
//                if (hasMember) {
//                    builder.append(", ");
//                }
//                builder.append("ROOMS = ");
//                builder.append(rooms);
//            }
//
//            builder.append(" WHERE APARTMENT_ID = ");
//            builder.append(id);
//
//            return String.valueOf(builder);
//        }
    }

    private class deleteApartment implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            if (isIdNegative(row)) {
                return;
            }
            int apartmentID = Integer.parseInt(String.valueOf(table.getValueAt(row, 0)));

            try {
                conn = DBUtil.getConnected();
                String sql = "DELETE FROM APARTMENTS WHERE APARTMENT_ID = " + apartmentID + ";";
                state = conn.prepareStatement(sql);
                state.execute();

                String message = "The apartment with ID " + apartmentID + " has been successfully deleted from the database";
                JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }

            table.setModel(DBUtil.getAllModel("APARTMENTS"));
        }
    }

    private class showApartmentRow implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            if (isIdNegative(row)) {
                return;
            }
            id = Integer.parseInt(String.valueOf(table.getValueAt(row, 0)));

            ResultSet result = DBUtil.getSelectedRow("APARTMENTS", id);

            try {
                String address = null;
                double price = 0.00;
                double area = 0.00;
                int floor = 0;
                int room = 0;

                while (result.next()) {
                    address = result.getString(2);
                    price = result.getDouble(3);
                    area = result.getDouble(4);
                    floor = result.getInt(5);
                    room = result.getInt(6);
                }

                addressTField.setText(address);
                priceTField.setText(String.valueOf(price));
                areaTField.setText(String.valueOf(area));
                floorTField.setText(String.valueOf(floor));
                roomTField.setText(String.valueOf(room));

            } catch (SQLException e2) {
                e2.printStackTrace();
            }


        }
    }

    private void resetFields() {
        addressTField.setText("");
        priceTField.setText("");
        areaTField.setText("");
        floorTField.setText("");
        roomTField.setText("");
    }

    private void showErrorMessage() {
        System.out.println("Invalid values");
        String message = "You have entered invalid values! Please, try again!";
        JOptionPane.showMessageDialog(null, message, "Error! Invalid values.", JOptionPane.ERROR_MESSAGE);
        resetFields();
    }

    private boolean isIdNegative(int row) {
        if (row == -1) {
            JOptionPane.showMessageDialog(null,
                    "You have not selected a row in the table",
                    "Error!",
                    JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }
}
