package com.company;

import java.util.EventListener;

/**
 * Created by k on 19.11.2015.
 */
public interface PathWatcherEventListener extends EventListener {
    public void eventPerformed (PathWatcherEvent e);
}
