package com.labs.UIAPI.command.constant;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.core.entity.Constant;
import com.labs.core.helper.StringableTable;
import com.labs.core.service.IConstantProvider;

public class GetConstantsCommand implements ICommand<StringableTable<Constant>> {
    private IConstantProvider Cp;
    private boolean Configured = false;

    public void setServices(IConstantProvider constantProvider){
        Cp = constantProvider;
        Configured = true;
    }

    @Override
    public CommandResult<Object> executeAsObjective() {
        if(!Configured)
        return new CommandResult<Object>(null, "FATAL: Command was not configured", false);

        Constant[] arr = Cp.getAll();
        StringableTable<Constant> tbl = new StringableTable<Constant>(arr, false, 2, "Title", "Value");
        return new CommandResult<Object>(tbl, "Found "+arr.length+" constants available", true);
    }

    @Override
    public CommandResult<StringableTable<Constant>> execute() {
        if(!Configured)
        return new CommandResult<StringableTable<Constant>>(null, "FATAL: Command was not configured", false);

        Constant[] arr = Cp.getAll();
        StringableTable<Constant> tbl = new StringableTable<Constant>(arr, false, 2, "Title", "Value");
        return new CommandResult<StringableTable<Constant>>(tbl, "Found "+arr.length+" constants available", true);
    }
}
