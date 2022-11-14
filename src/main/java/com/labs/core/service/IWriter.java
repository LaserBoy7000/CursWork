package com.labs.core.service;

import com.google.inject.ImplementedBy;
import com.labs.core.service.impl.Writer;

//This service does single item updating and deleting of object
//based on selector internal bufer
//In some cases it also checks data consistency and causes
//recalculations
@ImplementedBy(Writer.class)
public interface IWriter {
    public boolean update(String param, Object value, Class<?> type);
    public boolean remove(boolean allSelection);
    public String getLastOperationStatus();
}
