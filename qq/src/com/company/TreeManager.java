package com.company;

import com.sun.jmx.snmp.Timestamp;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.TimeStringConverter;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import javax.swing.tree.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Created by k on 28.10.2015.
 */

public class TreeManager {

    private PathIconManager iconManager;
    private JProgressBar guiBar;
    boolean endOfWatch;
    private runnableProcessWatch threadToWatch;

    private PathTreeNode createBranchFromPath (PathTreeNode rootNode, Path path)
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

    public TreeManager(PathIconManager iconManager)
    {
        this.iconManager = iconManager;
        endOfWatch = true;
    }

    public void remove (JTree tree, PathTreeNode node)
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

    public PathTreeNode createBranchFromString (String way)
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
        return this.createBranchFromPath(root, path);
    }

    public PathTreeNode createTreeFromString (String... paths)
    {
        PathTreeNode root = new PathTreeNode(Paths.get("A:/"));
        Path path;
        for (String s : paths) {
            path = Paths.get(s);
            if (Files.exists(path)) {
                root.add(this.createBranchFromString(s));
            }
        }
        return root;
    }

    public PathTreeNode createBranchAndInsertFromRoot (JTree tree, PathTreeNode root, boolean compFlag)
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
        this.endOfWatch = false;
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
            System.out.println(e.getCause().toString());
        }
        this.endOfWatch = true;
        return root;


    }

    public PathTreeNode createBranchAndInsertFromSelected (JTree tree, boolean compFlag)
    {
        return createBranchAndInsertFromRoot(tree, (PathTreeNode) tree.getLastSelectedPathComponent(), compFlag);
    }

    private void recursiveScan (JTree tree, PathTreeNode node, PathComparator comparator, boolean watchAll)
    {
        int items = 0;
        if (node == null) {
            return;
        }
        Object o = null;
        if (node.isLeaf() == true) {
            o = node.getUserObject();
            if (o == null) {
                return;
            }
            File file = new File(o.toString());
            if (file.isDirectory()) {
                createBranchAndInsertFromRoot(tree, node, true);
            } else {
                if (comparator.compareAndCollect(file, false) == false) {
                    node.setMatch(false);
                    //this.remove(tree, node);
                    return;
                } else {
                    node.setMatch(true);
                }
                return;
            }
        }
        items = node.getChildCount();
        for (int i = 0; i < items; i++) {
            recursiveScan(tree, (PathTreeNode)node.getChildAt(i), comparator, watchAll);
        }
    }

    public String watchAllLeafsInTree (JTree tree, PathComparator comparator, boolean watchAll)
    {
        if (threadToWatch == null) {
            threadToWatch = new runnableProcessWatch();
            threadToWatch.start();
        }
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        PathTreeNode root = (PathTreeNode)model.getRoot();
        comparator.resetAll();
        comparator.setTimeStart(System.nanoTime());
        this.recursiveScan(tree, root, comparator, watchAll);
        comparator.setTimeEnd(Math.abs(System.nanoTime()));
        return "Well done!";
    }

    public long expandRows (JTree tree)
    {
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        PathTreeNode Root = (PathTreeNode)model.getRoot();
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
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        PathTreeNode Root = (PathTreeNode)model.getRoot();
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

    public void setGuiBarToShow (JProgressBar bar)
    {
        this.guiBar = bar;
    }

    public class runnableProcessWatch extends Thread {
        @Override
        public void run () {
            System.out.println("here");
            guiBar.setIndeterminate(true);
            while (endOfWatch == false) {
                sleepSafe(1000, 0);
            }
            guiBar.setIndeterminate(false);
            guiBar.setValue(0);

            sleepSafe(1, 0);
        }

        public void sleepSafe(long m, int n)
        {
            try {
                sleep(m, n);
            } catch (InterruptedException e) {

            }
        }
    }
}
