package com.company;

import org.w3c.dom.Attr;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by k on 06.11.2015.
 */
public class PathKey {
    protected Word key;
    protected int matched;
    protected long matchedSize;
    protected int matchedToArgs;
    colorPalette palette;
    Color linkColor;

    public PathKey(Word key)
    {
        palette = new colorPalette();
        ArrayList<String> array = key.getArrayFromValueAndSetValueAsFirst('$', '$');
        if (array.isEmpty() == true) {
            linkColor = Color.black;
        } else {
            linkColor = palette.getColorByName(array.get(0));
        }
        this.key = key;
        this.matched = 0;
        this.matchedSize = 0;
        this.matchedToArgs = 0;
    }

    public void clearAll ()
    {
        this.matched = 0;
        this.matchedSize = 0;
        this.matchedToArgs = 0;
    }

    private boolean compareToName (String name, long size)
    {
        if (name.contains(key.getName()) == true) {
            this.matchedSize += size;
            this.matched++;
            return true;
        }
        return false;
    }

    private boolean compareToValue (String value, long size)
    {
        if (value.contains(key.getValue()) == true) {
            this.matchedSize += size;
            this.matched++;
            return true;
        }
        return false;
    }

    private boolean compareToArgs (String arg)
    {

        for (String string : this.key.getArgs()) {
            if (string.contains(arg) == true) {
                this.matchedToArgs++;
                return true;
            }
        }
        return false;
    }

    public boolean compare (String arg, long size)
    {
        return this.compareToName(arg, size);
    }


    public ArrayList<textBoundedItem> getTextItems ()
    {
        AttributesFormatter formatter = new AttributesFormatter();
        ArrayList<textBoundedItem> items = new ArrayList<>();
        items.add(new textBoundedItem(this.key.toString(), new Color(38, 17, 117, 100)));
        items.add(new textBoundedItem("Matched :         " + Integer.toString(this.matched) + " Paths"));
        items.add(new textBoundedItem("Size of matched : " + formatter.printSize(this.matchedSize) + '\n', new Color(224, 130, 30, 150)));
        return items;
    }

    @Override
    public String toString()
    {
        AttributesFormatter formatter = new AttributesFormatter();
        String output = new String (this.key.toString() +
                "\r\nMatched : \"" +
                Integer.toString(this.matched) +
                "\" Paths\r\nSize of matched : " +
                formatter.printSize(this.matchedSize) +
                "\r\n\n");
        return output;
    }

    public int getMatched()
    {
        return matched;
    }

    public int getMatchedToArgs() {
        return matchedToArgs;
    }

    public void setKey(Word key) {
        this.key = key;
    }

    public Word getKey() {
        return key;
    }

    public void setMatched(int matched) {
        this.matched = matched;
    }

    public void setMatchedToArgs(int matchedToArgs) {
        this.matchedToArgs = matchedToArgs;
    }

    public Color getLinkColor ()
    {
        return linkColor;
    }

}
