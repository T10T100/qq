package com.company;

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

    public PathKey(Word key)
    {
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

    private String printSize (long size)
    {
        char prefix = ' ';
        if (size > 2000000000) {
            size /= 1000000000;
            prefix = 'G';
        } else if (size > 2000000) {
            size /= 1000000;
            prefix = 'M';
        } else if (size > 20000) {
            size /= 1000;
            prefix = 'K';
        } else {
        }
        return Long.toString(size) + ' ' + prefix + "Bytes";
    }

    public ArrayList<textBoundedItem> getTextItems ()
    {
        ArrayList<textBoundedItem> items = new ArrayList<>();
        items.add(new textBoundedItem(this.key.toString(), new Color(38, 17, 117, 100)));
        items.add(new textBoundedItem("Matched :         " + Integer.toString(this.matched) + " Paths"));
        items.add(new textBoundedItem("Size of matched : " + this.printSize(this.matchedSize) + '\n', new Color(124, 130, 30, 150)));
        return items;
    }

    @Override
    public String toString()
    {
        String output = new String (this.key.toString() +
                "\nMatched : \"" +
                Integer.toString(this.matched) +
                "\" Paths\nSize of matched : " +
                this.printSize(this.matchedSize) +
                "\n\n");
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


}
