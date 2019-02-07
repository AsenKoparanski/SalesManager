package sample.model;

import java.sql.*;
import java.time.LocalDate;
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
    public static final String COLUMN_SALES_DATE = "date";
    public static final int INDEX_SALES_DESCRIPTION = 1;
    public static final int INDEX_SALES_DETAILS = 2;
    public static final int INDEX_SALESEMP_ID = 3;
    public static final int INDEX_SALES_DATE = 4;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    public static final String QUERY_EMPLOYEES = "SELECT " + COLUMN_EMP_ID + " FROM " +
            TABLE_EMPLOYEES + " WHERE " + COLUMN_EMP_ID + " = ?";

    public static final String QUERY_SALES_BY_EMPLOYEE_ID = "SELECT * FROM " + TABLE_SALES +
            " WHERE " + COLUMN_SALESEMP_ID + "  = ? ORDER BY " + COLUMN_SALES_DESCRIPTION + " COLLATE NOCASE";

    public static final String INSERT_EMPLOYEES = "INSERT INTO " + TABLE_EMPLOYEES +
            '(' + COLUMN_EMP_ID + ", " + COLUMN_EMP_NAME + ") VALUES(?, ?)";

    public static final String INSERT_SALES = "INSERT INTO " + TABLE_SALES +
            '(' + COLUMN_SALES_DESCRIPTION + ", " + COLUMN_SALES_DETAILS + ", " +
            COLUMN_SALESEMP_ID + ", " + COLUMN_SALES_DATE + ") VALUES(?, ?, ?, ?)";

    private Connection conn;

    private PreparedStatement querySalesByEmployeeId;
    private PreparedStatement insertIntoEmployees;
    private PreparedStatement insertIntoSales;
    private PreparedStatement queryEmployees;

    private static Datasource instance = new Datasource();

    private Datasource() {

    }

    public static Datasource getInstance() {
        return instance;
    }

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            querySalesByEmployeeId = conn.prepareStatement(QUERY_SALES_BY_EMPLOYEE_ID);
            insertIntoEmployees = conn.prepareStatement(INSERT_EMPLOYEES);
            queryEmployees = conn.prepareStatement(QUERY_EMPLOYEES);
            insertIntoSales = conn.prepareStatement(INSERT_SALES);

            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {

            if (querySalesByEmployeeId != null) {
                querySalesByEmployeeId.close();
            }
            if (insertIntoEmployees != null) {
                insertIntoEmployees.close();
            }
            if (queryEmployees != null) {
                queryEmployees.close();
            }
            if (insertIntoSales != null) {
                insertIntoSales.close();
            }
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
    public List<Sale> querySaleForEmployeeId(int id) {
        try {
            querySalesByEmployeeId.setInt(1, id);
            ResultSet results = querySalesByEmployeeId.executeQuery();

            List<Sale> sales = new ArrayList<>();
            while(results.next()) {
                Sale sale = new Sale();
                sale.setDescription(results.getString(INDEX_SALES_DESCRIPTION));
                sale.setDetails(results.getString(INDEX_SALES_DETAILS));
                sale.setEmployeeId(results.getInt(INDEX_SALESEMP_ID));
                sale.setDate(results.getString(INDEX_SALES_DATE));
                System.out.println(sale.getDescription());
                System.out.println(sale.getDate());
                System.out.println(sale.getDetails());
                System.out.println(sale.getEmployeeId());
            }
            return sales;
        } catch (SQLException e) {
            System.out.println("Query failed" + e.getMessage());
            return null;
        }
    }

    public void insertEmployee(int id, String name) throws SQLException {

        queryEmployees.setInt(1, id);
        ResultSet results = queryEmployees.executeQuery();

        if(!results.next()) {
            insertIntoEmployees.setInt(1, id);
            insertIntoEmployees.setString(2, name);

            int affectedRows = insertIntoEmployees.executeUpdate();

            if(affectedRows !=1) {
                throw new SQLException("Couldn't insert employee!");
            }
        } else {
            // pop up dialog saying employee exists already.
            System.out.println("Employee exists already");
        }
    }
}















