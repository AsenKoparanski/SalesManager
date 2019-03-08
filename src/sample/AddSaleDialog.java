package sample;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.model.Datasource;
import sample.model.Employee;
import sample.model.Sale;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class AddSaleDialog {

    @FXML
    private TextField descriptionField;

    @FXML
    private TextArea detailsArea;

    @FXML
    private DatePicker saleDatePicker;

    public Sale addSale(Employee emp) {

        String description = descriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        String saleDate = saleDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        if (description != null && details != null && saleDate != null) {
            Sale sale = new Sale();
            sale.setDescription(description);
            sale.setDetails(details);
            sale.setDate(saleDate);
            sale.setEmpId(emp.getId());

            return sale;
        }
        return null;
        // check if null
        // check if existing ID, send appropriate prompt message
//            sale.setId(Datasource.getInstance().highestSalesId());
//            Datasource.getInstance().insertSale(sale);
//        } catch (SQLException e) {
//            System.out.println("Failed to insert sale in database: " + e.getMessage());
//            return null;
//        }
//        return null;
    }
}
