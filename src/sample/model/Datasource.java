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
    public static final String COLUMN_SALES_ID = "id";
    public static final String COLUMN_SALES_DESCRIPTION = "description";
    public static final String COLUMN_SALES_DETAILS = "details";
    public static final String COLUMN_SALES_EMP_ID = "empId";
    public static final String COLUMN_SALES_DATE = "date";
    public static final int INDEX_SALES_ID = 1;
    public static final int INDEX_SALES_DESCRIPTION = 2;
    public static final int INDEX_SALES_DETAILS = 3;
    public static final int INDEX_SALES_EMP_ID = 4;
    public static final int INDEX_SALES_DATE = 5;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    public static final String QUERY_EMPLOYEES = "SELECT " + COLUMN_EMP_ID + " FROM " +
            TABLE_EMPLOYEES + " WHERE " + COLUMN_EMP_ID + " = ?";

    public static final String QUERY_SALES_BY_EMPLOYEE_ID = "SELECT * FROM " + TABLE_SALES +
            " WHERE " + COLUMN_SALES_EMP_ID + " = ? ORDER BY " + COLUMN_SALES_DESCRIPTION + " COLLATE NOCASE";

    public static final String INSERT_EMPLOYEES = "INSERT INTO " + TABLE_EMPLOYEES +
            '(' + COLUMN_EMP_ID + ", " + COLUMN_EMP_NAME + ") VALUES(?, ?)";

    public static final String INSERT_SALES = "INSERT INTO " + TABLE_SALES +
            '(' + COLUMN_SALES_DESCRIPTION + ", " + COLUMN_SALES_DETAILS + ", " +
            COLUMN_SALES_EMP_ID + ", " + COLUMN_SALES_DATE + ") VALUES(?, ?, ?, ?)";

    public static final String DELETE_EMPLOYEES = "DELETE FROM " + TABLE_EMPLOYEES +
            " WHERE " + COLUMN_EMP_ID + " = ?";

    public static final String DELETE_SALES_BY_EMP_ID = "DELETE FROM " + TABLE_SALES +
            " WHERE " + COLUMN_EMP_ID + " = ?";

    public static final String DELETE_SALES_BY_ID = "DELETE FROM " + TABLE_SALES +
            " WHERE " + COLUMN_SALES_ID + " = ?";

    public static final String HIGHEST_SALES_ID = "SELECT MAX(" + COLUMN_SALES_ID + ") FROM " + TABLE_SALES;


    private Connection conn;
    private PreparedStatement querySalesByEmpId;
    private PreparedStatement insertIntoEmployees;
    private PreparedStatement insertIntoSales;
    private PreparedStatement queryEmployees;
    private PreparedStatement deleteEmployees;
    private PreparedStatement deleteSalesByEmpId;
    private PreparedStatement deleteSalesById;

    private static Datasource instance = new Datasource();

    private Datasource() {

    }

    public static Datasource getInstance() {
        return instance;
    }

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            querySalesByEmpId = conn.prepareStatement(QUERY_SALES_BY_EMPLOYEE_ID);
            insertIntoEmployees = conn.prepareStatement(INSERT_EMPLOYEES);
            queryEmployees = conn.prepareStatement(QUERY_EMPLOYEES);
            insertIntoSales = conn.prepareStatement(INSERT_SALES);
            deleteEmployees = conn.prepareStatement(DELETE_EMPLOYEES);
            deleteSalesById = conn.prepareStatement(DELETE_SALES_BY_ID);
            deleteSalesByEmpId = conn.prepareStatement(DELETE_SALES_BY_EMP_ID);

            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("Duplicates")
    public void close() {
        try {
            if (deleteSalesById != null) {
                deleteSalesById.close();
            }
            if (deleteSalesByEmpId != null) {
                deleteSalesByEmpId.close();
            }
            if (deleteEmployees != null) {
                deleteEmployees.close();
            }
            if (querySalesByEmpId != null) {
                querySalesByEmpId.close();
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
                } catch (InterruptedException e) {
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

    public List<Sale> querySalesByEmployeeId(int id) {
        try {
            querySalesByEmpId.setInt(1, id);
            ResultSet results = querySalesByEmpId.executeQuery();

            List<Sale> sales = new ArrayList<>();
            while (results.next()) {

                Sale sale = new Sale();
                sale.setId(results.getInt(INDEX_SALES_ID));
                sale.setDescription(results.getString(INDEX_SALES_DESCRIPTION));
                sale.setDetails(results.getString(INDEX_SALES_DETAILS));
                sale.setEmpId(id);
                sale.setDate(results.getString(INDEX_SALES_DATE));
                sales.add(sale);
            }
            return sales;
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    public boolean insertEmployee(Employee emp) throws SQLException {

        queryEmployees.setInt(1, emp.getId());
        ResultSet results = queryEmployees.executeQuery();

        if (!results.next()) {
            insertIntoEmployees.setInt(1, emp.getId());
            insertIntoEmployees.setString(2, emp.getName());

            int affectedRows = insertIntoEmployees.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert employee!");
            }
            return true;
        } else {
            // pop up dialog saying employee exists already.
            System.out.println("Employee exists already");
            return false;
        }
    }

    public boolean insertSale(Sale sale) throws SQLException {
        queryEmployees.setInt(1, sale.getEmpId());
        ResultSet results = queryEmployees.executeQuery();

        if (results.next()) {
            insertIntoSales.setString(1, sale.getDescription());
            insertIntoSales.setString(2, sale.getDetails());
            insertIntoSales.setInt(3, sale.getEmpId());
            insertIntoSales.setString(4, sale.getDate());

            int affectedRows = insertIntoSales.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert sale!");
            }
            return true;
        } else {
            System.out.println("That employee doesn't exist");
            return false;
        }
    }

    public boolean deleteEmployee(int id) throws SQLException {

        deleteEmployees.setInt(1, id);
        int affectedRows = deleteEmployees.executeUpdate();
        System.out.println(affectedRows);

        if (affectedRows != 1) {
            throw new SQLException("Affected rows value was different than 1");
        } else {
            return true;
        }
    }

    public boolean deleteSaleById(int id) throws SQLException {

        deleteSalesById.setInt(1, id);
        System.out.println("ID of current sale: " + id);
        int affectedRows = deleteSalesById.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Affected rows value from query was different than 1");
        } else {
            return true;
        }
    }

    public int highestSalesId() {
        try {
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(HIGHEST_SALES_ID);
            if (results.next()) {
                int highestId = results.getInt(1);
                return highestId + 1;
            } else {
                // If no sale ID's in database, give a starting value at 1.
                return 1;
            }
        } catch (SQLException e) {
            System.out.println("Error, couldn't get the highest ID from database");
            return 0;
        }
    }
}