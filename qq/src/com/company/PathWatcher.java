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

    private DateFormat dateFormat;
    private PathIconsManager icons;
    private Vector<PathWatcherEventListener> listenersMake;
    private Vector<PathWatcherEventListener> listenersWatch;

    private boolean hashReady;
    private Book hash;
    private JTree tree;

    private class HashThread extends Thread {

        @Override
        public void run() {
            super.run();
            System.out.println("++");
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            PathTreeNode root = (PathTreeNode) model.getRoot();
            Path p;
            hash.cleanUp();
            PathTreeNode node;
            int childCount = root.getChildCount();
            System.out.println("++");
            for (int i = 0; i < childCount; i++) {
                System.out.println("++");
                synchronized (tree) {
                    node = ((PathTreeNode) root.getChildAt(i));
                }
                node.setIcon(icons.getChekedIcon());
                p = Paths.get(node.toString());
                synchronized (this) {
                    if (Files.exists(p) == true) {
                        synchronized (hash) {
                            unwindPath(p, hash);
                        }
                    }
                }
                System.out.println("++");
            }
            tree.repaint();
            hash.finish();
            hashReady = true;
            fireMakeEvents();
        }
    }

    HashThread hashThread;

    public PathWatcher(PathIconsManager icons)
    {
        this.icons = icons;
        dateFormat = new SimpleDateFormat("YYYY:MM:dd : HH:mm:ss");
        hashThread  = new HashThread();
        hashReady = false;
    }

    public void addMakeListener (PathWatcherEventListener l)
    {
        if (listenersMake == null) {
            listenersMake = new Vector<>();
        }
        if (listenersMake.contains(l) == false) {
            listenersMake.add(l);
        }
    }

    public void removeMakeListener (PathWatcherEventListener l)
    {
        listenersMake.remove(l);
    }

    public void removeAllMakeListeners ()
    {
        listenersMake.removeAll(listenersMake);
    }

    private void fireMakeEvents ()
    {
        if (listenersMake == null || listenersMake.isEmpty() == true) {
            return;
        }
        Vector<PathWatcherEventListener> v;
        synchronized (this) {
            v = (Vector) listenersMake.clone();
        }
        PathWatcherEvent e = new PathWatcherEvent(this);
        for (PathWatcherEventListener l : v) {
            l.eventPerformed(e);
        }
    }

    public void addWatchListener (PathWatcherEventListener l)
    {
        if (listenersWatch == null) {
            listenersWatch = new Vector<>();
        }
        if (listenersMake.contains(l) == false) {
            listenersWatch.add(l);
        }
    }

    public void removeWatchListener (PathWatcherEventListener l)
    {
        listenersWatch.remove(l);
    }

    public void removeAllWatchListeners ()
    {
        listenersWatch.removeAll(listenersMake);
    }

    private void fireWatchEvents ()
    {
        if (listenersWatch == null || listenersMake.isEmpty() == true) {
            return;
        }
        Vector<PathWatcherEventListener> v;
        synchronized (this) {
            v = (Vector) listenersWatch.clone();
        }
        PathWatcherEvent e = new PathWatcherEvent(this);
        for (PathWatcherEventListener l : v) {
            l.eventPerformed(e);
        }
    }


    private PathTreeNode insertBranchByPath (PathTreeNode rootNode, Path path)
    {
        PathTreeNode node;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path child : stream) {
                node = new PathTreeNode(child);
                if (Files.isDirectory(child) == true) {
                    node.setIcon(icons.getFolderIcon());
                } else {
                    node.setIcon(icons.getFileIcon());
                }
                rootNode.add(node);
            }
        }
        catch (IOException e){

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
        PathTreeNode selected = (PathTreeNode)tree.getLastSelectedPathComponent();
        remove(tree, selected);
    }

    public PathTreeNode insertBranchByName (String way)
    {
        Path path = Paths.get(way);
        PathTreeNode root = new PathTreeNode(Paths.get("A:/"));
        root.setIcon(icons.getRootIcon());
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
        root.setIcon(icons.getRootIcon());
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
        try {
            if (root.isLeaf() == false) {
                return root;
            }
        } catch (NullPointerException e) {
            return null;
        }
        try {
           obj  = root.getUserObject();
        } catch (NullPointerException exception) {
            return root;
        }
        Path path = Paths.get(obj.toString());

        File file = null;
        PathTreeNode node;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path child : stream) {
                try {
                    file = child.toFile();
                    if (file.exists() == true) {
                        node = new PathTreeNode(child);
                        if (file.isDirectory() == true) {
                            node.setIcon(icons.getFolderIcon());
                        } else {
                            node.setIcon(icons.getFileIcon());
                        }
                        model.insertNodeInto(node, root, 0);
                    }
                } catch (NullPointerException exception) {

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
            hash.write(this.print(new File(path.toString())));
        }
    }

    public void makeHash (JTree tree, PathComparator comparator, Book log, Book hashFile)
    {
        if (hashReady == false) {
            this.hash = hashFile;
            this.tree = tree;
            (new HashThread()).start();
        } else {
            this.watchHash(comparator, log, hashFile);
        }
    }

    private void watchHash (PathComparator comparator, Book log, Book hashFile)
    {
        if (this.hashReady == false) {
            return;
        }
        Word word = new Word();
        comparator.resetAll();
        ArrayList<Path> paths = hashFile.getIndex();
        Stream<String> output = null;
        comparator.resetAll();
        comparator.setTimeStart(System.currentTimeMillis());
        for (Path path : paths) {
            try {
                output = Files.lines(path, hashFile.getCharset());
            } catch (IOException exception) {
                continue;
            }
            try {
                output.forEach(line -> this.look(word, line, comparator));
            } catch (UncheckedIOException exception) {
                continue;
            }
            if (output != null) {
                output.close();
            }
        }
        comparator.setTimeEnd(System.currentTimeMillis());
        comparator.log(log);
        fireWatchEvents();

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




    private String print (File file, DateFormat format)
    {
        return  "<" + file.getName() + ">"+
                "<" + file.length() + "><" +
                format.format(new Date(file.lastModified())) + ">";
    }
    private String print (File file)
    {
        return  "<" + file.getName() + ">"+
                "<" + file.length() + ">";
    }


    public void setHashReady(boolean hashReady)
    {
        this.hashReady = hashReady;
    }
}
