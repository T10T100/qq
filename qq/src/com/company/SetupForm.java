package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Operator on 26.10.2015.
 */
public class SetupForm extends JFrame {
    private JPanel contentPanel;
    private JTextArea outputTextArea;
    private JTextArea keyTexArea;
    private JButton stepButton;
    private JLabel labelToCompare;
    private JTree treeToPick;
    private JLabel treeInfoLabel;
    private JTree tree1;
    private JTextField searchField;
    private JComboBox charsetChooser;
    private JProgressBar progressBarOfWatch;
    private JButton buttonToBreak;
    private JButton buttonToLoadTemplate;

    private JFileChooser saveOutputAsDialog;


    private PathIconsManager icons;
    private PathTreeCellRenderer cellTreeRenderer;
    private PathWatcher pathWatcher;
    private PathComparator pathComparator;

    private KeyParser keyParser;


    private Book logObject;
    Book hashObject;
    private exelBook mkout;
    private Path workingRoot;

    DateFormat dateFormat;
    AttributesFormatter formatter;
    PathIconPainter iconsPainter;

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
    private String[] charsets = {
            "ISO-8859-1",
            "US-ASCII",
            "UTF-8",
            "UTF-16BE",
            "UTF-16LE",
            "UTF-16"
    };

    private ArrayList<Color> highlightColors;
    private Iterator<Color> highlightColorsIterator;


    private class WindowEventListener implements WindowListener {
        public void windowClosing(WindowEvent arg0) {
            iconsPainter.cleanUp();
            hashObject.cleanAll();
            try {
                Files.deleteIfExists(workingRoot);
            } catch (IOException exception) {

            }
            System.exit(0);
        }

        public void windowOpened(WindowEvent arg0) {}
        public void windowClosed(WindowEvent arg0) {}
        public void windowIconified(WindowEvent arg0) {}
        public void windowDeiconified(WindowEvent arg0) {}
        public void windowActivated(WindowEvent arg0) {}
        public void windowDeactivated(WindowEvent arg0) {}
    }


    public SetupForm() {
        super("Path Watcher");
        setContentPane(contentPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 500);
        this.setSize(d);
        this.setBackground(Color.DARK_GRAY);
        dateFormat = new SimpleDateFormat("YYYY:MM:dd - HH:mm:ss");
        formatter = new AttributesFormatter();

        setUpInventory();
        startUp();
        initTreeOfRoots();
        initTreeOfWatch();
        initButtons();
        initOthers();

        setVisible(true);
    }

    public void doNothing() {

    }

    private void printWithHighlight(ArrayList<textBoundedItem> items, JTextArea textArea) {
        textArea.setText("");
        Highlighter highLighter = textArea.getHighlighter();
        highLighter.removeAllHighlights();
        int index = 0;
        for (textBoundedItem item : items) {
            textArea.append(item.toString());
            try {
                highLighter.addHighlight(index, index + item.getLength() - 1, item.getHighLight());
            } catch (BadLocationException exception) {

            }
            index += item.getLength();
        }
    }

