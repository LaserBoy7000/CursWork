package com.labs.core.service.impl;

import com.google.inject.Inject;
import com.labs.core.entity.Exemption;
import com.labs.core.entity.Income;
import com.labs.core.entity.Tax;
import com.labs.core.entity.User;
import com.labs.core.service.ICalculator;
import com.labs.core.service.IExpressionEvaluator;

public class Calculator implements ICalculator {
    private IExpressionEvaluator ev;
    private String Status = "";

    @Inject
    public Calculator(IExpressionEvaluator evaluator){
        ev = evaluator;
    }

    @Override
    public Boolean isTaxApplyable(Tax tax, User user, Income income) {
        String[] eqs = tax.getFormula().split("#", -1);
        if(eqs.length == 1)
            return true;
        eqs[0] = eqs[0].replace("value", income.getValue()+"");
        Boolean rs = ev.calcBool(eqs[0], user, income);
        if(rs == null){
            setStatus(ev.getLastOperationStatus());
        }
        return rs;
    }

    @Override
    public double calculateTax(Tax tax, Income income, Exemption exemption, User user) {
        double txval = income.getValue();
        if(exemption != null && tax.isAllowsExemption()){
            txval = calculateExemption(exemption, income, user);
            if(Double.isNaN(txval))
                return txval;
        } else income.setExemption(null);

        String[] rs = tax.getFormula().split("#", -1);
        String fml = rs[rs.length-1];
        fml = fml.replaceAll("value", txval + "");
        if(fml.length() > 0)
            txval = ev.calcVal(fml, income, user);
        if(Double.isNaN(txval))
        {
            setStatus(ev.getLastOperationStatus());
            return txval;
        }
        return txval * (tax.getTIPP() + tax.getWC());
    }

    @Override
    public Boolean isExemptionApplyable(Exemption exemption, User user, Income income) {
        String[] eqs = exemption.getFormula().split("#", -1);
        if(eqs.length == 1)
            return true;
        eqs[0] = eqs[0].replace("value", income.getValue()+"");
        Boolean rs = ev.calcBool(eqs[0], user, income);
        if(rs == null)
            setStatus(ev.getLastOperationStatus());
        return rs;
    }

    private void setStatus(String status){
        Status = status;
    }

    @Override
    public String getLastOperaionStatus() {
        return Status;
    }

    @Override
    public double calculateExemption(Exemption exemption, Income income, User user) {
        double txval = income.getValue();
        String[] rs = exemption.getFormula().split("#", -1);
        String fml = rs[rs.length-1];
        fml = fml.replaceAll("value", income.getValue() + "");
        if(fml.length() > 0)
            txval = ev.calcVal(fml, user, income);
        
        if(Double.isNaN(txval)){
            setStatus(ev.getLastOperationStatus());
            return txval;
        }

        return txval >= 0 ? txval : 0;
    }
    
}
