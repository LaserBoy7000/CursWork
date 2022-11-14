package com.labs.core.service;

import java.util.Date;
import com.google.inject.ImplementedBy;
import com.labs.core.service.impl.CreatorService;
//Service for entity creation
@ImplementedBy(CreatorService.class)
public interface ICreator {
    public boolean createTax(String name, double tipp, double wc, boolean allowsExemption, String formula);
    public boolean createIncome(Date date, String title, double value);
    public boolean createExemption(String name, String formula);
    public String getLastOperationStatus();
}
