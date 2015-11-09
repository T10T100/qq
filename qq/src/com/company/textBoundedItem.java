package com.company;

import java.awt.*;

/**
 * Created by k on 09.11.2015.
 */
public class textBoundedItem {
    private boolean needHighlight;
    private Color color;
    private String text;
    private int length;

    public textBoundedItem ()
    {
        this.needHighlight = false;
        this.color = Color.RED;
        this.text = "'text'\n";
        this.length = this.text.length();
    }
    public textBoundedItem (String text)
    {
        this.needHighlight = false;
        this.color = Color.RED;
        this.text = text + '\n';
        this.length = this.text.length();
    }
    public textBoundedItem (String text, boolean needHighlight, Color color)
    {
        this.needHighlight = needHighlight;
        this.color = color;
        this.text = text + '\n';
        this.length = this.text.length();
    }




    public boolean getHighlightFlag ()
    {
        return this.needHighlight;
    }
    public Color getColor ()
    {
        return this.color;
    }
    public int getLength()
    {
        return length;
    }

    @Override
    public String toString()
    {
        return text;
    }
}
