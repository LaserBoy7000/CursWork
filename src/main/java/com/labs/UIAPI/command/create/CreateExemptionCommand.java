package com.labs.UIAPI.command.create;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.core.service.ICreator;

public class CreateExemptionCommand implements ICommand<Object> {

    private ICreator cr;
    private String Title;
    private String Formula;
    private boolean Configured = false;

    public CreateExemptionCommand(String title, String formula){
        Title = title; Formula = formula;
    }

    public void setServices(ICreator creator){
        cr = creator;
        Configured = true;
    }

    @Override
    public CommandResult<Object> executeAsObjective() {
        if(!Configured)
        return new CommandResult<Object>(null, "FATAL: Command was not configured", false);

        boolean rs = cr.createExemption(Title, Formula);
        if(!rs)
            return new CommandResult<Object>(null, cr.getLastOperationStatus(), false);
        return new CommandResult<Object>(null, "Exemption created successfully", true);
    }

    @Override
    public CommandResult<Object> execute() {
        return executeAsObjective();
    }
    
}
