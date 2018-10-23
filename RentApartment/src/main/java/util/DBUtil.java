package util;

import java.sql.*;

public class DBUtil {
    private static Connection conn = null;

    public static Connection getConnected() {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/rentapp", "admin", "admin");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static ResultSet getAllFreeApartments() {
        conn = getConnected();
        String sql = "SELECT * FROM APARTMENTS WHERE STATUS = 'free';";
        ResultSet result = null;

        try {
            PreparedStatement state = conn.prepareStatement(sql);
            result = state.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ResultSet getAllResultSet(String tableName) {
        conn = getConnected();
        String sql = "SELECT * FROM " + tableName + ";";
        ResultSet result = null;

        try {
            PreparedStatement state = conn.prepareStatement(sql);
            result = state.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ResultSet getSelectedRow(String tableName, int id) {
        conn = getConnected();
        String colName = tableName.substring(0, tableName.length() - 1);

        String sql = "SELECT * FROM " + tableName + " WHERE " + colName + "_ID = " + id + ";";
        ResultSet result = null;

        try {
            PreparedStatement state = conn.prepareStatement(sql);
            result = state.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static MyModel getAllModel(String tableName) {
        conn = getConnected();
        String sql = "SELECT * FROM " + tableName + ";";
        MyModel model;

        model = getMyModel(sql);

        return model;
    }

    private static MyModel getMyModel(String sql) {
        ResultSet result;
        MyModel model = null;
        try {
            PreparedStatement state = conn.prepareStatement(sql);
            result = state.executeQuery();
            model = new MyModel(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    public static MyModel getAllRents() {
        conn = getConnected();
        String sql = "SELECT RENT_ID, ADDRESS, FIRST_NAME, LAST_NAME\n" +
                "FROM RENT JOIN APARTMENTS\n" +
                "ON RENT.APARTMENT_ID = APARTMENTS.APARTMENT_ID\n" +
                "JOIN CLIENTS\n" +
                "ON RENT.CLIENT_ID = CLIENTS.CLIENT_ID;";
        MyModel model;

        model = getMyModel(sql);

        return model;
    }
}