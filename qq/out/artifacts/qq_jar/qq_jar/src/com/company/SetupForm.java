package com.company;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.ComboBoxTableCell;

import javax.print.DocFlavor;
import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.xml.stream.events.Characters;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Operator on 26.10.2015.
 */
public class SetupForm extends JFrame {
    private JPanel contentPanel;
    private JTextArea outputTextArea;
    private JTextArea keyTexArea;
    private JCheckBox watchMode;
    private JButton stepButton;
    private JLabel labelforPath;
    private JLabel labelToCompare;
    private JLabel labelToMode;
    private JTree treeToPick;
    private JLabel treeInfoLabel;
    private JTree tree1;
    private JProgressBar progressBarScanStatus;
    private JCheckBox expandMode;
    private JCheckBox watchSize;
    private JTextField topSize;
    private JTextField bottomSize;
    private JLabel topSizeLabel;
    private JLabel bottomSizeLabel;
    private JButton resetButton;
    private JComboBox bottomSizeRangeValueSource;
    private JComboBox topSizeRangeValueSource;
    private JButton expandButton;
    private JButton expandAllButton;
    private JButton buttonCollapseAll;
    private JCheckBox hideMissMatched;

    private TreeManager treeManager;
    private PathIconManager iconManager;
    private PathComparator pathComparator;
    private boolean hideMissFlag;

    private String[] hddRoots = {
            "A:/",
            "B:/",
            "C:/",
            "D:/",
            "E:/",
            "F:/",
            "G:/",
            "H:/",
            "I:/"
    };

    private String[] iconsPath = {
            "nfolder.jpg",
            "cfolder.jpg",
            "lfolder.jpg",
            "locked.jpg",
            "completed.jpg",
            "ready.jpg",
            "file.jpg",
            "hfile.jpg"
    };

    public class ComboBoxItem {
        public ComboBoxItem (String s, int r)
        {
            super();
            this.toString = s;
            this.toRange = r;
        }
        @Override
        public String toString()
        {
            return this.toString;
        }
        public String toString;
        int toRange;
    }

    public SetupForm ()
    {
        super("Frame");
        setContentPane(contentPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 500);
        this.setSize(d);
        this.setBackground(Color.DARK_GRAY);



        hideMissFlag = false;

        keyTexArea.setText("'any'");
        iconManager = new PathIconManager(iconsPath);
        this.setVisible(true);
        treeManager = new TreeManager(iconManager);

        PathTreeCellRenderer cellTreeRenderer = new PathTreeCellRenderer(tree1, false);

        treeToPick.setModel(new DefaultTreeModel(treeManager.createTreeFromString(hddRoots)));
        treeToPick.setCellRenderer(new PathTreeCellRenderer(treeToPick, false));
        treeToPick.setBackground(Color.WHITE);

        tree1.setModel(new DefaultTreeModel(treeManager.createTreeFromString(hddRoots)));
        tree1.setCellRenderer(cellTreeRenderer);
        tree1.setBackground(Color.WHITE);


        progressBarScanStatus.setMinimum(0);
        progressBarScanStatus.setMaximum(100);


        treeManager.setGuiBarToShow(progressBarScanStatus);

        pathComparator = new PathComparator();

        hideSizeSettings();

        DefaultComboBoxModel<ComboBoxItem> comboModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<ComboBoxItem> comboModel2 = new DefaultComboBoxModel<>();
        int i = 0;
        for (String s : sizePostfix) {
            comboModel.addElement(new ComboBoxItem(s, i));
            comboModel2.addElement(new ComboBoxItem(s, i));
            i++;
        }
        topSizeRangeValueSource.setModel(comboModel);
        bottomSizeRangeValueSource.setModel(comboModel2);

        treeToPick.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == treeToPick) {
                    PathTreeNode nodeSelected = new PathTreeNode((PathTreeNode) treeToPick.getLastSelectedPathComponent());
                    if (nodeSelected != null) {
                        if (SwingUtilities.isLeftMouseButton(e) == true) {

                        } else if (SwingUtilities.isRightMouseButton(e) == true) {
                                //treeManager.createBranchAndInsertFromSelected(treeToPick, pathComparator, false);
                                ((DefaultTreeModel) (tree1.getModel())).insertNodeInto(nodeSelected, (PathTreeNode)tree1.getModel().getRoot(), 0);
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

                if (e.getSource() == treeToPick) {
                    try
                    {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }catch(Exception ex){}
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getSource() == treeToPick) {
                    try
                    {
                        setCursor(Cursor.getDefaultCursor());
                    }catch(Exception ex){}
                }
            }
        });

