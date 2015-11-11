package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by k on 11.11.2015.
 */
public class PathsHashFile {
    private final String name;
    private final String location;
    private final File logFile;
    private boolean needUpdate;
    private boolean exist;

    private Map<String, ArrayList<String>> book;
    private ArrayList<String> paragraph;
    public PathsHashFile (String name, String location)
    {
        this.book = new HashMap<>();
        this.book.put("$", new ArrayList<>());
        this.paragraph = book.get("$");
        this.exist = false;
        this.needUpdate = true;
        this.name = name;
        this.location = location;
        Path path = Paths.get(location);
        if (Files.exists(path) == false) {
            logFile = null;
            return;
        }
        if (Files.isDirectory(path) == false) {
            logFile = null;
            return;
        }
        if (name.contains(".") == false) {
            logFile = null;
            return;
        }
        logFile = new File(location + File.separator + name);
            try {
                if (Files.deleteIfExists(logFile.toPath()) == true) {

                } else {

                }

            } catch (IOException exception) {

            }
            logFile.getParentFile().mkdirs();
            try {
                if (logFile.createNewFile() == true) {
                    this.exist = true;
                } else {

                }
            } catch (IOException exception) {

            }
    }

    public PathsHashFile (String name, File location)
    {
        this.book = new HashMap<>();
        this.book.put("$", new ArrayList<>());
        this.paragraph = book.get("$");
        this.exist = false;
        this.needUpdate = true;
        this.name = name;
        this.location = location.toString();
        if (name.contains(".") == false) {
            logFile = null;
            return;
        }
        logFile = new File(location.toPath().toString() + File.separator + name);
        try {
            if (Files.deleteIfExists(logFile.toPath()) == true) {
            } else {

            }

        } catch (IOException exception) {

        }
        logFile.getParentFile().mkdirs();
        try {
            if (logFile.createNewFile() == true) {
                this.exist = true;
            } else {

            }
        } catch (IOException exception) {

        }
    }

    public void setNewBook (String name)
    {
        this.book.clear();
        this.book.put(name, new ArrayList<>());
        this.paragraph = this.book.get(name);
        this.needUpdate = true;
    }

    public void setNewParagraph (String name)
    {
        this.book.put(name + "\r\n", new ArrayList<>());
        this.paragraph = this.book.get(name);
        this.needUpdate = true;
    }

    public void writeToCurrentParagraph (String line)
    {
        this.paragraph.add(line + "\r\n");
        this.needUpdate = true;
    }

    public void writeToExistingParagraph (String name, String line)
    {
        ArrayList<String> par = this.book.get(name);
        if (par != null) {
            par.add(line + "\r\n");
        } else {
            par = new ArrayList<>();
            par.add(line + "\r\n");
            this.book.put(name, par);
        }
        this.needUpdate = true;
    }




    public void writeTolog ()
    {
        if (this.exist == false) {
            return;
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(this.logFile, true);
        } catch (IOException exception) {

        }
        if (writer == null) {
            return;
        }
        String name = "";
        ArrayList<String> paragraph;
        Iterator<String> keyIterator = this.book.keySet().iterator();
        while (keyIterator.hasNext() == true) {
            name = keyIterator.next();
            paragraph = this.book.get(name);
            if (paragraph == null) {
                continue;
            }
            try {
                writer.write("\r\n" + name + "\r\n");
            } catch (IOException exception) {

            }
            for (String line : paragraph) {
                try {
                    writer.write(line);
                } catch (IOException exception) {

                }
            }
        }
        try {
            writer.close();
        } catch (IOException exception) {

        }
        this.needUpdate = false;
    }

    public void setNeedUpdate (boolean value)
    {
        this.needUpdate = value;
    }

    public boolean isNeedUpdate()
    {
        return needUpdate;
    }

    public boolean isExist()
    {
        return exist;
    }
}
