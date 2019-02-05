package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import sample.model.Datasource;
import sample.model.Employee;
import sample.model.Sale;

/**
 * Author: Asen Koparanski
 * Purpose:
 * Date: 05.02.2019
 */

public class Controller {
    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private TableView<Sale> salesTable;

    public void listEmployees() {

        Task<ObservableList<Employee>> task = new GetAllEmployeesTask();

        employeeTable.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();

    }

    @FXML
    public void listSalesForEmployees() {
        final Employee emp = (Employee) employeeTable.getSelectionModel().getSelectedItem();
        if (emp == null) {
            System.out.println("No employee selected");
            return;
        }
        Task<ObservableList<Sale>> task = new Task<ObservableList<Sale>>() {
            @Override
            protected ObservableList<Sale> call() throws Exception {


                return FXCollections.observableArrayList(
                        Datasource.getInstance().querySaleForEmployeeId(emp.getId()));
            }
        };
        employeeTable.itemsProperty().bind(task.valueProperty());
    }
//    @FXML
//    public void handleClickListView() {
//        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
//        itemDetailsTextArea.setText(item.getDetails());
//        deadlineLabel.setText(item.getDeadline().toString());
//    }
}
// Helper class to run a background thread for a potentially longer task.
class GetAllEmployeesTask extends Task {
    @Override
    public ObservableList<Employee> call() {

        return FXCollections.observableArrayList
                (Datasource.getInstance().queryEmployees(Datasource.ORDER_BY_NONE));

    }
}