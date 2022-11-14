package com.labs.core.entity;

public class Exemption {
    private int ID;
    private String Formula;
    private String Title;

    public int getID() {
        return ID;
    }
    public void setID(int iD) {
        ID = iD;
    }

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }
    
    public String getFormula() {
        return Formula;
    }
    public void setFormula(String formula) {
        Formula = formula;
    }

    @Override
    public String toString() {
        return Title;
    }
}
