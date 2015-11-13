package com.company;

/**
 * Created by k on 13.11.2015.
 */
public class PathWatcherStatus {
    private final String statusName;
    public PathWatcherStatus (String name)
    {
        this.statusName = name;
    }


    public String getStatusName()
    {
        return statusName;
    }

    @Override
    public String toString()
    {
        return statusName;
    }
}
