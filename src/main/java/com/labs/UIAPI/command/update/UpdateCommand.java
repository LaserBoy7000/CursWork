package com.labs.UIAPI.command.update;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.core.service.IWriter;

public class UpdateCommand implements ICommand<Object> {

    private IWriter wr;
    private boolean configured = false;
    private String Property;
    private Class<?> Type;
    private Object Value;

    public UpdateCommand(String property, Object value, Class<?> type){
        Property = property; Value = value; Type = type;
    }

    public void setServices(IWriter writer) {
        configured = true;
        wr = writer;
    }

    @Override
    public CommandResult<Object> executeAsObjective() {
        return execute();
    }

    @Override
    public CommandResult<Object> execute() {
        if(!configured)
            return new CommandResult<Object>(null, "FATAL: Command was not configured", false);
        
        if(wr.update(Property, Value, Type))
            return new CommandResult<Object>(null, "Updated successfuly", true);
        return new CommandResult<Object>(null, wr.getLastOperationStatus(), false);
    }
    
}
