package com.company;

import sun.management.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.FileSystem;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
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
    ArrayList<pathCompareKey> keys = new ArrayList<>();

    public PathComparator ()
    {

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
            //key.clearAll();
        }
    }

    public boolean compareAndCollect (File file, boolean logic)
    {
        boolean result = false;
        for (pathCompareKey key : keys) {
            result |= key.compare(file.getName(), file.length(), logic);
        }
        return result;
    }

    public String getSystemInfo ()
    {
        FileSystem system = FileSystems.getDefault();
        return system.toString();
    }

    @Override
    public String toString()
    {
        String output = "";
        for (pathCompareKey key : keys) {
            output += key.toString();
        }
        return output;
    }
}