        treeToPick.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (e.getSource() == treeToPick) {
                    treeManager.createBranchAndInsertFromSelected(treeToPick, pathComparator, false);
                }
            }
        });

        tree1.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == tree1) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        treeManager.removeSelected(tree1);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (e.getSource() == tree1) {
                    try
                    {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }catch(Exception ex){}
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getSource() == tree1) {
                    try
                    {
                        setCursor(Cursor.getDefaultCursor());
                    }catch(Exception ex){}
                }
            }
        });

        tree1.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (e.getSource() == tree1) {
                    pathComparator.setArg(keyTexArea.getText());
                    treeManager.createBranchAndInsertFromSelected(tree1, pathComparator, true);
                    outputTextArea.setText(pathComparator.getLastCompResultAsString());
                }
            }
        });

        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = treeManager.watchAllLeafsInTree(tree1, pathComparator, true, hideMissFlag);
                tree1.repaint();
                outputTextArea.setText(pathComparator.getLastCompResultAsString());
            }
        });

        keyTexArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getSource() == keyTexArea) {
                    pathComparator.resetAll();
                    outputTextArea.setText("New compare key entered! \nPlease press \"Watch\" button \nto start watch process for added items again\n");
                    Highlighter highLighter = keyTexArea.getHighlighter();
                    Highlighter.HighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.orange);
                    try {
                        highLighter.addHighlight(0, (keyTexArea.getText()).length(), highlightPainter);
                    }
                    catch (BadLocationException exception) {

                    }
                }
            }
        });

        topSize.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getSource() == topSize) {
                    super.keyTyped(e);
                    pathComparator.setTop(applySize(topSize, topSizeRangeValueSource));
                    pathComparator.setArg(keyTexArea.getText());
                }
            }
        });

        bottomSize.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getSource() == bottomSize) {
                    super.keyTyped(e);
                    pathComparator.setBottom(applySize(bottomSize, bottomSizeRangeValueSource));
                    pathComparator.setArg(keyTexArea.getText());
                }
            }
        });


        watchMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == watchMode) {
                    cellTreeRenderer.setLogic(watchMode.isSelected());
                }
            }
        });

        expandMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == expandMode) {
                    cellTreeRenderer.setExpandlogic(expandMode.isSelected());
                }
            }
        });

        watchSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == watchSize) {
                    if (watchSize.isSelected() == true) {
                        showSizeSettings();
                    } else {
                        hideSizeSettings();
                    }
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pathComparator.resetAll();
                pathComparator.setTop(0);
                pathComparator.setBottom(0);
                outputTextArea.setText("0");
                keyTexArea.setText("'any'");
                topSize.setText("0");
                bottomSize.setText("0");
                tree1.setModel(new DefaultTreeModel(new PathTreeNode("Added")));
                treeToPick.setModel(new DefaultTreeModel(treeManager.createTreeFromString(hddRoots)));
            }
        });

        expandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == expandButton) {
                    treeManager.expandRows(tree1);
                }
            }
        });

        expandAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == expandAllButton) {
                    treeManager.expandAll(tree1);
                    tree1.setFocusable(true);
                    tree1.setFocusCycleRoot(true);
                }
            }
        });

        buttonCollapseAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == buttonCollapseAll) {
                    treeManager.collapseAll(tree1);
                }
            }
        });

        hideMissMatched.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == hideMissMatched) {
                    hideMissFlag = hideMissMatched.isSelected();
                }
            }
        });

        setVisible(true);
    }

    public long applySize (JTextField textField, JComboBox<ComboBoxItem> comboBox)
    {
        int range = ((ComboBoxItem)comboBox.getSelectedItem()).toRange;
        long value = Long.parseLong("0" + textField.getText());
        while (range-- > 0) {
            value *= 1000;
        }
        return value;
    }



    private static String[] sizePostfix = {
            "Bytes",
            "KBytes",
            "MBytes",
            "GBytes",
            "TBytes"
    };

    public void hideSizeSettings ()
    {
        topSize.setVisible(false);
        bottomSize.setVisible(false);
        topSizeLabel.setVisible(false);
        bottomSizeLabel.setVisible(false);
        topSizeRangeValueSource.setVisible(false);
        bottomSizeRangeValueSource.setVisible(false);
        pathComparator.setTop(0);
        pathComparator.setBottom(0);
        topSize.setText("0");
        bottomSize.setText("0");
    }

    public void showSizeSettings ()
    {
        topSize.setVisible(true);
        bottomSize.setVisible(true);
        topSizeLabel.setVisible(true);
        bottomSizeLabel.setVisible(true);
        topSizeRangeValueSource.setVisible(true);
        bottomSizeRangeValueSource.setVisible(true);
    }

    public void doNothing ()
    {

    }

}
