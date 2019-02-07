package sample.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Author: Asen Koparanski
 * Purpose:
 * Date: 05.02.2019
 */

public class Sale {

    private SimpleStringProperty description;
    private SimpleStringProperty details;
    private SimpleIntegerProperty employeeId;
    private SimpleStringProperty date;

    public Sale() {
        this.description = new SimpleStringProperty();
        this.details = new SimpleStringProperty();
        this.employeeId = new SimpleIntegerProperty();
        this.date = new SimpleStringProperty();
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getDetails() {
        return details.get();
    }


    public void setDetails(String details) {
        this.details.set(details);
    }

    public int getEmployeeId() {
        return employeeId.get();
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId.set(employeeId);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }
}
