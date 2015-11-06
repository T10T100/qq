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
public class PathComparator extends ArrayList<pathCompareKey> {

    private long timeStart;
    private long timeEnd;

    public PathComparator ()
    {

    }

    public void setUp (ArrayList<Word> words)
    {
        this.removeAll(this);
        for (Word word : words) {
            this.add(new pathCompareKey(word));
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
        for (pathCompareKey key : this) {
            //key.clearAll();
        }
    }

    public boolean compareAndCollect (File file, boolean logic)
    {
        boolean result = false;
        for (pathCompareKey key : this) {
            result |= key.compare(file.getName(), file.length(), logic);
        }
        return result;
    }


    @Override
    public String toString()
    {
        String output = "";
        for (pathCompareKey key : this) {
            output += key.toString();
        }
        return output;
    }
}
