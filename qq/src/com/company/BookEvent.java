package com.company;

import java.util.EventObject;

/**
 * Created by k on 13.11.2015.
 */
public class BookEvent extends EventObject {
    private final String cause;
    public BookEvent (Object o, String cause)
    {
        super(o);
        this.cause = cause;
    }



    public String getCause ()
    {
        return this.cause;
    }
}
