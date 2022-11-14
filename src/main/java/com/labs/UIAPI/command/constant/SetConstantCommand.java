package com.labs.UIAPI.command.constant;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.core.service.IConstantProvider;

public class SetConstantCommand implements ICommand<Object> {

    private String Name;
    private Double Value;
    private IConstantProvider Cp;
    private boolean Configured = false;

    public SetConstantCommand(String title, Double value){
        Name = title; Value = value;
    }

    public void setServices(IConstantProvider constantProvider){
        Cp = constantProvider;
        Configured = true;
    }

    @Override
    public CommandResult<Object> executeAsObjective() {
        if(!Configured)
        return new CommandResult<Object>(null, "FATAL: Command was not configured", false);

        boolean rs = Cp.setConstant(Name, Value);
        if(rs)
            return new CommandResult<Object>(null, Name + " was successfuly set to " + Value, true);
        return new CommandResult<Object>(null, Cp.getLastOperationStatus(), false);
    }

    @Override
    public CommandResult<Object> execute() {
        return executeAsObjective();
    }
}
