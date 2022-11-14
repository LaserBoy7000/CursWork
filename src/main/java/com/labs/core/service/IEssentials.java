package com.labs.core.service;

import com.google.inject.ImplementedBy;
import com.labs.core.service.impl.Essentials;

//This service provides non-core functionality which can be more complicated
// than standard CRUD, for example reports generation
@ImplementedBy(Essentials.class)
public interface IEssentials {
    public Object[][] GenerateYearlyReport();
    public String getLastOperationStatus();
}
