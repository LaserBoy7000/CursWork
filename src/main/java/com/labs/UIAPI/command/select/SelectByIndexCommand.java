package com.labs.UIAPI.command.select;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.core.service.ISelector;

public class SelectByIndexCommand implements ICommand<Object> {
    private ISelector Selector;
    private int index;
    private boolean Configured = false;
    
    public SelectByIndexCommand(int index){
        this.index = index;
    }

    public void setServices(ISelector selector){
        Selector = selector;
        Configured = true;
    }


    @Override
    public CommandResult<Object> executeAsObjective() {
        if(!Configured)
        return new CommandResult<Object>(null, "FATAL: Command was not configured", false);

        Selector.setFromPrevious(true);
        boolean rs = Selector.selectOfIndex(index, Selector.getLastSelectionType());
        if(rs == false)
            return new CommandResult<Object>(null, Selector.getLastOperationStatus(), false);
        else  return new CommandResult<Object>(null, Selector.getLastOperationStatus(), true);
    }

    @Override
    public CommandResult<Object> execute() {
        return executeAsObjective();
    }
}
