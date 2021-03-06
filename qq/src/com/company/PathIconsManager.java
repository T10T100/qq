package com.company;

/**
 * Created by k on 19.11.2015.
 */


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Operator on 27.10.2015.
 */

/*
*     private String[] iconsPath = {
            "cfolder.jpg",
            "lfolder.jpg",
            "locked.jpg",
            "completed.jpg",
            "ready.jpg"
    };
* */
public class PathIconsManager {
    private ImageIcon folderIcon;
    private ImageIcon fileIcon;
    private ImageIcon chekedIcon;
    private ImageIcon unchekedIcon;
    private ImageIcon rootIcon;
    private ImageIcon openIcon;
    private ImageIcon closeIcon;
    private ImageIcon deadIcon;
    private Map<String, ImageIcon> typeIcons;
    private Map<String, ImageIcon> userTypeIcons;
    private boolean skipDefaults;

    public PathIconsManager ()
    {
        this.folderIcon = null;
        this.fileIcon = null;
        this.chekedIcon = null;
        this.unchekedIcon = null;
        typeIcons = new HashMap<>();
        userTypeIcons = new HashMap<>();
        skipDefaults = true;
    }

    public ImageIcon getImageFrom (String location)
    {
        return new ImageIcon(getImage(location));
    }

    private BufferedImage getImage (String location)
    {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(location));
        } catch (IOException e) {

        }
        return image;
    }
    private BufferedImage getImage (File location)
    {
        BufferedImage image = null;
        try {
            image = ImageIO.read(location);
        } catch (IOException e) {
            return null;
        }
        return image;
    }

    public void addTypeIcon (String name, String location)
    {
        typeIcons.put(name, new ImageIcon(getImage(location)));
    }
    public void addTypeIcon (String name, File location)
    {
        typeIcons.put(name, new ImageIcon(getImage(location)));
    }
    public void addUserTypeIcon (String name, String location)
    {
        userTypeIcons.put(name, new ImageIcon(getImage(location)));
    }
    public void addUserTypeIcon (String name, File location)
    {
        userTypeIcons.put(name, new ImageIcon(getImage(location)));
    }
    public void addUserTypeIcon (ImageIcon icon, String type)
    {
        userTypeIcons.put(type, icon);
    }
    public void clearUserIcons ()
    {
        userTypeIcons.clear();
    }


    public void addTypeIcons (File location)
    {
        Path path = location.toPath();
        File file;
        Word word = null;
        String name;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path p : stream) {
                file = p.toFile();
                name = file.getName();
                if (name.contains("type_")) {
                    word = new Word(name, '_');
                    word.splitValueAndSet('.');
                    addTypeIcon(word.getName(), file);
                }
            }
        } catch (IOException exception) {

        }
    }
    public void addUserTypeIcons (String location)
    {
        addUserTypeIcons(new File(location));
    }

    public void addUserTypeIcons (File location)
    {
        Path path = location.toPath();
        File file;
        Word word = null;
        String name;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path p : stream) {
                file = p.toFile();
                name = file.getName();
                if (name.contains("type_")) {
                    word = new Word(name, '_');
                    word.splitValueAndSet('.');
                    addUserTypeIcon(word.getName(), file);
                }
            }
        } catch (IOException exception) {

        }
    }
    public void addTypeIcons (String location)
    {
        addTypeIcons(new File(location));
    }




    public ImageIcon getTypeIcon (String name)
    {
        if (typeIcons.containsKey(name) == true && skipDefaults == false) {
            return typeIcons.get(name);
        } else if (userTypeIcons.containsKey(name) == true) {
            return userTypeIcons.get(name);
        } else {
            return fileIcon;
        }
    }

    public ImageIcon getTypeIcon (File file)
    {
        String name = file.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        return getTypeIcon(name);
    }

    public void assignTypeIcon (String dest, String... src)
    {
        ImageIcon icon = typeIcons.get(dest);
        if (icon == null) {
            return;
        }
        for (String s : src) {
            System.out.println(s);
            typeIcons.put(s, icon);
        }
    }
    public void assignUserTypeIcon (String dest, String... src)
    {
        ImageIcon icon = typeIcons.get(dest);
        if (icon == null) {
            return;
        }
        for (String s : src) {
            System.out.println(s);
            userTypeIcons.put(s, icon);
        }
    }

    public void setFolderIcon(String location)
    {
        try {
            this.folderIcon = new ImageIcon(getImage(location));
        } catch (NullPointerException e) {

        }
    }
    public void setFileIcon(String location)
    {
        try {
            this.fileIcon = new ImageIcon(getImage(location));
        } catch (NullPointerException e) {

        }
    }
    public void setUnchekedIcon(String location)
    {
        try {
            this.unchekedIcon = new ImageIcon(getImage(location));
        } catch (NullPointerException e) {

        }
    }
    public void setChekedIcon(String location)
    {
        try {
            this.chekedIcon = new ImageIcon(getImage(location));
        } catch (NullPointerException e) {

        }
    }

    public void setRootIcon(String location)
    {
        try {
            this.rootIcon = new ImageIcon(getImage(location));
        } catch (NullPointerException e) {

        }
    }

    public void setCloseIcon(String location)
    {
        try {
            this.closeIcon = new ImageIcon(getImage(location));
        } catch (NullPointerException e) {

        }
    }
    public void setOpenIcon(String location)
    {
        try {
            this.openIcon = new ImageIcon(getImage(location));
        } catch (NullPointerException e) {

        }
    }
    public void setDeadIcon(String location)
    {
        try {
            this.deadIcon = new ImageIcon(getImage(location));
        } catch (NullPointerException e) {

        }
    }

    public ImageIcon getFolderIcon()
    {
        return this.folderIcon;
    }
    public ImageIcon getFileIcon()
    {
        return this.fileIcon;
    }
    public ImageIcon getUnchekedIcon()
    {
        return this.unchekedIcon;
    }
    public ImageIcon getChekedIcon()
    {
        return this.chekedIcon;
    }
    public ImageIcon getRootIcon ()
    {
        return this.rootIcon;
    }
    public ImageIcon getCloseIcon()
    {
        return closeIcon;
    }
    public ImageIcon getOpenIcon()
    {
        return openIcon;
    }
    public ImageIcon getDeadIcon()
    {
        return deadIcon;
    }

    public void setSkipDefaults(boolean skipDefaults)
    {
        this.skipDefaults = skipDefaults;
    }
}