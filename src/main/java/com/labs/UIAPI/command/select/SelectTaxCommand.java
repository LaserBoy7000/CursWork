package com.labs.UIAPI.command.select;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.core.entity.Tax;
import com.labs.core.helper.StringableTable;
import com.labs.core.service.ISelector;

public class SelectTaxCommand implements ICommand<StringableTable<Tax>> {
    private ISelector Selector;
    private SelectorConfiguration Conf;
    private boolean Configured = false;
    
    public SelectTaxCommand(SelectorConfiguration conf){
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
        Tax[] rs = Selector.selectTax();
        if(rs == null)
            return new CommandResult<Object>(null, Selector.getLastOperationStatus(), false);
        if(Conf.SelectN != null && !Selector.selectOfIndex(Conf.SelectN-1, Tax.class))
            return new CommandResult<Object>(null, Selector.getLastOperationStatus(), false);

        StringableTable<Tax> resultive = new StringableTable<>(rs, true, 3, "Title", "TIPP", "WC", "AllowsExemption", "Formula");
        return new CommandResult<Object>(resultive, "Found " + resultive.size() + " Entries", true);
    }

    @Override
    public CommandResult<StringableTable<Tax>> execute() {
        if(!Configured)
        return new CommandResult<StringableTable<Tax>>(null, "FATAL: Command was not configured", false);

        Conf.configure(Selector);
        Tax[] rs = Selector.selectTax();
        if(rs == null)
            return new CommandResult<StringableTable<Tax>>(null, Selector.getLastOperationStatus(), false);
        if(Conf.SelectN != null && !Selector.selectOfIndex(Conf.SelectN-1, Tax.class))
            return new CommandResult<StringableTable<Tax>>(null, Selector.getLastOperationStatus(), false);

        StringableTable<Tax> resultive = new StringableTable<>(rs, true, 3, "Title", "TIPP", "WC", "AllowsExemption", "Formula");
        return new CommandResult<StringableTable<Tax>>(resultive, "Found " + resultive.size() + " Entries", true);
    }
}
