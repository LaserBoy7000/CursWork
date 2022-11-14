package com.labs.UIAPI;

import com.labs.core.service.IIdentityService;

public abstract class UserCommand<T> implements ICommand<T> {
    protected IIdentityService Id;

    @Override
    public CommandResult<T> execute(){
        if(!Id.isUserAvailable())
            return new CommandResult<T>(null, "WARNING: You cannot execute this command being unauthorized", false);
        return exectuteP();
    }

    @Override
    public CommandResult<Object> executeAsObjective(){
        if(!Id.isUserAvailable())
            return new CommandResult<Object>(null, "WARNING: You cannot execute this command being unauthorized", false);
        return exectuteAsObjectiveP();
    }

    protected abstract CommandResult<T> exectuteP();
    protected abstract CommandResult<Object> exectuteAsObjectiveP();
}
