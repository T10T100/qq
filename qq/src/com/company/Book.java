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
    private ArrayList<String> text;
    private ArrayList<Path> index;
    private ArrayList<Book> childs;
    private ArrayList<exelBook> eChilds;
    private Path root;
    private final int linesLimit = 50000;
    private int linesCount;
    private String charsetName;


    public Book(File location, String bookName)
    {
        index = new ArrayList<>();
        text = new ArrayList<>();
        childs = new ArrayList<>();
        eChilds = new ArrayList<>();
        this.name = bookName;
        this.root = location.toPath();
        this.linesCount = 0;
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

    public exelBook newExelBookThere (String name)
    {
        exelBook eBook = new exelBook(this.root.toFile(), name);
        if (this.eChilds.contains(eBook) == false) {
            this.eChilds.add(eBook);
        }
        return eBook;
    }

    public void write (String line)
    {
        text.add(line);
        linesCount++;
        if (linesCount >= linesLimit) {
            File file = null;
            FileWriter writer = null;
            String p = root + File.separator + Long.toString(System.currentTimeMillis()) + "." + this.name;
            file = new File(p);
            try {
                file.createNewFile();
            } catch (IOException exception) {
                return;
            }
            try {
                try {
                    writer = new FileWriter(file, true);
                    while (text.isEmpty() == false) {
                        try {
                            writer.write("\r\n"  + text.remove(0) + " \r\n");
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
            index.add(Paths.get(p));
        }
    }

    public void finish ()
    {
        File file = null;
        FileWriter writer = null;
        String p = root + File.separator + Long.toString(System.currentTimeMillis()) + "." + this.name;
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

    public void finish (String name)
    {
        File file = null;
        FileWriter writer = null;
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

            }
        }
        index.removeAll(index);
        for (Book b : childs) {
            b.cleanUp();
        }
    }
    void cleanAll ()
    {
        for (Path p : index) {
            try {
                Files.deleteIfExists(p);
            } catch (IOException exception) {

            }
        }
        index.removeAll(index);
        for (Book b : childs) {
            b.cleanUp();
            try {
                Files.deleteIfExists(b.root);
            } catch (IOException exception) {

            }
        }
        childs.removeAll(childs);
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
