package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;

import sample.model.Datasource;
import sample.model.Employee;
import sample.model.Sale;

import java.time.format.DateTimeFormatter;

/**
 * Author: Asen Koparanski
 * Purpose:
 * Date: 05.02.2019
 */

public class Controller {

    @FXML
    private TableView<Sale> salesTable;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    public void listEmployees() {

        employeeTable.getSelectionModel().selectFirst();
        employeeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Employee>() {
            @Override
            public void changed(ObservableValue<? extends Employee> observable, Employee oldValue, Employee newValue) {
                if(newValue != null) {
                    final Employee emp = (Employee) employeeTable.getSelectionModel().getSelectedItem();
                    listSalesForEmployees(emp);
                }
            }
        });
        Task<ObservableList<Employee>> task = new GetAllEmployeesTask();
        employeeTable.itemsProperty().bind(task.valueProperty());
        progressBar.progressProperty().bind(task.progressProperty());

        progressBar.setVisible(true);

        task.setOnSucceeded(e -> progressBar.setVisible(false));
        task.setOnFailed(e -> progressBar.setVisible(false));

        new Thread(task).start();

    }

    @FXML
    public void listSalesForEmployees(Employee emp) {
        if (emp == null) {
            // pop-up dialog here.
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
        // figure out how to have 2 tableviews and switch between them
        salesTable.itemsProperty().bind(task.valueProperty());
//        salesTable.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
//        salesTable.refresh();
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