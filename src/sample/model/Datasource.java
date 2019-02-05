package sample.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Asen Koparanski
 * Purpose:
 * Date: 05.02.2019
 */
public class Datasource {

    public static final String DB_NAME = "salesmanager.db";

    public static final String CONNECTION_STRING = "jdbc:sqlite:D:\\workspace\\SalesManager\\" + DB_NAME;

    public static final String TABLE_EMPLOYEES = "employees";
    public static final String COLUMN_EMP_ID = "id";
    public static final String COLUMN_EMP_NAME = "name";
    public static final int INDEX_EMP_ID = 1;
    public static final int INDEX_EMP_NAME = 2;

    public static final String TABLE_SALES = "sales";
    public static final String COLUMN_SALES_DESCRIPTION = "description";
    public static final String COLUMN_SALES_DETAILS = "details";
    public static final String COLUMN_SALESEMP_ID = "employeeId";
    public static final int INDEX_RECORD_NAME = 1;
    public static final int INDEX_RECORD_LEVEL = 2;
    public static final int INDEX_RECORDEMP_ID = 3;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    public static final String QUERY_EMPLOYEES = "SELECT " + COLUMN_EMP_NAME + " FROM " +
            TABLE_EMPLOYEES + " WHERE " + COLUMN_EMP_NAME + " = ?";

    private Connection conn;

    private static Datasource instance = new Datasource();

    private Datasource() {

    }

    public static Datasource getInstance() {
        return instance;
    }

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }

    public List<Employee> queryEmployees(int sortOrder) {

        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(TABLE_EMPLOYEES);
        if (sortOrder != ORDER_BY_NONE) {
            sb.append(" ORDER BY ");
            sb.append(COLUMN_EMP_NAME);
            sb.append(" COLLATE NOCASE ");
            if (sortOrder == ORDER_BY_DESC) {
                sb.append("DESC");
            } else {
                sb.append("ASC");
            }
        }

        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sb.toString())) {

            List<Employee> employees = new ArrayList<>();
            while (results.next()) {
                try {
                    Thread.sleep(20);
                } catch(InterruptedException e) {
                    System.out.println("Interuppted: " + e.getMessage());
                }
                Employee emp = new Employee();
                emp.setId(results.getInt(INDEX_EMP_ID));
                emp.setName(results.getString(INDEX_EMP_NAME));
                employees.add(emp);

            }
            return employees;

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }
}















