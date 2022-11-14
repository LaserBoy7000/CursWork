package com.labs.core.entity;

public class Tax {
    private int ID;   
    private String Title;
    private double TIPP;
    private double WC;
    private boolean AllowsExemption;
    private String Formula;

    public Tax(){}

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

    public double getTIPP() {
        return TIPP;
    }
    public void setTIPP(double tIPP) {
        TIPP = tIPP;
    }

    public double getWC() {
        return WC;
    }
    public void setWC(double wC) {
        WC = wC;
    }

    public boolean isAllowsExemption() {
        return AllowsExemption;
    }
    public void setAllowsExemption(boolean allowsExemption) {
        AllowsExemption = allowsExemption;
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
