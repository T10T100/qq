package com.company;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by Operator on 27.10.2015.
 */
public class PathTreeNode extends DefaultMutableTreeNode {
    private ImageIcon icon;
    private boolean hasChilds;
    private boolean hasMatch;


    public PathTreeNode(Object o) {
        super(o);
    }

    public PathTreeNode(ImageIcon icon) {
        this.icon = icon;
    }

    public PathTreeNode(Object o, ImageIcon icon) {
        super(o);
        this.icon = icon;
    }

    public PathTreeNode(Object o, boolean allowsChildren, ImageIcon icon) {
        super(o, allowsChildren);
        this.icon = icon;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public void setMatch(boolean value)
    {
        this.hasMatch = value;
    }

    public boolean getMatch ()
    {
        return this.hasMatch;
    }
}