    private String searchProcess(JTextArea textArea, JTextField textField) {
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
                } else if (text.contains("-") == true) {
                    String insert = "";
                    String test = "";
                    int indexOf = text.indexOf("-");
                    if (indexOf >= 1 && text.length() >= 3) {
                        insert = text.substring(3, text.length());
                        int l = insert.length() + 1;
                        int startIndex = (int)text.charAt(indexOf - 1);
                        int endIndex = (int)text.charAt(indexOf + 1);
                        if (startIndex <= endIndex) {
                            for (int i = startIndex; i <= endIndex; i++) {
                                test = Character.toString((char)i) + insert;
                                for (int index = input.indexOf(test); index >= 0; index = input.indexOf(test, index + 1)) {
                                    matchedMarks.add(new TextMark(index, index + l, keyHighColor));
                                }
                            }
                        }
                    }
                }else {
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

    public void addHighlighters(Highlighter highLighter, ArrayList<TextMark> marks) {
        for (TextMark mark : marks) {
            try {
                highLighter.addHighlight(mark.getStart(), mark.getEnd(), mark.getHighLight());
            } catch (BadLocationException exception) {

            }
        }
    }

    public Color getNextTextHighlightColor() {
        if (highlightColorsIterator.hasNext() == false) {
            highlightColorsIterator = highlightColors.iterator();
        }
        return highlightColorsIterator.next();
    }

    public Color getFirstTextHighlightColor() {
        highlightColorsIterator = highlightColors.iterator();
        return highlightColorsIterator.next();
    }

    private void startUp() {
        int directorySelect = 0;
        saveOutputAsDialog = new JFileChooser();
        saveOutputAsDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directorySelect = saveOutputAsDialog.showOpenDialog(SetupForm.this);

        File file = saveOutputAsDialog.getSelectedFile();
        if (directorySelect == JFileChooser.CANCEL_OPTION || directorySelect == JFileChooser.ERROR_OPTION) {
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }

        String wp = file.toPath().toString() + File.separator + "_SITC_";
        workingRoot = Paths.get(wp);
        file = new File(wp);
        if (file.exists() == false) {
            file.mkdir();
        }

        treeToPick.setModel(new DefaultTreeModel(pathWatcher.makeTreeByName(hddRoots)));
        treeToPick.setCellRenderer(new PathTreeCellRenderer(treeToPick, false, icons));
        treeToPick.setBackground(Color.WHITE);

        tree1.setModel(new DefaultTreeModel(pathWatcher.makeTreeByName(workingRoot.toString())));
        tree1.setCellRenderer(cellTreeRenderer);
        tree1.setBackground(Color.WHITE);

        hashObject = new Book(file, "pwh");
        logObject = hashObject.newBookWithin("txt");
        //mkout = logObject.newExelBookThere("Exel");

        this.addWindowListener(new WindowEventListener());

        logObject.setCharsetName("ISO-8859-1");
        hashObject.setCharsetName("ISO-8859-1");


    }

    private void setUpInventory() {
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

        iconsPainter = new PathIconPainter();
        //iconsPainter.drawIcon("", "JPEG");

        icons = new PathIconsManager();
        icons.setFolderIcon("folder.jpg");
        icons.setFileIcon("file.jpg");
        icons.setChekedIcon("cheked.jpg");
        icons.setUnchekedIcon("uncheked.jpg");
        icons.setRootIcon("root.jpeg");
        icons.setCloseIcon("needOpen.jpg");
        icons.setOpenIcon("needClose.jpg");
        icons.setDeadIcon("deadRoot.jpg");

        icons.addTypeIcons("");
        icons.assignTypeIcon("class",   "java", "jar");
        icons.assignTypeIcon("exe",     "msi", "bat");
        icons.assignTypeIcon("eps",     "ps");
        icons.assignTypeIcon("mov",     "avi", "mpeg", "ac3");
        icons.assignTypeIcon("conf",    "cfg");
        icons.assignTypeIcon("doc",     "docx");
        icons.assignTypeIcon("asm",     "s");
        icons.assignTypeIcon("xls",     "xlsx");


        try {
            Image image = ImageIO.read(new File("loadTemplateIco.jpg"));
            if (image != null) {
                buttonToLoadTemplate.setIcon(new ImageIcon(image));
            }
        } catch (IOException exception) {

        }

        try {
            Image image = ImageIO.read(new File("eyeIco.jpg"));
            if (image != null) {
                stepButton.setIcon(new ImageIcon(image));
            }
        } catch (IOException exception) {

        }
        try {
            Image image = ImageIO.read(new File("breakIco.jpg"));
            if (image != null) {
                buttonToBreak.setIcon(new ImageIcon(image));
            }
        } catch (IOException exception) {

        }
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("jjj.jpg"));
        } catch (IOException e) {

        }
        this.setIconImage(image);


        keyParser = new KeyParser();
        pathWatcher = new PathWatcher(icons, progressBarOfWatch, iconsPainter);
        cellTreeRenderer = new PathTreeCellRenderer(tree1, false, icons);
        outputTextArea.setEditable(false);
        keyTexArea.setText("$");

