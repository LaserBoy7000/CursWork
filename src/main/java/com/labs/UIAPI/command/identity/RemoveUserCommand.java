package com.labs.UIAPI.command.identity;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.core.service.IIdentityService;

public class RemoveUserCommand implements ICommand<Object> {
    private String Name;
    private String Surname;
    private boolean Configured = false;
    private IIdentityService Id;

    public RemoveUserCommand(String name, String surname){
        Name = name; Surname = surname;
    }

    public void setServices(IIdentityService identity){
        Id = identity;
        Configured = true;
    }

    @Override
    public CommandResult<Object> executeAsObjective() {
        if(!Configured)
        return new CommandResult<Object>(null, "FATAL: Command was not configured", false);

        boolean rs = Id.removeUser(Name, Surname);
        if(rs)
            return new CommandResult<Object>(null, "User " +Name +' ' +Surname+ " was Successfuly removed", rs);
        else return new CommandResult<Object>(null, Id.getLastOperationStatus(), rs);
    }

    @Override
    public CommandResult<Object> execute() {
        return executeAsObjective();
    }
    
}
