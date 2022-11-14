package com.labs.UIAPI;

public interface ICommand<T> extends IObjectiveCommand {
    public CommandResult<T> execute();
}
