package com.company;

import java.util.EventListener;

/**
 * Created by k on 13.11.2015.
 */
public interface PathWatcherListener extends EventListener {
    void actionPerformed (PathWatcherEvent event);
}
