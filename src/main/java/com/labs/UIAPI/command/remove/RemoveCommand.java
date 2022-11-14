package com.labs.UIAPI.command.remove;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.core.service.IWriter;

public class RemoveCommand implements ICommand<Object> {
    private IWriter Writer;
    private boolean allSelection;
    private boolean configured = false;

    public RemoveCommand(boolean allTheSelection){
        allSelection = allTheSelection;
    }

    public void setServices(IWriter writer){
        configured = true;
        Writer = writer;
    }

    @Override
    public CommandResult<Object> executeAsObjective() {
        return execute();
    }

    @Override
    public CommandResult<Object> execute() {
        if(!configured)
            return new CommandResult<Object>(null, "FATAL: Command was not configured", false);
        
        if(Writer.remove(allSelection))
            return new CommandResult<Object>(null, "Removed Successfuly", true);
        return new CommandResult<Object>(null, Writer.getLastOperationStatus(), false);
    }
    
}
