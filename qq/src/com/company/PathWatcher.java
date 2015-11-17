package com.company;

import javax.swing.*;
import javax.swing.tree.*;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by k on 28.10.2015.
 */

public class PathWatcher {

    private int alignName;
    private int alignSize;
    private int alignDate;
    DateFormat dateFormat;

    private transient Vector eventListeners;

    public PathWatcher()
    {
        dateFormat = new SimpleDateFormat("YYYY:MM:dd : HH:mm:ss");
        this.alignName = 80;
        this.alignSize = 16;
        this.alignDate = 20;
    }

    public void addEventListener (PathWatcherListener listener)
    {
        if (eventListeners == null) {
            eventListeners = new Vector();
        }
        if (eventListeners.contains(listener) == false) {
            eventListeners.add(listener);
        }
    }

    public void removeEventListener (PathWatcherListener listener)
    {
        eventListeners.remove(listener);
    }

    public void removeAllListeners ()
    {
        eventListeners.removeAll(eventListeners);
    }

    private void fireEvents (PathWatcherStatus status)
    {
        if (eventListeners == null || eventListeners.isEmpty() == true) {
            return;
        }
        PathWatcherEvent event = new PathWatcherEvent(this, status);
        Vector listeners;
        synchronized (this) {
            listeners = (Vector) eventListeners.clone();
        }
        Enumeration e = listeners.elements();
        while (e.hasMoreElements() == true) {
            PathWatcherListener l = (PathWatcherListener) e.nextElement();
            l.actionPerformed(event);
        }
    }

    private PathTreeNode insertBranchByPath (PathTreeNode rootNode, Path path)
    {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path child : stream) {
                rootNode.add(new PathTreeNode(child));
            }
        }
        catch (IOException e){
            //fireEvents(new PathWatcherStatus("Cannot create a branch"));
        }
        return rootNode;
    }

    private void remove (JTree tree, PathTreeNode node)
    {
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        if (model.getRoot() == node) {
            return;
        }
        try {
            if (node != null) {
                model.removeNodeFromParent(node);
            }
        } catch (NullPointerException exception) {

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
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        Object obj = null;
        if (root.isLeaf() == false) {
            return root;
        }
        try {
           obj  = root.getUserObject();
        } catch (NullPointerException exception) {
            //fireEvents(new PathWatcherStatus("Cannot delete this!"));
            return root;
        }
        Path path = Paths.get(obj.toString());

        File file = null;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path child : stream) {
                try {
                    file = child.toFile();
                    if (file.exists() == true) {
                        model.insertNodeInto(new PathTreeNode(child), root, 0);
                    }
                } catch (NullPointerException exception) {

                }
            }
        }
        catch (IOException e){
            //fireEvents(new PathWatcherStatus("Cannot open folder"));
        }
        return root;
    }

    public PathTreeNode createBranchAndInsertFromSelected (JTree tree)
    {
        return insertBranchByRoot(tree, (PathTreeNode) tree.getLastSelectedPathComponent());
    }


    private void unwindPath (Path path, Book hash)
    {
        if (Files.isDirectory(path) == true) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path child : stream) {
                    unwindPath(child, hash);
                }
            } catch (IOException exception) {

            }
        } else {
            hash.write(this.printFileToHash(new File(path.toString()), dateFormat));
        }
    }

    public void makeHash (JTree tree, Book hashFile)
    {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        PathTreeNode root = (PathTreeNode) model.getRoot();
        Path p;
        hashFile.cleanUp();
        int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            p = Paths.get(root.getChildAt(i).toString());
            if (Files.exists(p) == true) {
                unwindPath(p, hashFile);
            }
        }
        hashFile.finish();
        //fireEvents(new PathWatcherStatus("hash!"));
    }

    public void watchHash (PathComparator comparator, boolean makeLog, Book logFile, Book hashFile)
    {
        Word word = new Word();
        comparator.resetAll();
        ArrayList<Path> paths = hashFile.getIndex();
        Stream<String> output = null;
        comparator.setTimeStart(System.nanoTime());
        for (Path path : paths) {
            try {
                output = Files.lines(path, hashFile.getCharset());
            } catch (IOException exception) {
                continue;
            }
            try {
                if (makeLog == false) {
                    output.forEach(line -> this.look(word, line, comparator));
                } else {
                    output.forEach(line -> this.lookAndWriteLog(word, line, comparator, logFile, hashFile, "^~-~"));
                    logFile.finish();
                }
            } catch (UncheckedIOException exception) {
                continue;
            }
            if (output != null) {
                output.close();
            }
        }
        comparator.setTimeEnd(System.nanoTime());

    }

    private void look (Word word, String line, PathComparator comparator)
    {
        word.setValue(line);
        ArrayList<String> attributes;
        attributes = word.getArrayFromValue('<', '>');
        if (attributes.size() >= 2) {
            comparator.compareAndCollect(attributes.get(1), Long.parseLong(attributes.get(2)));
        }
    }

    private void lookAndWriteLog (Word word, String line, PathComparator comparator, Book logFile, Book hashFile, String trailer)
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
        if (attributes.size() >= 3) {
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
                logFile.write(name + "size- " + size + "date- " + date + " matched to- <" + postfix + ">");
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

    public void setAligns (int alignName, int alignSize, int alignDate)
    {
        this.alignName = alignName;
        this.alignSize = alignSize;
        this.alignDate = alignDate;
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
