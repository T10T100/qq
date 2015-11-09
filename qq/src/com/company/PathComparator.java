package com.company;

import sun.management.*;
import sun.security.timestamp.Timestamper;

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
    private long watchErrors;
    ArrayList<pathCompareKey> keys = new ArrayList<>();

    public PathComparator ()
    {
        this.totalSize = 0;
        this.watched = 0;
        this.watchErrors = 0;
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
        this.watchErrors = 0;
    }

    public boolean compareAndCollect (File file, boolean logic)
    {
        this.watched++;
        this.totalSize += file.length();
        for (pathCompareKey key : keys) {
            key.compare(file.getName(), file.length(), logic);
        }
        return true;
    }

    public String getSystemInfo ()
    {
        FileSystem system = FileSystems.getDefault();
        return system.toString();
    }

    @Override
    public String toString()
    {

        String output = "Watch time : " + this.printTime(this.timeEnd - this.timeStart) + "\n";
        output += "Watched : \"" + Long.toString(this.watched) + "\" paths;\n" +
                  "Total Size : " + this.printSize(this.totalSize) + "\n\n";
        for (pathCompareKey key : keys) {
            output += key.toString();
        }
        return output;
    }

    private String printSize (long size)
    {
        String s;
        if (size > 2000000000) {
            size /= 1000000000;
            s = "GBytes";
        } else if (size > 2000000) {
            size /= 1000000;
            s = "MBytes";
        } else if (size > 20000) {
            size /= 1000;
            s = "KBytes";
        } else {
            s = "Bytes";
        }
        return "\"" + Long.toString(size) + "\" " + s;
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
}
