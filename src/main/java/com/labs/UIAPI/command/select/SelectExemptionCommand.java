package com.labs.UIAPI.command.select;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.core.entity.Exemption;
import com.labs.core.helper.StringableTable;
import com.labs.core.service.ISelector;

public class SelectExemptionCommand implements ICommand<StringableTable<Exemption>> {
    private ISelector Selector;
    private SelectorConfiguration Conf;
    private boolean Configured = false;
    
    public SelectExemptionCommand(SelectorConfiguration conf){
        Conf = conf;
    }

    public void SetServices(ISelector selector){
        Selector = selector;
        Configured = true;
    }

    @Override
    public CommandResult<Object> executeAsObjective() {;
        if(!Configured)
            return new CommandResult<Object>(null, "FATAL: Command was not configured", false);
        
        Conf.configure(Selector);
        Exemption[] rs = Selector.selectExemption();
        if(rs == null)
            return new CommandResult<Object>(null, Selector.getLastOperationStatus(), false);
        if(Conf.SelectN != null && !Selector.selectOfIndex(Conf.SelectN-1, Exemption.class))
            return new CommandResult<Object>(null, Selector.getLastOperationStatus(), false);

        StringableTable<Exemption> resultive = new StringableTable<>(rs, true, 2, "Title", "Formula");
        return new CommandResult<Object>(resultive, "Found " + resultive.size() + " Entries", true);
    }

    @Override
    public CommandResult<StringableTable<Exemption>> execute() {
        if(!Configured)
            return new CommandResult<StringableTable<Exemption>>(null, "FATAL: Command was not configured", false);
        
        Conf.configure(Selector);
        Exemption[] rs = Selector.selectExemption();
        if(rs == null)
            return new CommandResult<StringableTable<Exemption>>(null, Selector.getLastOperationStatus(), false);
        if(Conf.SelectN != null && !Selector.selectOfIndex(Conf.SelectN-1, Exemption.class))
            return new CommandResult<StringableTable<Exemption>>(null, Selector.getLastOperationStatus(), false);

        StringableTable<Exemption> resultive = new StringableTable<>(rs, true, 2, "Title", "Formula");
        return new CommandResult<StringableTable<Exemption>>(resultive, "Found " + resultive.size() + " Entries", true);
    }
}
