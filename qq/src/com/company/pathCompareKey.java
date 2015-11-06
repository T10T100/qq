package com.company;

/**
 * Created by k on 06.11.2015.
 */
public class pathCompareKey {
    private Word key;
    private int matched = 0;
    private int watched = 0;
    private long totalSize = 0;
    private int matchedToArgs = 0;

    public pathCompareKey (Word key)
    {
        this.key = key;
        this.watched = 1;
        this.matched = 0;
        this.totalSize = 0;
        this.matchedToArgs = 0;
    }

    public void clearAll ()
    {
        this.watched = 0;
        this.matched = 0;
        this.totalSize = 0;
        this.matchedToArgs = 0;
    }

    private boolean compareToName (String name, long size)
    {
        this.watched++;
        this.totalSize += size;
        if (name.contains(key.getName())) {
            this.matched++;
            return true;
        }
        return false;
    }

    private boolean compareToValue (String value, long size)
    {
        this.watched++;
        this.totalSize += size;
        if (value.contains(key.getValue())) {
            this.matched++;
            System.out.println(matched);
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

    public boolean compare (String arg, long size, boolean logic)
    {
        if (logic == true) {
            return this.compareToName(arg, size);
        } else {
            return this.compareToValue(arg, size);
        }
    }

    private String printSize (long size)
    {
        String s;
        if (size > 1000000000) {
            size /= 1000000000;
            s = "GBytes";
        } else if (size > 1000000) {
            size /= 1000000;
            s = "MBytes";
        } else if (size > 10000) {
            size /= 1000;
            s = "KBytes";
        } else {
            s = "Bytes";
        }
        return "\"" + Long.toString(size) + "\" " + s;
    }

    @Override
    public String toString()
    {
        String output = new String (this.key.toString() +
                "\nWatched : \"" +
                Integer.toString(this.watched) +
                "\" Paths\nTotal Size : " +
                this.printSize(this.totalSize) +
                "\nMatched : \"" +
                Integer.toString(this.matched) +
                "\" Paths\nMissmatched : \"" +
                Integer.toString(this.watched - this.matched) +
                "\" Items\nSize of matched : " +
                this.printSize(this.totalSize - this.matched) +
                "\n\n");
        return output;
    }
}
