package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import sample.model.Datasource;
import sample.model.Employee;

import java.sql.SQLException;

public class AddEmployeeDialog {

    @FXML
    private TextField empId;

    @FXML
    private TextField empName;


    public Employee addEmployee() {
        String idToInt = empId.getText().trim();
        int employeeId = Integer.parseInt(idToInt);

        String employeeName = empName.getText().trim();
        Employee emp = new Employee();
        emp.setId(employeeId);
        emp.setName(employeeName);

        try {
            Datasource.getInstance().insertEmployee(employeeId, employeeName);
        } catch (SQLException e) {
            System.out.println("Failed inserting employee" + e.getMessage());
        }
        return emp;
    }
}











