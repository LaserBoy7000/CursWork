package com.labs.UIAPI.command.create;


import java.util.Date;
import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.UserCommand;
import com.labs.core.service.ICreator;
import com.labs.core.service.IIdentityService;

public class CreateIncomeCommand extends UserCommand<Object> {
    private String Title;
    private double Value;
    private Date Date;
    private boolean Configured = false;
    private ICreator cr;

    public CreateIncomeCommand(String title, double value, Date date) {
        Title = title; Value = value; Date = date;
    }

    public void setServices(ICreator creator, IIdentityService identityService){
        cr = creator;
        Id = identityService;
        Configured = true;
    }

    @Override
    protected CommandResult<Object> exectuteP() {
        return exectuteAsObjectiveP();
    }

    @Override
    protected CommandResult<Object> exectuteAsObjectiveP() {
        if(!Configured)
        return new CommandResult<Object>(null, "FATAL: Command was not configured", false);

        boolean rs = cr.createIncome(Date, Title, Value);
        if(rs)
            return new CommandResult<Object>(null, "Income "+Title+" registered successfuly", true);
        return new CommandResult<Object>(null, cr.getLastOperationStatus(), true);
    }
}
