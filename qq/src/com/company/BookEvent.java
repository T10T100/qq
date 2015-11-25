package com.company;

import java.util.EventObject;

/**
 * Created by k on 25.11.2015.
 */
public class BookEvent extends EventObject {
    private final int cause;
    private final String causeName;
    public BookEvent (Object o, int cause, String causeName)
    {
        super(o);
        this.cause = cause;
        this.causeName = causeName;
    }


    public int getCause()
    {
        return cause;
    }

    public String getCauseName()
    {
        return causeName;
    }
}
