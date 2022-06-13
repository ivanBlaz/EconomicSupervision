package com.devivan.economicsupervision.Objects.Account.Movements;

public class Movement {
    private int id;
    private int conceptId;
    private String date;
    public String location;
    private String subCategoryId;
    private String type;
    private double value;

    public Movement() {

    }

    public Movement(int id, int conceptId, String date, String location, String subCategoryId, String type, double value) {
        this.id = id;
        this.conceptId = conceptId;
        this.date = date;
        this.location = location;
        this.subCategoryId = subCategoryId;
        this.type = type;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConceptId() {
        return conceptId;
    }

    public void setConceptId(int conceptId) {
        this.conceptId = conceptId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
