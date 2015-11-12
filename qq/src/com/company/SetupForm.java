package com.company;

import sun.font.FontFamily;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
    private JCheckBox watchOnCapitalsCheckBox;
    private JCheckBox logOutEnable;

    private JFileChooser saveOutputAsDialog;

    private TreeManager treeManager;
    private PathIconManager iconManager;
    private PathComparator pathComparator;

    private KeyParser keyParser;



    PathsHashFile logObject;
    PathsHashFile hashObject;
    private boolean hashReady;

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

    private ArrayList<Color> highlightColors;
    private Iterator<Color> highlightColorsIterator;


    public SetupForm ()
    {
        super("Path Watcher");
        setContentPane(contentPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 500);
        this.setSize(d);
        this.setBackground(Color.DARK_GRAY);



        highlightColors = new ArrayList<>();
        highlightColors.add(new Color(127, 61, 23, 150));
        highlightColors.add(new Color(227, 73, 59, 150));
        highlightColors.add(new Color(238, 186, 76, 150));
        highlightColors.add(new Color(33, 182, 168, 150));
        highlightColors.add(new Color(127, 23, 105, 150));
        highlightColors.add(new Color(124, 130, 30, 150));
        highlightColors.add(new Color(195, 17, 76, 150));
        highlightColors.add(new Color(38, 17, 117, 100));
        highlightColorsIterator = highlightColors.iterator();

        int directorySelect = 0;
        saveOutputAsDialog = new JFileChooser();
        saveOutputAsDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directorySelect = saveOutputAsDialog.showOpenDialog(SetupForm.this);

        if (directorySelect == JFileChooser.CANCEL_OPTION || directorySelect == JFileChooser.ERROR_OPTION) {
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }

        logObject = new PathsHashFile("log.txt", saveOutputAsDialog.getSelectedFile(), "$LOG$");
        hashObject = new PathsHashFile("hash.txt", saveOutputAsDialog.getSelectedFile(), "$HASH$");
        hashReady = false;

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
                            if (e.getClickCount() >= 2) {

                            }
                        } else if (SwingUtilities.isRightMouseButton(e) == true) {
                            ((DefaultTreeModel) tree1.getModel()).insertNodeInto(nodeSelected, (PathTreeNode) tree1.getModel().getRoot(), 0);
                            hashReady = false;
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
                    treeManager.createBranchAndInsertFromSelected(treeToPick);
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
                    treeManager.createBranchAndInsertFromSelected(tree1);
                }
            }
        });

        stepButton.addActionListener(new ActionListener() {         /**Watch there**/
            @Override
            public void actionPerformed(ActionEvent e) {
                pathComparator.resetAll();
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                if (logOutEnable.isSelected() == true) {
                    treeManager.watchAllLeafsInTree(tree1, pathComparator, hashReady, logObject, hashObject);
                } else {
                    treeManager.watchAllLeafsInTree(tree1, pathComparator, hashReady, hashObject);
                }
                hashReady = true;

                setCursor(Cursor.getDefaultCursor());
                printWithHighlight(pathComparator.getTextItems(), outputTextArea);
                hashObject.setNeedUpdate(false);
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
                }
            }
        });

        keyTexArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                if (e.getSource() == keyTexArea) {
                    keyTexArea.selectAll();
                    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                if (e.getSource() == keyTexArea) {
                    setCursor(Cursor.getDefaultCursor());
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
                    //searchProcess(parsedKeys, searchField);
                }
            }
        });

        searchField.addMouseListener(new MouseAdapter() {
            @Override
             public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                if (e.getSource() == searchField) {
                    searchField.selectAll();
                    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                super.mouseEntered(e);
                if (e.getSource() == searchField) {
                    setCursor(Cursor.getDefaultCursor());
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
            try {
                highLighter.addHighlight(index, index + item.getLength() - 1, item.getHighLight());
            }
            catch (BadLocationException exception) {

            }
            index += item.getLength();
        }
    }

    private String searchProcess (JTextArea textArea, JTextField textField)
    {
        String input = textArea.getText();
        if (input.isEmpty() == true || textField.getText().isEmpty() == true) {
            return "null";
        }

        Word inputWord = new Word(textField.getText());
        inputWord.removeSpacesFromWord();

        textArea.getHighlighter().removeAllHighlights();
        textField.getHighlighter().removeAllHighlights();

        ArrayList<TextMark> keyMarks = new ArrayList<>();
        ArrayList<TextMark> matchedMarks = new ArrayList<>();

        int keyLastPosition = 0;
        Color keyHighColor;
        this.getFirstTextHighlightColor();

        for (String text : inputWord.getArrayFromValue(',')) {
            keyHighColor = this.getNextTextHighlightColor();
            if (text.isEmpty() == false) {

                keyMarks.add(new TextMark(keyLastPosition, keyLastPosition + text.length() + 1, keyHighColor));
                Word word;
                if (text.contains("<")) {
                    word = new Word(text, '<');
                    if (word.getValue().length() == 0 || word.getName().length() == 0) {
                        continue;
                    }
                    word.removeAllButDigitsFromWord();
                    long bottomValue = Long.parseLong(word.getName());
                    long topValue = Long.parseLong(word.getValue());
                    Word inputNumbers = new Word(input);
                    for (Word.BoundedNumber num : inputNumbers.getIntegersFromValue()) {
                        if (bottomValue < num.getNumber() && num.getNumber() < topValue) {
                            matchedMarks.add(new TextMark(num.getStartIndex(), num.getEndIndex(), keyHighColor));
                        }
                    }
                } else {
                    for (int index = input.indexOf(text); index >= 0; index = input.indexOf(text, index + 1)) {
                        matchedMarks.add(new TextMark(index, index + text.length(), keyHighColor));
                    }
                }

                keyLastPosition += text.length() + 1;
            }
        }
        this.addHighlighters(textField.getHighlighter(), keyMarks);
        this.addHighlighters(textArea.getHighlighter(), matchedMarks);
        return "!";
    }

    public void addHighlighters (Highlighter highLighter, ArrayList<TextMark> marks)
    {
        for (TextMark mark : marks) {
            try {
                highLighter.addHighlight(mark.getStart(), mark.getEnd(), mark.getHighLight());
            } catch (BadLocationException exception) {

            }
        }
    }

    public Color getNextTextHighlightColor ()
    {
        if (highlightColorsIterator.hasNext() == false) {
            highlightColorsIterator = highlightColors.iterator();
        }
        return highlightColorsIterator.next();
    }
    public Color getFirstTextHighlightColor ()
    {
        highlightColorsIterator = highlightColors.iterator();
        return highlightColorsIterator.next();
    }
}
