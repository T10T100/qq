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
    private PathIconPainter iconPainter;
    private Vector<PathWatcherEventListener> listenersMake;
    private Vector<PathWatcherEventListener> listenersWatch;
    private Vector<PathWatcherEventListener> listenersIntermediateMake;
    private long pathsTotal;
    private long pathsCount;
    JProgressBar statusBar;

    private boolean hashReady;
    private boolean breaker;

    private class HashThread extends Thread {
        private Book hash;
        private JTree tree;

        public HashThread (JTree tree, Book hash)
        {
            this.tree = tree;
            this.hash = hash;
        }

        @Override
        public void run() {
            super.run();
            breaker = false;
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            PathTreeNode root = (PathTreeNode) model.getRoot();
            Path p;
            hash.cleanUp();
            PathTreeNode node;
            int childCount = root.getChildCount();
            for (int i = 0; i < childCount; i++) {
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
            }
            tree.repaint();
            hash.finish();
            hashReady = true;
            fireMakeEvents();

        }
    }

    private class HashWatchThread extends Thread {
        private Book hash;
        private  Book log;
        private PathComparator comparator;

        public HashWatchThread (PathComparator comparator, Book hash, Book log)
        {
            this.hash = hash;
            this.log = log;
            this.comparator = comparator;
        }

        @Override
        public void run() {
            super.run();
            pathsCount = 0;
            statusBar.setIndeterminate(false);
            statusBar.setValue(0);
            statusBar.setMinimum(0);
            statusBar.setMaximum((int)pathsTotal);

            Word word = new Word();
            comparator.resetAll();
            ArrayList<Path> paths = hash.getIndex();
            Stream<String> output = null;
            comparator.resetAll();
            comparator.setTimeStart(System.currentTimeMillis());
            int fireAt = 0;

            for (Path path : paths) {
                fireIntermediateMakeEvents();
                try {
                    output = Files.lines(path, hash.getCharset());
                } catch (IOException exception) {
                    continue;
                }
                try {
                    output.forEach(line -> look(word, line, comparator, fireAt));
                } catch (UncheckedIOException exception) {
                    continue;
                }
                if (output != null) {
                    output.close();
                }
            }
            comparator.setTimeEnd(System.currentTimeMillis());
            comparator.log(log);
            Word w;
            File location;
            iconPainter.cleanUp();
            icons.clearUserIcons();
            for (PathKey key : comparator.getKeys()) {
                w = key.getKey();
                if (w.getValue().isEmpty() == true) {
                    continue;
                }
                if (w.getName().isEmpty() == true) {
                    continue;
                }
                w.removeSpacesFromValue();
                location = hash.newDirectoryThere("extension_icons");
                icons.addUserTypeIcon(iconPainter.drawIcon(location.toPath(), w.getValue()), w.getName());
        }
            fireWatchEvents();
        }
    }

    HashThread hashThread;

    public PathWatcher(PathIconsManager icons, JProgressBar statusBar, PathIconPainter iconPainter)
    {
        this.icons = icons;
        this.iconPainter = iconPainter;
        dateFormat = new SimpleDateFormat("YYYY:MM:dd - HH:mm:ss");
        this.statusBar = statusBar;
        hashReady = false;
        pathsTotal = 0;
        pathsCount = 0;
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


    public void addIntermediateMakeListener (PathWatcherEventListener l)
    {
        if (listenersIntermediateMake == null) {
            listenersIntermediateMake = new Vector<>();
        }
        if (listenersIntermediateMake.contains(l) == false) {
            listenersIntermediateMake.add(l);
        }
    }

    public void removeIntermediateMakeListener (PathWatcherEventListener l)
    {
        listenersIntermediateMake.remove(l);
    }

    public void removeAllIntermediateMakeListeners ()
    {
        listenersIntermediateMake.removeAll(listenersMake);
    }

    private void fireIntermediateMakeEvents ()
    {
        if (listenersIntermediateMake == null || listenersMake.isEmpty() == true) {
            return;
        }
        Vector<PathWatcherEventListener> v;
        synchronized (this) {
            v = (Vector) listenersIntermediateMake.clone();
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
                    node.setIcon(icons.getTypeIcon(child.toFile()));
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
        PathTreeNode root = (PathTreeNode)model.getRoot();
        if (root == null) {
            return;
        }
        try {
            if (node != null) {
                if (node.getParent() != null) {
                    model.removeNodeFromParent(node);
                } else {
                    clearTree(tree);
                }
            }
        } catch (NullPointerException exception) {

        }
        if (isTreeEmpty(tree) == true) {
            root.setIcon(icons.getDeadIcon());
        }
        tree.repaint();
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
                            node.setIcon(icons.getTypeIcon(file));
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

    public void dragFromselected (JTree destTree, JTree sourceTree)
    {
        PathTreeNode nodeSelected = new PathTreeNode((PathTreeNode) sourceTree.getLastSelectedPathComponent());
        if (nodeSelected == null) {
            return;
        }
        PathTreeNode node = new PathTreeNode(nodeSelected);
        node.setIcon(icons.getUnchekedIcon());
        ((PathTreeNode)destTree.getModel().getRoot()).setIcon(icons.getRootIcon());
        ((DefaultTreeModel) destTree.getModel()).insertNodeInto(node, (PathTreeNode) destTree.getModel().getRoot(), 0);
        setHashReady(false);
    }

    private void unwindPath (Path path, Book hash)
    {
        if (Files.isDirectory(path) == true) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path child : stream) {
                    unwindPath(child, hash);
                    if (breaker == true) {
                        break;
                    }
                }
                fireIntermediateMakeEvents();
            } catch (IOException exception) {

            }
        } else {
            pathsTotal++;
            hash.write(this.print(new File(path.toString())));
        }
    }

    public void makeHash (JTree tree, PathComparator comparator, Book log, Book hashFile)
    {

        comparator.resetAll();
        if (hashReady == false) {
            statusBar.setIndeterminate(true);
            pathsTotal = 0;
            pathsCount = 0;
            (new HashThread(tree, hashFile)).start();
        } else {
            statusBar.setIndeterminate(false);
            statusBar.setMaximum((int)pathsTotal);
            statusBar.setMinimum(0);
            this.watchHash(comparator, log, hashFile);
        }
    }

    public void watchHash (PathComparator comparator, Book log, Book hashFile)
    {

        (new HashWatchThread(comparator, hashFile, log)).start();
    }

    private void look (Word word, String line, PathComparator comparator, int fireAt)
    {
        word.setValue(line);
        ArrayList<String> attributes;
        attributes = word.getArrayFromValue('<', '>');
        if (attributes.size() >= 2) {
            comparator.compareAndCollect(attributes.get(1), Long.parseLong(attributes.get(2)));
            pathsCount++;
        }
        if (fireAt++ % 1000 == 0) {
            fireIntermediateMakeEvents();
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

    public boolean isHashReady ()
    {
        return hashReady;
    }

    public void setBreaker(boolean breaker)
    {
        this.breaker = breaker;
        hashReady = false;
    }

    public long getPathsCount ()
    {
        return this.pathsCount;
    }

    public boolean isTreeEmpty (JTree tree)
    {
        PathTreeNode node = (PathTreeNode)(tree.getModel().getRoot());
        if (node.getChildCount() == 0) {
            return true;
        }
        return false;
    }

    public void clearTree (JTree tree)
    {
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        PathTreeNode root = (PathTreeNode)model.getRoot();
        int count = root.getChildCount();
        if (count == 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            model.removeNodeFromParent( (MutableTreeNode)root.getChildAt(i) );
        }
    }

    /*
    public void updateTree (JTree tree)
    {
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        PathTreeNode newRoot = new PathTreeNode("A:/", icons.getRootIcon());
        DefaultTreeModel newModel = new DefaultTreeModel(newRoot);
        ArrayList<PathTreeNode> leafs = new ArrayList<>();
        ArrayList<PathTreeNode> nodes = new ArrayList<>();

        PathTreeNode root = (PathTreeNode)model.getRoot();
        if (root == null) {
            return;
        }
        if (root.isLeaf() == true) {
            return;
        }
        nodes.add(root);
        PathTreeNode temp;
        while (nodes.isEmpty() == false) {
            nodes.remove(0);
            int childCount = root.getChildCount();
            if (childCount == 0) {
                continue;
            }
            for (int i = 0; i < childCount; i++) {
                try {
                    temp = (PathTreeNode) root.getChildAt(i);
                } catch (ArrayIndexOutOfBoundsException exception) {
                    break;
                }
                if (root.isLeaf() == true) {
                    leafs.add(temp);
                } else {
                    nodes.add(temp);
                }
                if (temp.getUserObject().getClass() == Path.class) {
                    Path path = (Path) temp.getUserObject();
                    temp.setIcon(icons.getTypeIcon(path.toFile().getName()));
                }
                newRoot.add(temp);
            }
        }
        tree.setModel(newModel);
    }
    */

}
