package com.company;

import java.util.EventObject;

/**
 * Created by k on 13.11.2015.
 */
public class PathWatcherEvent extends EventObject {
    PathWatcherStatus status;
    public PathWatcherEvent (Object o, PathWatcherStatus status)
    {
        super(o);
        this.status = status;
    }


    public PathWatcherStatus getStatus ()
    {
        return this.status;
    }
}
