package com.company;

import sun.management.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.FileSystem;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by k on 28.10.2015.
 */
public class PathComparator extends LinkedList<String> {

    public PathComparator ()
    {

    }
    public PathComparator (String... strings)
    {
        for (String s : strings) {
            add(s);
        }
    }

    public void setArgs (String... strings)
    {
        removeAll(this);
        for (String s : strings) {
            add(s);
        }
    }
    public void setArg (String s)
    {
        removeAll(this);
        add(s);
    }

    public void setArgSeparatedByComma (String commaString)
    {
        LinkedList<String> args = new LinkedList<>();

    }

    public void setError ()
    {
        this.errorFlag = true;
    }

    public void setError (boolean value)
    {
        this.errorFlag = value;
    }

    public boolean getError ()
    {
        return this.errorFlag;
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
        totalWatched = 0;
        totalSize = 0;
        totalMatchedSize = 0;
        totalMissMatched = 0;
        totalMatched = 0;
        totalBroken = 0;
        errorFlag = false;
    }

    private boolean compareAndToArgs (String string)
    {
        for (String s : this)
        {
            if (s != null) {
                if (s.contains("'any'") == false) {
                    if (string.contains(s) == false) {
                        return false;
                    }
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    private boolean compareOrToArgs (String string)
    {
        for (String s : this)
        {
            if (s != null) {
                if (s.contains("'any'") == false) {
                    if (string.contains(s) == true) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean compareToArgs (String string, boolean compareLogic)
    {
        if (compareLogic == true) {
            return this.compareAndToArgs(string);
        } else {
            return this.compareOrToArgs(string);
        }
    }

    public boolean compareAndCollect (File file, boolean compareLogic)
    {
        if (isEmpty() == true) {
            return false;
        }
        long size = file.length();
        totalSize += size;
        totalWatched++;

        if (this.compareToArgs(file.getName(), compareLogic) == true) {
            totalMatched++;
            totalMatchedSize += size;
            return true;
        } else {
            totalMissMatched++;
            return false;
        }
    }

    private String printSize (long size)
    {
        String s = "";
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

    public String getLastCompResultAsString ()
    {
        long timeDifference = Math.abs(timeEnd - timeStart);
        timeDifference /= 1000000000;
        int seconds = (int)timeDifference % 60;
        timeDifference /= 60;
        int minutes = (int)timeDifference % 60;
        timeDifference /= 60;
        int hours = (int)timeDifference % 24;
        LocalTime time = LocalTime.of(hours, minutes, seconds, 0);
        String keys = "";
        for (String k : this) {
            if (k != null) {
                keys += " \'" + k + "\'";
            }
        }
        String s =
                        "Keys :                   " + keys + "\n" +
                        "Watched :              \"" + Long.toString(totalWatched) + "\" items.\n" +
                        "Matched :              \"" + Integer.toString(totalMatched) + "\" items.\n" +
                        "MissMatched :          \"" + Integer.toString(totalMissMatched) + "\" items.\n" +
                        "Total size :             " + this.printSize(totalSize) + '\n' +
                        "Total size of matched:   " + this.printSize(totalMatchedSize) + '\n' +
                        "Total Broken files :   \"" + Long.toString(totalBroken) + "\" items.\n" +
                        "Time Lapsed :          \"" + time.toString() + "\"\n";
        FileSystem fs = FileSystems.getDefault();
        s += fs.toString();
        return s;

    }

    private long totalSize;
    private long totalMatchedSize;
    private long totalWatched;
    private int  totalMatched;
    private int  totalMissMatched;
    private int  totalBroken;
    private boolean errorFlag;
    private long timeStart;
    private long timeEnd;
}
