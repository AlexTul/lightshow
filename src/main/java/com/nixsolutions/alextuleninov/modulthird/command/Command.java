package com.nixsolutions.alextuleninov.modulthird.command;

import com.nixsolutions.alextuleninov.modulthird.exceptions.LightshowException;

public interface Command<T> {

    T execute() throws LightshowException;

}
