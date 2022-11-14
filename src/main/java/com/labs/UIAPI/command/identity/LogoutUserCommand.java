package com.labs.UIAPI.command.identity;
import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.UserCommand;
import com.labs.core.service.IIdentityService;

public class LogoutUserCommand extends UserCommand<Object> {
    private boolean Configured = false;

    public void setServices(IIdentityService identity){
        Id = identity;
        Configured = true;
    }

    @Override
    protected CommandResult<Object> exectuteP() {
        if(!Configured)
        return new CommandResult<Object>(null, "FATAL: Command was not configured", false);

        boolean rs = Id.logOut();
        if(rs)
            return new CommandResult<Object>(null, "Successfuly logged out", true);
        return new CommandResult<Object>(null, Id.getLastOperationStatus(), false);
    }

    @Override
    protected CommandResult<Object> exectuteAsObjectiveP() {
        return exectuteP();
    }
}