package com.company;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Created by Operator on 27.10.2015.
 */
public class PathTreeCellRenderer extends DefaultTreeCellRenderer {
    private TreePath oldSelectedPath;
    private boolean selectLogic;
    private boolean expandlogic;

    public PathTreeCellRenderer (final JTree tree, boolean logic)
    {
        this.selectLogic = logic;
            tree.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {

                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
                    if (selRow < 0) {
                        TreePath currentSelected = oldSelectedPath;
                        oldSelectedPath = null;
                        if (currentSelected != null) {
                            treeModel.nodeChanged((TreeNode) currentSelected.getLastPathComponent());
                        }
                    } else {
                        TreePath selectedPath = tree.getPathForLocation(e.getX(), e.getY());
                        if ((oldSelectedPath == null) || !selectedPath.equals(oldSelectedPath)) {
                            oldSelectedPath = selectedPath;
                            treeModel.nodeChanged((TreeNode) oldSelectedPath.getLastPathComponent());
                            if (selectLogic == true) {
                                tree.setSelectionPath(selectedPath);
                            }
                            if (expandlogic == true) {
                                tree.expandPath(selectedPath);
                            }
                        }
                    }
                    tree.repaint();
                }
            });
    }

    @Override
    public Component getTreeCellRendererComponent (JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        JComponent comp = (JComponent) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        comp.setOpaque(true);

        boolean highlight = (oldSelectedPath != null) && (value == oldSelectedPath.getLastPathComponent());
        PathTreeNode node = (PathTreeNode) value;

        if (highlight == true) {
            comp.setBackground(Color.PINK);
        } else {
            comp.setBackground(tree.getBackground());
        }
        //setIcon(node.getIcon());
        Icon ico = getIcon();
        if (node.getIcon() != null) {
            setIcon(node.getIcon());
        } else {
            setIcon(ico);
        }

        return comp;
    }

    public void setLogic (boolean value)
    {
        this.selectLogic = value;
    }

    public  void setExpandlogic (boolean value)
    {
        this.expandlogic = value;
    }

}