        pathComparator = new PathComparator();

    }

    private void initTreeOfRoots () {
        treeToPick.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == treeToPick) {
                    if (SwingUtilities.isLeftMouseButton(e) == true) {
                        if (e.getClickCount() >= 2) {
                            PathTreeNode node = (PathTreeNode)treeToPick.getLastSelectedPathComponent();
                            if (node != null){
                                showPathInfo((Path) node.getUserObject());
                            }
                        }
                    } else if (SwingUtilities.isRightMouseButton(e) == true) {
                        pathWatcher.dragFromselected(tree1, treeToPick);
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
                    try {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getSource() == treeToPick) {
                    try {
                        setCursor(Cursor.getDefaultCursor());
                    } catch (Exception ex) {
                    }
                }
            }
        });
        treeToPick.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (e.getSource() == treeToPick) {
                    pathWatcher.createBranchAndInsertFromSelected(treeToPick);
                }
            }
        });
    }

    private void initTreeOfWatch ()
    {
        tree1.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == tree1) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        pathWatcher.removeSelected(tree1);
                        pathWatcher.setHashReady(false);
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        if (e.getClickCount() >= 2) {
                            if (pathWatcher.isTreeEmpty(tree1) == true) {
                                tree1.setModel(new DefaultTreeModel(pathWatcher.makeTreeByName(workingRoot.toString())));
                            } else {
                                PathTreeNode node = (PathTreeNode) tree1.getLastSelectedPathComponent();
                                if (node != null) {
                                    if ((node.getUserObject()).getClass() != PathTreeNode.class) {
                                        showPathInfo((Path) node.getUserObject());
                                    }
                                }
                            }
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
                if (e.getSource() == tree1) {
                    try {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getSource() == tree1) {
                    try {
                        setCursor(Cursor.getDefaultCursor());
                    } catch (Exception ex) {
                    }
                }
            }
        });
        tree1.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (e.getSource() == tree1) {
                    pathWatcher.createBranchAndInsertFromSelected(tree1);
                }
            }
        });
    }

    private void initButtons ()
    {
        stepButton.addActionListener(new ActionListener() {
            /**Watch there**/
            @Override
            public void actionPerformed(ActionEvent e) {
                disableWatchBlock();
                pathWatcher.makeHash(tree1, pathComparator, logObject, hashObject);

            }
        });
        buttonToBreak.setEnabled(false);
        buttonToBreak.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pathWatcher.setBreaker(true);
            }
        });
        buttonToLoadTemplate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveOutputAsDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int errno = saveOutputAsDialog.showOpenDialog(SetupForm.this);
                if (errno == JFileChooser.CANCEL_OPTION || errno == JFileChooser.ERROR_OPTION) {
                    return;
                }
                File file = saveOutputAsDialog.getSelectedFile();
                if (file.getName().contains("txt") == false) {
                    return;
                }
                loadKeysFromTemplate(file.toPath());
                saveOutputAsDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            }
        });

    }

    private void initOthers ()
    {
        progressBarOfWatch.setMinimum(0);
        progressBarOfWatch.setMaximum(100);
        keyTexArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyTyped(e);
                if (e.getSource() == keyTexArea) {
                    outputTextArea.setText("");
                    keyParser.setUp(pathComparator, keyTexArea.getText());
                }
            }
        });
        keyTexArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                if (e.getSource() == keyTexArea) {
                    keyTexArea.selectAll();
                    try {
                        System.out.println(keyTexArea.getText(e.getX(), e.getY()));
                    } catch (BadLocationException exception) {

                    }
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
        charsetChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == charsetChooser) {
                    hashObject.setCharsetName((String) charsetChooser.getSelectedItem());
                    logObject.setCharsetName((String) charsetChooser.getSelectedItem());
                    System.out.println((String) charsetChooser.getSelectedItem());
                }
            }
        });

        pathWatcher.addMakeListener(new PathWatcherEventListener() {
            @Override
            public void eventPerformed(PathWatcherEvent e) {
                pathWatcher.watchHash(pathComparator, logObject, hashObject);
            }
        });
        pathWatcher.addWatchListener(new PathWatcherEventListener() {
            @Override
            public void eventPerformed(PathWatcherEvent e) {
                printWithHighlight(pathComparator.getTextItems(), outputTextArea);
                enableWatchBlock();
            }
        });
        pathWatcher.addIntermediateMakeListener(new PathWatcherEventListener() {
            @Override
            public void eventPerformed(PathWatcherEvent e) {
                if (((PathWatcher)e.getSource()).isHashReady() == true) {
                    progressBarOfWatch.setValue((int)pathWatcher.getPathsCount());
                } else {

                }
            }
        });


        for (String chset : charsets) {
            charsetChooser.addItem(chset);
        }
    }

    private void enableWatchBlock ()
    {
        progressBarOfWatch.setValue(0);
        setCursor(Cursor.getDefaultCursor());
        stepButton.setEnabled(true);
        charsetChooser.setEnabled(true);
        keyTexArea.setEnabled(true);
        searchField.setEnabled(true);
        buttonToBreak.setEnabled(false);
    }
    private void disableWatchBlock ()
    {
        progressBarOfWatch.setValue(0);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        stepButton.setEnabled(false);
        charsetChooser.setEnabled(false);
        keyTexArea.setEnabled(false);
        searchField.setEnabled(false);
        buttonToBreak.setEnabled(true);
    }


    private void showPathInfo (Path path)
    {
        JOptionPane.showMessageDialog(this, formatter.showPathInfo(path));
    }


    private String templateInput;
    private void loadKeysFromTemplate (Path location)
    {
        Stream<String> output = null;
        templateInput = "";
        try {
            output = Files.lines(location, hashObject.getCharset());
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Cannot read this !");
            return;
        }
        try {
            output.forEach(s -> addToTemplate(s));

        } catch (UncheckedIOException exception) {
            JOptionPane.showMessageDialog(this, "Cannot read line !");
            return;
        }
        keyTexArea.setText(templateInput);
        keyParser.setUp(pathComparator, keyTexArea.getText());
        Word w;
        String searchArgs = "";
        for (PathKey key : pathComparator.getKeys()) {
            w = key.getKey();
            if (w.getValue().isEmpty() == true) {
                continue;
            }
            searchArgs += w.getValue() + ",";
        }
        searchArgs += ",";
        searchField.setText(searchArgs);
        searchProcess(outputTextArea, searchField);
    }

    void addToTemplate (String from)
    {
        templateInput += from;
    }

}
