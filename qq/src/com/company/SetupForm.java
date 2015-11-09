package com.company;

import com.sun.java.swing.plaf.motif.MotifTextUI;
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
import java.util.ArrayList;

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
    private JButton resetButton;
    private JButton expandButton;
    private JButton expandAllButton;
    private JButton buttonCollapseAll;
    private JTextArea parsedKeys;
    private JTextField searchField;

    private TreeManager treeManager;
    private PathIconManager iconManager;
    private PathComparator pathComparator;

    private KeyParser keyParser;

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


    public SetupForm ()
    {
        super("Frame");
        setContentPane(contentPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 500);
        this.setSize(d);
        this.setBackground(Color.DARK_GRAY);

        parsedKeys.setEditable(false);
        outputTextArea.setEditable(false);

        keyParser = new KeyParser();
        keyTexArea.setText("'any'");
        iconManager = new PathIconManager(iconsPath);

        treeManager = new TreeManager(iconManager);

        PathTreeCellRenderer cellTreeRenderer = new PathTreeCellRenderer(tree1, false);

        treeToPick.setModel(new DefaultTreeModel(treeManager.createTreeFromString(hddRoots)));
        treeToPick.setCellRenderer(new PathTreeCellRenderer(treeToPick, false));
        treeToPick.setBackground(Color.WHITE);

        tree1.setModel(new DefaultTreeModel(treeManager.createTreeFromString("")));
        tree1.setCellRenderer(cellTreeRenderer);
        tree1.setBackground(Color.WHITE);


        progressBarScanStatus.setMinimum(0);
        progressBarScanStatus.setMaximum(100);


        treeManager.setGuiBarToShow(progressBarScanStatus);

        pathComparator = new PathComparator();

        treeToPick.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == treeToPick) {
                    PathTreeNode nodeSelected = new PathTreeNode((PathTreeNode) treeToPick.getLastSelectedPathComponent());
                    if (nodeSelected != null) {
                        if (SwingUtilities.isLeftMouseButton(e) == true) {

                        } else if (SwingUtilities.isRightMouseButton(e) == true) {
                                //treeManager.createBranchAndInsertFromSelected(treeToPick, pathComparator, false);
                                ((DefaultTreeModel) tree1.getModel()).insertNodeInto(nodeSelected, (PathTreeNode) tree1.getModel().getRoot(), 0);
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
                    treeManager.createBranchAndInsertFromSelected(treeToPick, false);
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
                    treeManager.createBranchAndInsertFromSelected(tree1, true);
                }
            }
        });

        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treeManager.collapseAll(tree1);
                pathComparator.resetAll();
                treeManager.watchAllLeafsInTree(tree1, pathComparator, true);
                tree1.repaint();
                treeManager.expandRows(tree1);
                //outputTextArea.setText(pathComparator.toString());
                printWithHighlight(pathComparator.getTextItems(), outputTextArea);
            }
        });

        keyTexArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyTyped(e);
                if (e.getSource() == keyTexArea) {

                    outputTextArea.setText("");
                    parsedKeys.setText("");
                    for (String s : keyParser.parseToDefault(pathComparator, keyTexArea.getText())) {
                        parsedKeys.append(s + "\n");
                    }

                    /*
                    Highlighter highLighter = keyTexArea.getHighlighter();
                    Highlighter.HighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.orange);
                    try {
                        highLighter.addHighlight(0, (keyTexArea.getText()).length(), highlightPainter);
                    }
                    catch (BadLocationException exception) {

                    }
                    */
                }
            }
        });

        keyTexArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                if (e.getSource() == keyTexArea) {
                    keyTexArea.selectAll();
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

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pathComparator.resetAll();
                outputTextArea.setText("0");
                keyTexArea.setText("'any'");
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

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyTyped(e);
                if (e.getSource() == searchField) {
                    searchProcess(outputTextArea, searchField);
                    searchProcess(parsedKeys, searchField);
                }
            }
        });

        searchField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                if (e.getSource() == searchField) {
                    searchField.selectAll();
                }
            }
        });

        setVisible(true);
    }


    public void doNothing ()
    {

    }



    private void printWithHighlight (ArrayList<textBoundedItem> items, JTextArea textArea)
    {
        textArea.setText("");
        Highlighter highLighter = textArea.getHighlighter();
        highLighter.removeAllHighlights();
        int index = 0;
        for (textBoundedItem item : items) {
            textArea.append(item.toString());
            if (item.getHighlightFlag() == true) {
                try {
                    highLighter.addHighlight(index, index + item.getLength() - 1, new DefaultHighlighter.DefaultHighlightPainter(item.getColor()));
                }
                catch (BadLocationException exception) {

                }
            }
            index += item.getLength();
        }
    }

    private String searchProcess (JTextArea textArea, JTextField textField)
    {
        Highlighter highLighter = textArea.getHighlighter();
        highLighter.removeAllHighlights();
        String input = textArea.getText();
        if (input.isEmpty() == true) {
            return "Empty input!";
        }
        String keyWord = textField.getText();
        if (keyWord.isEmpty() == true) {
            return "No key!";
        }
        int watchIndex = input.indexOf(keyWord);
        int keyWordSize = keyWord.length();

        while (watchIndex >= 0) {
            try {
                highLighter.addHighlight(watchIndex, watchIndex + keyWordSize, new DefaultHighlighter.DefaultHighlightPainter(Color.pink));
            }
            catch (BadLocationException exception) {

            }
            watchIndex = input.indexOf(keyWord, watchIndex + 1);
        }

        return keyWord;
    }
}
