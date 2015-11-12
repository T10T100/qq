package com.company;

import com.sun.jmx.snmp.Timestamp;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.TimeStringConverter;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import javax.swing.tree.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Created by k on 28.10.2015.
 */

public class TreeManager {

    private PathIconManager iconManager;
    private int alignName;
    private int alignSize;
    private int alignDate;
    DateFormat dateFormat;

    private JLabel statusOut;

    public TreeManager(PathIconManager iconManager, JLabel statusOut)
    {
        this.iconManager = iconManager;
        this.statusOut = statusOut;
        dateFormat = new SimpleDateFormat("YYYY:MM:dd : HH:mm:ss");
        this.alignName = 80;
        this.alignSize = 16;
        this.alignDate = 20;
    }

    private PathTreeNode insertBranchByPath (PathTreeNode rootNode, Path path)
    {
        PathTreeNode node = null;
        File file = null;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path child : stream) {
                node = new PathTreeNode(child, this.iconManager.fileIcon);
                file = child.toFile();
                if (file.isDirectory() == true) {
                    node.setIcon(this.iconManager.nowFolderIcon);
                } else {
                    if (file.isHidden() == true) {
                        node.setIcon(this.iconManager.hiddenFileIcon);
                    } else {
                        node.setIcon(this.iconManager.fileIcon);
                    }
                }
                rootNode.add(node);
            }
        }
        catch (IOException e){
            System.out.println(e.getCause().toString());
        }
        return rootNode;
    }

    private void remove (JTree tree, PathTreeNode node)
    {
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        if (model.getRoot() == node) {
            return;
        }
        if (node != null) {
            model.removeNodeFromParent(node);
        }
    }

    public void removeSelected (JTree tree)
    {
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        PathTreeNode selected = (PathTreeNode)tree.getLastSelectedPathComponent();
        remove(tree, selected);
    }

    public PathTreeNode insertBranchByName (String way)
    {
        Path path = Paths.get(way);
        PathTreeNode root = new PathTreeNode(Paths.get("A:/"));
        if (Files.exists(path) == false)
        {
            return root;
        }
        if (Files.isDirectory(path) == false)
        {
            return root;
        }
        root.setUserObject(path);
        return this.insertBranchByPath(root, path);
    }

    public PathTreeNode makeTreeByName (String... paths)
    {
        PathTreeNode root = new PathTreeNode(Paths.get("A:/"));
        Path path;
        for (String s : paths) {
            path = Paths.get(s);
            if (Files.exists(path)) {
                root.add(this.insertBranchByName(s));
            }
        }
        return root;
    }

    public PathTreeNode insertBranchByRoot (JTree tree, PathTreeNode root)
    {
        if (root == null) {
            return new PathTreeNode("null");
        }
        if (root.isLeaf() == false) {
            return new PathTreeNode("not a leaf");
        }
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();

        Object obj = root.getUserObject();
        Path path = Paths.get(obj.toString());

        if (Files.exists(path) == false) {
            return new PathTreeNode("null");
        }
        if (Files.isDirectory(path) == false) {
            return new PathTreeNode("file ?");
        }

        PathTreeNode childNode = null;
        File file = null;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path child : stream) {
                file = child.toFile();
                if (file.exists() == true) {
                    childNode = new PathTreeNode(child, this.iconManager.fileIcon);
                    if (file.isDirectory() == true) {
                        childNode.setIcon(this.iconManager.nowFolderIcon);
                    } else {
                        if (file.isHidden() == true) {
                            childNode.setIcon(this.iconManager.hiddenFileIcon);
                        } else {
                            childNode.setIcon(this.iconManager.fileIcon);
                        }
                    }
                    model.insertNodeInto(childNode, root, 0);
                }
            }
        }
        catch (IOException e){

        }
        return root;
    }

    public PathTreeNode createBranchAndInsertFromSelected (JTree tree)
    {
        return insertBranchByRoot(tree, (PathTreeNode) tree.getLastSelectedPathComponent());
    }

    private void makeHash (JTree tree, PathTreeNode fromNode, PathsHashFile hashFile)
    {
        int items = 0;
        if (fromNode == null) {
            return;
        }
        Object o = null;
        if (fromNode.isLeaf() == true) {
            o = fromNode.getUserObject();
            if (o == null) {
                return;
            }
            File file = new File(o.toString());
            if (file.isDirectory() == true) {
                insertBranchByRoot(tree, fromNode);
            } else {
                hashFile.writeToCurrentParagraph(this.printFileToHash(file, dateFormat));
                return;
            }
        }
        items = fromNode.getChildCount();
        for (int i = 0; i < items; i++) {
            makeHash(tree, (PathTreeNode)fromNode.getChildAt(i), hashFile);
        }
    }

    public void makeHash (JTree tree, PathsHashFile hashFile)
    {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        PathTreeNode root = (PathTreeNode) model.getRoot();
        statusOut.setText("gathering...");
        this.makeHash(tree, root, hashFile);
        statusOut.setText("done!");

        hashFile.writeToLog();hashFile.clear();hashFile.setNewBook("$hash$");
    }

    public void watchHash (PathComparator comparator, boolean makeLog, PathsHashFile logFile, PathsHashFile hashFile)
    {
        Word word = new Word();
        comparator.resetAll();
        statusOut.setText("watching...");
        comparator.setTimeStart(System.nanoTime());
        Stream<String> stream = hashFile.readLineByLine();

        if (makeLog == false) {
            stream.forEach(line -> this.look(word, line, comparator, hashFile));
        } else {
            logFile.writeToCurrentParagraph(comparator.printKeys());
            stream.forEach(line -> this.lookAndWriteLog(word, line, comparator, logFile, hashFile, "^~-~"));
            logFile.writeToLog();logFile.clear();logFile.setNewBook("$log$");
        }
        stream.close();
        statusOut.setText("done!");
        comparator.setTimeEnd(System.nanoTime());
    }

    private void look (Word word, String line, PathComparator comparator, PathsHashFile hashFile)
    {
        word.setValue(line);
        ArrayList<String> attributes;
        attributes = word.getArrayFromValue('<', '>');
        if (attributes.size() >= 2) {
            comparator.compareAndCollect(attributes.get(1), Long.parseLong(attributes.get(2)));
        }
    }

    private void lookAndWriteLog (Word word, String line, PathComparator comparator, PathsHashFile logFile, PathsHashFile hashFile, String trailer)
    {
        word.setValue(line);
        ArrayList<String> attributes;
        String postfix = "";
        String name;
        String size;
        String date;
        int nameSize;
        int sizeSize;
        int dateSize;
        attributes = word.getArrayFromValue('<', '>');
        if (attributes.size() >= 2) {
            name = attributes.get(1);
            size = attributes.get(2);
            date = attributes.get(3);
            postfix = comparator.compareAndLog(name, Long.parseLong(size));
            size = this.convertStringNumber(size);

            nameSize = this.alignName - name.length();
            sizeSize = this.alignSize - size.length();
            dateSize = this.alignDate - date.length();

            name += ';';
            name = this.insertTrail(name, trailer, nameSize);
            size = this.insertTrail(size, trailer, sizeSize);
            date += "; ";
            date = this.insertTrail(date, trailer, dateSize);

            if (postfix.isEmpty() == false) {
                logFile.writeToCurrentParagraph(name + "size- " + size + "date- " + date + " matched to- <" + postfix + ">");
            }
        }
    }


    public long expandRows (JTree tree)
    {
        long rowCount = tree.getRowCount();
        for (long i = 0; i < rowCount; i++) {
            tree.expandRow((int)i);
        }
        return rowCount;
    }

    public long expandAll (JTree tree)
    {
        long oldCount = 0, count = 0;
        long total = 0;
        do {
            oldCount = count;
            count = expandRows(tree);
            total += count;
        } while (oldCount != count);
        return total;
    }

    public void collapseAll (JTree tree)
    {
        long rowCount = tree.getRowCount();
        long oldRowCount = rowCount;
        do {
            oldRowCount = rowCount;
            rowCount = tree.getRowCount();
            for (int i = (int)rowCount - 1; i >= 0; i--) {
                tree.collapseRow(i);
            }
        } while (oldRowCount != rowCount);
    }

    private String printFileToLog (File file, DateFormat format)
    {
        return  file.getName() +
                " *-----* " +
                (file.isHidden() == true ? "Hidden file, " : "File, ") +
                "Size- <" + file.length() + "> Bytes, modified- " +
                format.format(new Date(file.lastModified()));
    }
    private String printFileToHash (File file, DateFormat format)
    {
        return  "<" + file.getName() + ">"+
                "<" + file.length() + "><" +
                format.format(new Date(file.lastModified())) + ">";
    }
    private String printDirToLog (File file, DateFormat format)
    {
        return  file.toPath().toString() +
                " *-----* Folder, " + "modified- " +
                format.format(new Date(file.lastModified()));
    }
    private String printDirToHash (File file, DateFormat format)
    {
        return  "<" + file.getName() + ">"+
                format.format(new Date(file.lastModified())) + ">";
    }

    public void setAligns (int allignName, int allignSize, int allignDate)
    {
        this.alignName = allignName;
        this.alignSize = allignSize;
        this.alignDate = allignDate;
    }

    private String convertStringNumber (String number)
    {
        char prefix = ' ';
        String output = "";
        int length = number.length();
        if (length > 15) {
            prefix = '?';
        } else if (length > 12) {
            prefix = 'T';
        } else if (length > 9) {
            prefix = 'G';
        } else if (length > 6) {
            prefix = 'M';
        } else if (length > 3) {
            prefix = 'K';
        } else if (length > 0) {
            prefix = ' ';
        }
        char[] array = number.toCharArray();
        for (int i = 0, stop = 0; i < length; i++) {
            if (i == 3) {
                output += ".";
            }
            if (i < 3) {
                output += array[i];
            } else {
                output += array[i];
            }
            if (++stop > 5) {
                break;
            }
        }
        return output + " " + prefix + "bytes";
    }

    private String insertTrail (String input, String trailer, int length)
    {
        int trailerLength = trailer.length();
        while (length >= 0) {
            for (int index = 0; length >= 0 && index < trailerLength; length--, index++) {
                input += trailer.charAt(index);
            }
        }
        return input;
    }

}
