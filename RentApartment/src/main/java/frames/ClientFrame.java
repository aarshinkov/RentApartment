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

public class ClientFrame extends JFrame {
    private PreparedStatement state = null;
    private Connection conn = null;

    private int id = 0;

    private JTable table = new JTable();

    private JTextField firstNameTField = new JTextField();
    private JTextField lastNameTField = new JTextField();

    private JRadioButton maleRButton = new JRadioButton("Male");
    private JRadioButton femaleRButton = new JRadioButton("Female");

    private JTextField personalNumberTField = new JTextField(10);

    public ClientFrame() {

        PFrame.getClientsPanel().setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        PFrame.getClientsPanel().add(mainPanel, BorderLayout.CENTER);
        JPanel navPanel = new JPanel();
        PFrame.getClientsPanel().add(navPanel, BorderLayout.SOUTH);

        mainPanel.setLayout(new GridLayout(2, 1));
        JPanel fieldsPanel = new JPanel();
        mainPanel.add(fieldsPanel);
        JPanel tablePanel = new JPanel();
        mainPanel.add(tablePanel);

        //change rows according to the fields of the tables
        fieldsPanel.setLayout(new GridLayout(4, 2, -300, 0));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel firstNameLabel = new JLabel("First name");
        fieldsPanel.add(firstNameLabel);
        fieldsPanel.add(firstNameTField);

        JLabel lastNameLabel = new JLabel("Last name");
        fieldsPanel.add(lastNameLabel);
        fieldsPanel.add(lastNameTField);

        JLabel genderLabel = new JLabel("Gender");
        fieldsPanel.add(genderLabel);
        JPanel genderPanel = new JPanel();
        fieldsPanel.add(genderPanel);

        genderPanel.setLayout(new FlowLayout());
        ButtonGroup group = new ButtonGroup();
        group.add(maleRButton);
        group.add(femaleRButton);

        genderPanel.add(maleRButton);
        maleRButton.setSelected(true);
        genderPanel.add(femaleRButton);

        JLabel personalNumberLabel = new JLabel("Personal number");
        fieldsPanel.add(personalNumberLabel);
        fieldsPanel.add(personalNumberTField);

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

        addButton.addActionListener(new addClient());
        editButton.addActionListener(new editClient());
        deleteButton.addActionListener(new deleteClient());
        showButton.addActionListener(new showClientRow());

        table.setModel(DBUtil.getAllModel("CLIENTS"));
    }

    private class addClient implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String firstName = firstNameTField.getText();
            String lastName = lastNameTField.getText();
            String gender = checkGender();
            String personalNumber = personalNumberTField.getText();

            if (personalNumber.length() != 10) {
                showErrorMessage();
                resetFields();
                return;
            }
            try {
                conn = DBUtil.getConnected();
                String sql = "INSERT INTO CLIENTS VALUES(NULL,?,?,?,?);";
                state = conn.prepareStatement(sql);
                state.setString(1, firstName);
                state.setString(2, lastName);
                state.setString(3, gender);
                state.setString(4, personalNumber);


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

            table.setModel(DBUtil.getAllModel("CLIENTS"));
        }
    }

    private class editClient implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String firstName = firstNameTField.getText();
            String lastName = lastNameTField.getText();

            try {
                if (id == 0) {
                    throw new Exception();
                }
                conn = DBUtil.getConnected();
                String sql = createSQLString(firstName, lastName);
                state = conn.prepareStatement(sql);

                state.execute();

                String message = "The customer with ID " + id + " has been successfully updated";
                JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                id = 0;
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

            table.setModel(DBUtil.getAllModel("CLIENTS"));
        }

        private String createSQLString(String firstName, String lastName) {
            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE CLIENTS SET FIRST_NAME = '");
            builder.append(firstName);
            builder.append("', LAST_NAME = '");
            builder.append(lastName);
            builder.append("' WHERE CLIENT_ID = ");
            builder.append(id);

            return String.valueOf(builder);
        }
    }

    private class deleteClient implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            int clientID = Integer.parseInt(String.valueOf(table.getValueAt(row, 0)));

            try {
                conn = DBUtil.getConnected();
                String sql = "DELETE FROM CLIENTS WHERE CLIENT_ID = " + clientID + ";";
                state = conn.prepareStatement(sql);

                state.execute();

                String message = "The customer with ID " + clientID + " has been successfully deleted from the database";
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

            table.setModel(DBUtil.getAllModel("CLIENTS"));
        }
    }

    private class showClientRow implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            id = Integer.parseInt(String.valueOf(table.getValueAt(row, 0)));

            ResultSet result = DBUtil.getSelectedRow("CLIENTS", id);

            try {
                String firstName = "";
                String lastName = "";
                while (result.next()) {
                    firstName = result.getString(2);
                    lastName = result.getString(3);
                }

                firstNameTField.setText(firstName);
                lastNameTField.setText(lastName);
            } catch (SQLException e2) {
                e2.printStackTrace();
            }


        }
    }

    private void resetFields() {
        firstNameTField.setText("");
        lastNameTField.setText("");
        maleRButton.setSelected(true);
        personalNumberTField.setText("");
    }

    private String checkGender() {
        String gender = "m";
        if (femaleRButton.isSelected()) {
            gender = "f";
        }
        return gender;
    }

    private void showErrorMessage() {
        System.out.println("Invalid values");
        String message = "You have entered invalid values! Please, try again!";
        JOptionPane.showMessageDialog(null, message, "Error! Invalid values.", JOptionPane.ERROR_MESSAGE);
    }
}
