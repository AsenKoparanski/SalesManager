package sample.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Author: Asen Koparanski
 * Purpose:
 * Date: 05.02.2019
 */

public class Sale {

    private SimpleIntegerProperty id;
    private SimpleStringProperty description;
    private SimpleStringProperty details;
    private SimpleIntegerProperty empId;
    private SimpleStringProperty date;

    public Sale() {
        this.description = new SimpleStringProperty();
        this.details = new SimpleStringProperty();
        this.id = new SimpleIntegerProperty();
        this.empId = new SimpleIntegerProperty();
        this.date = new SimpleStringProperty();
    }
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
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

    public int getEmpId() {
        return empId.get();
    }

    public void setEmpId(int empId) {
        this.empId.set(empId);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }
}
