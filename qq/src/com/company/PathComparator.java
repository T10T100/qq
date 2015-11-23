package com.company;

import java.awt.*;
import java.io.File;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created by k on 28.10.2015.
 */
public class PathComparator {

    private long timeStart;
    private long timeEnd;
    private long totalSize;
    private long watched;
    ArrayList<PathKey> keys = new ArrayList<>();

    public PathComparator ()
    {
        this.totalSize = 0;
        this.watched = 0;
    }

    public void setUp (ArrayList<Word> words)
    {
        keys.removeAll(keys);
        for (Word word : words) {
            keys.add(new PathKey(word));
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
        for (PathKey key : keys) {
            key.clearAll();
        }
        this.watched = 0;
        this.totalSize = 0;
    }

    public boolean compareAndCollect (File file)
    {
        this.watched++;
        this.totalSize += file.length();
        for (PathKey key : keys) {
            key.compare(file.getName(), file.length());
        }
        return true;
    }

    public boolean compareAndCollect (String name, long size)
    {
        this.watched++;
        this.totalSize += size;
        for (PathKey key : keys) {
            key.compare(name, size);
        }
        return true;
    }

    public ArrayList<textBoundedItem> getTextItems ()
    {
        ArrayList<textBoundedItem> items = new ArrayList<>();
        AttributesFormatter formatter = new AttributesFormatter();
        items.add(new textBoundedItem("Watch time : " + formatter.printTime(this.timeEnd - this.timeStart), Color.LIGHT_GRAY));
        items.add(new textBoundedItem("Watched :    " + Long.toString(this.watched) + " Paths", Color.LIGHT_GRAY));
        items.add(new textBoundedItem("Total Size : " + formatter.printSize(this.totalSize), new Color(227, 73, 59, 150)));

        for (PathKey key : keys) {
            items.addAll(key.getTextItems());
        }
        return items;
    }

    public void log (Book log)
    {
        log.cleanUp();
        AttributesFormatter formatter = new AttributesFormatter();
        log.write("Watch time : " + formatter.printTime(this.timeEnd - this.timeStart) + "\n");
        log.write("Watched : \"" + Long.toString(this.watched) + "\" Paths\n");
        log.write("Total Size : " + formatter.printSize(this.totalSize) + "\n\n");
        for (PathKey key : keys) {
            log.write(key.toString());
        }

        log.finish("PWLog");
    }

    public ArrayList<PathKey> getKeys()
    {
        return keys;
    }
}
