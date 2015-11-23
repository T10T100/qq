package com.company;

import org.w3c.dom.Attr;

import java.time.DateTimeException;
import java.time.LocalTime;

/**
 * Created by k on 23.11.2015.
 */
public class AttributesFormatter {
    private String format;


    public  AttributesFormatter ()
    {
        this.format = "";
    }
    public AttributesFormatter (String format)
    {
        this.format = format;
    }





    public String printSize (long size)
    {
        char prefix = ' ';
        if (size > 2000000000) {
            size /= 1073741823;
            prefix = 'G';
        } else if (size > 2000000) {
            size /= 1048575;
            prefix = 'M';
        } else if (size > 20000) {
            size /= 1024;
            prefix = 'K';
        } else {
        }
        return Long.toString(size) + ' ' + prefix + "Bytes";
    }

    public String printTime (long mills)
    {
        mills = Math.abs(mills);
        int seconds = (int)(mills / 1000);
        int minutes = (seconds / 60) % 60;
        int hours = (minutes / 60) % 24;
        LocalTime time;
        try {
            time = LocalTime.of(seconds, minutes, hours, (int)mills);
            return time.toString();
        } catch (DateTimeException exception) {
            return "time exception!";
        }

    }
}
