package com.labs.UIAPI.command.identity;
import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.core.service.IIdentityService;

public class AuthorizeUserCommand implements ICommand<Object> {
    private IIdentityService id;
    private String Name;
    private String Surname;
    private String Password;
    private boolean Configured = false;

    public AuthorizeUserCommand(String name, String surname, String password){
        Name = name;
        Surname = surname;
        Password = password;
    }

    public void setServices(IIdentityService identity){
        id = identity;
        Configured = true;
    }


    @Override
    public CommandResult<Object> executeAsObjective() {
        if(!Configured)
        return new CommandResult<Object>(null, "FATAL: Command was not configured", false);

        boolean rs = id.register(Name, Surname, Password);
        if(rs)
            return new CommandResult<Object>(null, "Authorization was successful", true);
        return new CommandResult<Object>(null, id.getLastOperationStatus(), false);
    }

    @Override
    public CommandResult<Object> execute() {
        return executeAsObjective();
    }
    
}
