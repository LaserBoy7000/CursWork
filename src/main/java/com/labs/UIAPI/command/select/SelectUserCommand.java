package com.labs.UIAPI.command.select;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.core.entity.User;
import com.labs.core.helper.StringableTable;
import com.labs.core.service.ISelector;

public class SelectUserCommand  implements ICommand<StringableTable<User>> {
    private ISelector Selector;
    private SelectorConfiguration Conf;
    private boolean Configured = false;
    
    public SelectUserCommand(SelectorConfiguration conf){
        Conf = conf;
    }

    public void setServices(ISelector selector){
        Selector = selector;
        Configured = true;
    }

    @Override
    public CommandResult<Object> executeAsObjective() {
        if(!Configured)
        return new CommandResult<Object>(null, "FATAL: Command was not configured", false);

        Conf.configure(Selector);
        User[] rs = Selector.selectUser();
        if(rs == null)
            return new CommandResult<Object>(null, Selector.getLastOperationStatus(), false);
        if(Conf.SelectN != null && !Selector.selectOfIndex(Conf.SelectN-1, User.class))
            return new CommandResult<Object>(null, Selector.getLastOperationStatus(), false);

        StringableTable<User> resultive = new StringableTable<>(rs, true, 2, "Name", "Surname", "Children", "ChildrenD", "Exemption");
        return new CommandResult<Object>(resultive, "Found " + resultive.size() + " Entries", true);
    }

    @Override
    public CommandResult<StringableTable<User>> execute() {
        if(!Configured)
        return new CommandResult<StringableTable<User>>(null, "FATAL: Command was not configured", false);

        Conf.configure(Selector);
        User[] rs = Selector.selectUser();
        if(rs == null)
            return new CommandResult<StringableTable<User>>(null, Selector.getLastOperationStatus(), false);
        if(Conf.SelectN != null && !Selector.selectOfIndex(Conf.SelectN-1, User.class))
            return new CommandResult<StringableTable<User>>(null, Selector.getLastOperationStatus(), false);

        StringableTable<User> resultive = new StringableTable<>(rs, true, 2, "Name", "Surname", "Children", "ChildrenD", "Exemption");
        return new CommandResult<StringableTable<User>>(resultive, "Found " + resultive.size() + " Entries", true);
    }
}
