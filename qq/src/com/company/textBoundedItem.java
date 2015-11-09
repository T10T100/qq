package com.company;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

/**
 * Created by k on 09.11.2015.
 */
public class textBoundedItem {
    Highlighter.HighlightPainter highLight;
    private final String text;
    private final int length;

    public textBoundedItem ()
    {
        this.highLight = new DefaultHighlighter.DefaultHighlightPainter(Color.WHITE);
        this.text = "'text'\n";
        this.length = this.text.length();
    }
    public textBoundedItem (String text)
    {
        this.highLight = new DefaultHighlighter.DefaultHighlightPainter(Color.WHITE);
        this.text = text + '\n';
        this.length = this.text.length();
    }
    public textBoundedItem (String text, Color color)
    {
        this.highLight = new DefaultHighlighter.DefaultHighlightPainter(color);
        this.text = text + '\n';
        this.length = this.text.length();
    }

    public Highlighter.HighlightPainter getHighLight ()
    {
        return this.highLight;
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
