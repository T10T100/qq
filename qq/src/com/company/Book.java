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
public class Book {
    private String name;
    private ArrayList<File> folders;
    private ArrayList<String> text;
    private ArrayList<Path> index;
    private ArrayList<Book> childs;
    private Path root;
    private final int linesLimit = 50000;
    private int linesCount;
    private String charsetName;

    private Vector<BookEventListener> listeners;



    public Book(File location, String bookName)
    {
        folders = new ArrayList<>();
        index = new ArrayList<>();
        text = new ArrayList<>();
        childs = new ArrayList<>();
        this.name = bookName;
        this.root = location.toPath();
        this.linesCount = 0;
        listeners = new Vector<>();
    }

    public void addEventListener (BookEventListener listener)
    {
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public void removeListener (BookEventListener listener)
    {
        listeners.remove(listener);
    }
    public void removeAlllisteners ()
    {
        listeners.removeAll(listeners);
    }

    private void fireEvents (int cause, String causeName)
    {
        Vector<BookEventListener> L = new Vector();
        synchronized (this) {
            L = (Vector)listeners.clone();
        }
        BookEvent event = new BookEvent(this, cause, causeName);
        for (BookEventListener l : L) {
            l.eventPerformed(event);
        }
    }

    public Book newBookWithin (String bookName)
    {
        File file = new File(root + File.separator + "LOG");
        if (file.exists() == false) {
            file.mkdir();
        }
        Book book = new Book(file, bookName);
        if (this.childs.contains(book) == false) {
            this.childs.add(book);
        }
        return book;
    }


    public File newDirectoryThere (String name)
    {
        String folderPath = root.toString() + File.separator + name;
        File file = new File(folderPath);
        if (file.exists() == true) {
            if (folders.contains(file) == false) {
                folders.add(file);
            }
            return file;
        }
        file.mkdir();
        return file;
    }

    public void write (String line)
    {
        text.add(line);
        linesCount++;
        if (linesCount >= linesLimit) {
            File file;
            FileWriter writer;
            String p = root + File.separator + Long.toString(System.currentTimeMillis()) + "." + this.name;
            file = new File(p);
            try {
                file.createNewFile();
            } catch (IOException exception) {
                fireEvents(0, "Cannot create a file there !");
                return;
            }
            try {
                try {
                    writer = new FileWriter(file, true);
                    while (text.isEmpty() == false) {
                        try {
                            writer.write("\r\n"  + text.remove(0) + " \r\n");
                        } catch (IOException exception) {
                            fireEvents(0, "Cannot write to file !");
                            return;
                        }
                    }
                    writer.close();
                } catch (IOException exception) {
                    fireEvents(0, "Cannot open file to write!");
                    return;
                }
            } catch (NullPointerException exception) {
                fireEvents(0, "Sompething points to 0");
                return;
            }
            linesCount = 0;
            index.add(Paths.get(p));
        }
    }

    public void finish ()
    {
        File file;
        FileWriter writer;
        String p = root + File.separator + Long.toString(System.currentTimeMillis()) + "." + this.name;
        if (linesCount > 0) {
            file = new File(p);
            index.add(Paths.get(p));
            try {
                file.createNewFile();
            } catch (IOException exception) {
                fireEvents(0, "Cannot create a file there !");
                return;
            }
            try {
                try {
                    writer = new FileWriter(file, false);
                    while (text.isEmpty() == false) {
                        try {
                            writer.append("\r\n" + text.remove(0) + " \r\n");
                        } catch (IOException exception) {
                            fireEvents(0, "Cannot write to file !");
                            return;
                        }
                    }
                    writer.close();
                } catch (IOException exception) {
                    fireEvents(0, "Cannot open file to write!");
                    return;
                }
            } catch (NullPointerException exception) {
                fireEvents(0, "Sompething points to 0");
                return;
            }
            linesCount = 0;
        }
    }

    public void finish (String name)
    {
        File file;
        FileWriter writer;
        String p = root + File.separator + name + "." + this.name;
        if (linesCount > 0) {
            file = new File(p);
            index.add(Paths.get(p));
            try {
                file.createNewFile();
            } catch (IOException exception) {
                return;
            }
            try {
                try {
                    writer = new FileWriter(file, false);
                    while (text.isEmpty() == false) {
                        try {
                            writer.append("\r\n" + text.remove(0) + " \r\n");
                        } catch (IOException exception) {
                            return;
                        }
                    }
                    writer.close();
                } catch (IOException exception) {
                    return;
                }
            } catch (NullPointerException exception) {
                return;
            }
            linesCount = 0;
        }
    }

    void cleanUp ()
    {
        for (Path p : index) {
            try {
                Files.deleteIfExists(p);
            } catch (IOException exception) {
                fireEvents(0, "File \"" + (p.toFile()).getName() +  "\" not existing !");
            }
        }
        index.removeAll(index);
        for (Book b : childs) {
            b.cleanUp();
        }
        /*
        for (File folder : folders) {
            try {
                Files.deleteIfExists(folder.toPath());
            } catch (IOException exception) {

            }
        }
        folders.removeAll(folders);
        */
    }
    void cleanAll ()
    {
        for (Path p : index) {
            try {
                Files.deleteIfExists(p);
                fireEvents(0, "File \"" + (p.toFile()).getName() +  "\" not existing !");
            } catch (IOException exception) {

            }
        }
        index.removeAll(index);
        for (Book b : childs) {
            b.cleanUp();
            try {
                Files.deleteIfExists(b.root);
            } catch (IOException exception) {
                fireEvents(0, "File \"" + (b.root.toFile()).getName() +  "\" not existing !");
            }
        }
        childs.removeAll(childs);
        for (File folder : folders) {
            try {
                Files.deleteIfExists(folder.toPath());
            } catch (IOException exception) {
                fireEvents(0, "Folder \"" + folder.getName() +  "\" not existing !");
            }
        }
        folders.removeAll(folders);
    }








    public void setCharsetName(String charsetName)
    {
        this.charsetName = charsetName;
    }
    public String getCharsetName ()
    {
        return this.charsetName;
    }
    public Charset getCharset ()
    {
        return Charset.forName(this.charsetName);
    }

    public ArrayList<Path> getIndex()
    {
        return this.index;
    }
}
