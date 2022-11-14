package com.labs.core.entity;

import java.util.List;

public class User {
    private int ID;
    private String Name;
    private String Surname;
    private int Children;
    private int ChildrenD;
    private String Password;
    private Exemption Exemption;
    private List<Income> Incomes;

    public User(){}
    public User(String name, String surname, int children, int childrenD, String password) {
        Name = name;
        Surname = surname;
        Children = children;
        ChildrenD = childrenD;
        Password = password;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public int getChildren() {
        return Children;
    }

    public void setChildren(int children) {
        Children = children;
    }

    public int getChildrenD() {
        return ChildrenD;
    }

    public void setChildrenD(int childrenD) {
        ChildrenD = childrenD;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public Exemption getExemption() {
        return Exemption;
    }

    public void setExemption(Exemption exemption) {
        Exemption = exemption;
    }

    public List<Income> getIncomes() {
        return Incomes;
    }

    public void setIncomes(List<Income> incomes) {
        Incomes = incomes;
    }
}
