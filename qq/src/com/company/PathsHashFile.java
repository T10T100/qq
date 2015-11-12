package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.*;
import java.util.stream.Stream;

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
    public PathsHashFile (String name, String location, String bookName)
    {
        this.book = new HashMap<>();
        this.book.put(bookName, new ArrayList<>());
        this.paragraph = book.get(bookName);
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

    public PathsHashFile (String name, File location, String bookName)
    {
        this.book = new HashMap<>();
        this.book.put(bookName, new ArrayList<>());
        this.paragraph = book.get(bookName);
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
    }

    public void clear ()
    {
        this.book.clear();
    }

    public void setNewParagraph (String name)
    {
        this.book.put(name + "\r\n", new ArrayList<>());
        this.paragraph = this.book.get(name);
    }

    public void writeToCurrentParagraph (String line)
    {
        this.paragraph.add(line + "\r\n");
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
    }




    public void writeToLog ()
    {
        if (this.exist == false) {
            return;
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(this.logFile, false);
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
                writer.append("\r\n" + name + "\r\n");
            } catch (IOException exception) {

            }
            for (String line : paragraph) {
                try {
                    writer.append(line);
                } catch (IOException exception) {

                }
            }
        }
        try {
            writer.close();
        } catch (IOException exception) {

        }
    }

    public Stream<String> readLineByLine ()
    {
        Stream<String> output = null;
        if (this.logFile == null) {
            return output;
        }
        try {
            output = Files.lines(this.logFile.toPath(), Charset.forName("ISO-8859-1"));
        } catch (IOException exception) {
            System.out.println(exception.fillInStackTrace());
        }
        return output;
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
