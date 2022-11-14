package com.labs.UIAPI;

public class CommandResult<T> {
    public final T Result;
    public final String Message;
    public final boolean IsSucceed;
    public CommandResult(T result, String message, boolean isSucceed){
        Result = result;
        Message = message;
        IsSucceed = isSucceed;
    }
}
