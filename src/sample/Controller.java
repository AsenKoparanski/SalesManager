package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import javafx.scene.layout.BorderPane;
import sample.model.Datasource;
import sample.model.Employee;
import sample.model.Sale;

import java.io.IOException;
import java.util.Optional;

/**
 * Author: Asen Koparanski
 * Purpose:
 * Date: 05.02.2019
 */

public class Controller {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private TableView<Sale> salesTable;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    public void getAllEmployees() {
        Task<ObservableList<Employee>> task = new GetAllEmployeesTask();
        employeeTable.itemsProperty().bind(task.valueProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        progressBar.setVisible(true);
        task.setOnSucceeded(e -> progressBar.setVisible(false));
        task.setOnFailed(e -> progressBar.setVisible(false));
        new Thread(task).start();
    }
    
    @FXML
    public void employeeClickListener() {
        employeeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Employee>() {
            @Override
            public void changed(ObservableValue<? extends Employee> observable, Employee oldValue, Employee newValue) {
                if(newValue != null) {
                    final Employee emp = (Employee) employeeTable.getSelectionModel().getSelectedItem();
                    listSalesForEmployees(emp);
                }
            }
        });
    }

    @FXML
    public void showAddEmployeeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("employeeDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Couldn't load dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            EmployeeDialogController controller = fxmlLoader.getController();
            employeeTable.getItems().add(controller.addEmployee());
        }
    }
    @FXML
    public void listSalesForEmployees(Employee emp) {
        Task<ObservableList<Sale>> task = new Task<ObservableList<Sale>>() {
            @Override
            protected ObservableList<Sale> call() throws Exception {
                return FXCollections.observableArrayList(
                        Datasource.getInstance().querySalesByEmployeeId(emp.getId()));
            }
        };
        salesTable.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
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