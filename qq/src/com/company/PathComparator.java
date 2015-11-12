package com.company;

import sun.management.*;
import sun.security.timestamp.Timestamper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.FileSystem;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by k on 28.10.2015.
 */
public class PathComparator {

    private long timeStart;
    private long timeEnd;
    private long totalSize;
    private long watched;
    ArrayList<pathCompareKey> keys = new ArrayList<>();

    public PathComparator ()
    {
        this.totalSize = 0;
        this.watched = 0;
    }

    public void setUp (ArrayList<Word> words)
    {
        keys.removeAll(keys);
        for (Word word : words) {
            keys.add(new pathCompareKey(word));
        }
    }

    public void setTimeStart (long time)
    {
        this.timeStart = time;
    }

    public void setTimeEnd (long time)
    {
        this.timeEnd = time;
    }

    public void resetAll ()
    {
        for (pathCompareKey key : keys) {
            key.clearAll();
        }
        this.watched = 0;
        this.totalSize = 0;
    }

    public boolean compareAndCollect (File file)
    {
        this.watched++;
        this.totalSize += file.length();
        for (pathCompareKey key : keys) {
            key.compare(file.getName(), file.length());
        }
        return true;
    }

    public boolean compareAndCollect (String name, long size)
    {
        this.watched++;
        this.totalSize += size;
        for (pathCompareKey key : keys) {
            key.compare(name, size);
        }
        return true;
    }

    public String compareAndLog (String name, long size)
    {
        String log = "";
        int matched = 0;
        this.watched++;
        this.totalSize += size;
        boolean all = true;
        for (pathCompareKey key : keys) {
            if (key.compare(name, size) == true) {
                log += " [" + key.getKey().getName() + "] ";
                matched++;
            } else {
                all = false;
            }
        }
        if (all == true) {
            return "[ALL]";
        }
        return "(" + Integer.toString(matched) + ")" + log;
    }

    public String getSystemInfo ()
    {
        return "???";
    }

    public ArrayList<textBoundedItem> getTextItems ()
    {
        ArrayList<textBoundedItem> items = new ArrayList<>();

        items.add(new textBoundedItem("Watch time : " + this.printTime(this.timeEnd - this.timeStart), Color.LIGHT_GRAY));
        items.add(new textBoundedItem("Watched :    " + Long.toString(this.watched) + " Paths", Color.LIGHT_GRAY));
        items.add(new textBoundedItem("Total Size : " + this.printSize(this.totalSize), new Color(227, 73, 59, 150)));

        for (pathCompareKey key : keys) {
            items.addAll(key.getTextItems());
        }
        return items;
    }

    @Override
    public String toString()
    {

        String output = "Watch time : " + this.printTime(this.timeEnd - this.timeStart) + "\n";
        output += "Watched : \"" + Long.toString(this.watched) + "\" Paths\n" +
                  "Total Size : " + this.printSize(this.totalSize) + "\n\n";
        for (pathCompareKey key : keys) {
            output += key.toString();
        }
        return output;
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

    public String printTime (long nanos)
    {
        nanos = Math.abs(nanos);
        int seconds = (int)(nanos / 1000000000);
        int minutes = (seconds / 60) % 60;
        int hours = (minutes / 60) % 24;
        LocalTime time = LocalTime.of(seconds, minutes, hours, 0);
        return time.toString();
    }

    public String printKeys ()
    {
        String output = "Keys (" + Integer.toString(keys.size()) + ") -> ";
        for (pathCompareKey key : keys) {
            output += "[" + key.getKey().getName() + "]-";
        }
        return output;
    }

    public int getKeyCount ()
    {
        return keys.size();
    }

}
