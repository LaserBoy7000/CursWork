package com.labs.UIAPI.command.create;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.core.service.ICreator;

public class CreateTaxCommand implements ICommand<Object> {
    
    private ICreator cr;
    private String Title;
    private String Formula;
    private double TIPP;
    private double WC;
    private boolean AllowsExemption;
    private boolean Configured = false;

    public CreateTaxCommand(String title, String formula, boolean allowsExemption, double tipp, double wc){
        Title = title; Formula = formula;
        TIPP = tipp; WC = wc; AllowsExemption = allowsExemption;
    }

    public void setServices(ICreator creator){
        cr = creator;
        Configured = true;
    }

    @Override
    public CommandResult<Object> executeAsObjective() {
        if(!Configured)
        return new CommandResult<Object>(null, "FATAL: Command was not configured", false);

        boolean rs = cr.createTax(Title, TIPP, WC, AllowsExemption, Formula);
        if(rs)
            return new CommandResult<Object>(null, "Tax created successfuly", true);
        return new CommandResult<Object>(null, cr.getLastOperationStatus(), false);
    }

    @Override
    public CommandResult<Object> execute() {
        return executeAsObjective();
    }
}
