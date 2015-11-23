package com.company;

import org.w3c.dom.Attr;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.Date;

/**
 * Created by k on 23.11.2015.
 */
public class AttributesFormatter {
    private String format;
    private DateFormat dateFormat;

    public  AttributesFormatter ()
    {
        this.format = "";
        dateFormat = new SimpleDateFormat("YYYY:MM:dd - HH:mm:ss");
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

    public String showPathInfo (Path path)
    {
        if (path == null) {
            return "";
        }
        String message = new String();
        File file = path.toFile();
        if (file.isDirectory() == true) {
            message += "Directory \"" +
                    file.getName() + "\"\n";
            int dirs = 0;
            int files = 0;
            int unrec = 0;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path child : stream) {
                    if (Files.exists(child) == true) {
                        if (Files.isDirectory(child) == true) {
                            dirs++;
                        } else {
                            files++;
                        }
                    } else {
                        unrec++;
                    }
                }
            }
            catch (IOException e){

            }
            message += "Inside : \n";
            message += "\"" + Integer.toString(dirs) + "\" Folders\n";
            message += "\"" + Integer.toString(files) + "\" Files\n";
            message += "\"" + Integer.toString(unrec) + "\" Unrecognized\n";

        } else {
            message += "File \"" +
                    file.getName() + "\"\n" +
                    "Size \"" +
                    printSize(file.length()) + "\"\n" +
                    "Last modify :" +
                    dateFormat.format(new Date(file.lastModified()));
        }
        return message;
    }
}
