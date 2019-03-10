package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import javafx.scene.layout.BorderPane;
import sample.model.Datasource;
import sample.model.Employee;
import sample.model.Sale;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
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
    private TextField empNameTextField;

    @FXML
    private TextField empID;

    @FXML
    private TextField saleDescription;

    @FXML
    private TextArea saleDetails;

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
                    checkIfAnySaleRecords();
                    listSalesForEmployees(emp);
                    empNameTextField.setText(emp.getName());
                    empID.setText(String.valueOf(emp.getId()));
                    clearSales();
                }
            }
        });
    }
    public void clearSales() {
        saleDescription.setText("");
        saleDetails.setText("");
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

    @FXML
    public void saleClickListener() {
        salesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Sale>() {
            @Override
            public void changed(ObservableValue<? extends Sale> observable, Sale oldValue, Sale newValue) {
                if (newValue != null) {
                    final Sale sale = (Sale) salesTable.getSelectionModel().getSelectedItem();
                    saleDescription.setText(sale.getDescription());
                    saleDetails.setText(sale.getDetails());
                }
            }
        });
    }

    @FXML
    public void checkIfAnySaleRecords() {
        List<Sale> salesList = salesTable.getItems();
        if (salesList.isEmpty()) {
            salesTable.setPlaceholder(new Label("Employee has no sale records."));
        }
    }

    @SuppressWarnings("Duplicates")
    @FXML
    public void addEmployeeButton() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add Employee");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("addEmployeeDialog.fxml"));
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
            AddEmployeeDialog controller = fxmlLoader.getController();
            final Employee emp = (Employee) controller.addEmployee();

            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return Datasource.getInstance().insertEmployee(emp);
                }
            };
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent t) {
                    if (task.getValue()) {
                        employeeTable.getItems().add(emp);
                    } else {
                        System.out.println("Employee couldn't be added");
                    }

                }
            });
            new Thread(task).start();
        }
    }

    @FXML
    public void deleteEmployeeButton() {
        // prompt are you sure you want to delete employee - deleting will delete their sales bla bla
        final Employee emp = (Employee) employeeTable.getSelectionModel().getSelectedItem();
        if (emp != null) {
            try {
                if (Datasource.getInstance().deleteEmployee(emp.getId())) {
                    employeeTable.getItems().remove(emp);
                }
            } catch (SQLException e) {
                System.out.println("Failed to delete employee: " + e.getMessage());
            }
        } else {
            // Prompt to select an employee.
            System.out.println("Select an employee first!");
        }
    }

    @SuppressWarnings("Duplicates")
    @FXML
    public void addSaleButton() {
        final Employee emp = employeeTable.getSelectionModel().getSelectedItem();

        if (emp != null) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(mainBorderPane.getScene().getWindow());
            dialog.setTitle("Add Sale to Employee");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("addSaleDialog.fxml"));
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
                AddSaleDialog controller = fxmlLoader.getController();

                final Sale sale = controller.addSale(emp);
                if (sale != null) {
                    Task<Boolean> task = new Task<Boolean>() {
                        @Override
                        protected Boolean call() throws Exception {
                            sale.setId(Datasource.getInstance().highestSalesId());
                            return Datasource.getInstance().insertSale(sale);

                        }
                    };
                    task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent t) {
                            if (task.getValue()) {
                                salesTable.getItems().add(sale);
                            } else {
                                System.out.println("Sale couldn't be added");
                            }

                        }
                    });
                    new Thread(task).start();
                } else {
                    System.out.println("Sale was empty, please fill out all fields");
                }
            }
        } else  {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Please select an employee first!", ButtonType.OK);
            alert.setTitle("Unable to add sale.");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.showAndWait();

        }
    }

    @FXML
    public void deleteSaleButton() {
        Sale sale = (Sale) salesTable.getSelectionModel().getSelectedItem();

        if (sale != null) {
            try {
                if (Datasource.getInstance().deleteSaleById(sale.getId())) {
                    salesTable.getItems().remove(sale);
                }
            } catch (SQLException e) {
                System.out.println("Failed to delete sale: " + e.getMessage());
            }
        } else {
            // Prompt for sale selection!
            System.out.println("Select a sale first!");
        }
    }
}

// Helper class to run a background thread for a potentially longer task.
class GetAllEmployeesTask extends Task {
    @Override
    public ObservableList<Employee> call() {
        return FXCollections.observableArrayList
                (Datasource.getInstance().queryEmployees(Datasource.ORDER_BY_NONE));

    }
}

