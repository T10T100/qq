package com.company;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

/**
 * Created by k on 10.11.2015.
 */
public class TextMark {
    private final Highlighter.HighlightPainter highLight;
    private final int start;
    private final int end;
    public  TextMark (int start, int end, Color color)
    {
        this.highLight = new DefaultHighlighter.DefaultHighlightPainter(color);
        this.start = start;
        this.end = end;
    }


    public Highlighter.HighlightPainter getHighLight() {
        return highLight;
    }

    public int getEnd()
    {
        return end;
    }

    public int getStart()
    {
        return start;
    }
}
