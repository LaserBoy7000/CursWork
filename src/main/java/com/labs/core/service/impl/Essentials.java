package com.labs.core.service.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.inject.Inject;
import com.labs.UIAPI.command.select.SelectorConfiguration;
import com.labs.core.entity.Income;
import com.labs.core.entity.Tax;
import com.labs.core.service.IEssentials;
import com.labs.core.service.ISelector;

public class Essentials implements IEssentials {

    private ISelector Selector;
    private String Status;

    @Inject
    public Essentials(ISelector selector) {
        Selector = selector;
    }

    @Override
    public Object[][] GenerateYearlyReport() {
        String y = Calendar.getInstance().get(Calendar.YEAR) + "-";
        SelectorConfiguration c = new SelectorConfiguration();
        c.configure(Selector);
        Income[] i = Selector.selectIncome();
        if(i == null){
            setStatus(Selector.getLastOperationStatus());
            return null;
        }
        c.LexemParam = "Date";
        c.Lexem = y;
        c.FromPrevious = true; 
        c.configure(Selector);
        i = Selector.selectIncome();
        Selector.clearContext();
        if(i == null){
            setStatus("WARNING: Could not find any incomes");
            return null;
        }
        Map<String, Double> taxes = new HashMap<>();
        Arrays.stream(i).forEach((inc) -> {
            Tax tx = inc.getTax();
            if(tx != null){
                double v = inc.getValueTaxed();
                if(taxes.containsKey(tx.getTitle())){
                    v += taxes.get(tx.getTitle());
                    taxes.replace(tx.getTitle(), v);
                }
                else taxes.put(tx.getTitle(), inc.getValueTaxed());
            }
        });

        int sz = 2+taxes.size();
        Object[][] rs = new Object[sz][2];
        rs[0] = new Object[]{"Tax", "TotalYearly"};
        Iterator<String> keys = taxes.keySet().iterator();
        double total = 0.0;
        int r = 1;
        for(; r < sz - 1; r++){
            String vl = keys.next();
            Double vln = taxes.get(vl);
            total += vln;
            rs[r] = new Object[]{vl, vln};
        }
        rs[r] = new Object[]{"ALL", total};
       
        return rs;
    }

    private void setStatus(String status){
        Status = status;
    }

    @Override
    public String getLastOperationStatus() {
        return Status;
    }
    
}
