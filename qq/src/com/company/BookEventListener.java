package com.company;

import java.util.EventListener;

/**
 * Created by k on 25.11.2015.
 */
public interface BookEventListener extends EventListener {
    void eventPerformed (BookEvent o);
}
