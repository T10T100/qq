package com.company;

import java.awt.*;

/**
 * Created by k on 09.11.2015.
 */
public class textBoundedItem {
    private boolean needHighlight;
    private Color color;
    private String text;

    public textBoundedItem ()
    {
        this.needHighlight = false;
        this.color = Color.RED;
        this.text = "'text'";
    }
    public textBoundedItem (String text)
    {

    }
}
