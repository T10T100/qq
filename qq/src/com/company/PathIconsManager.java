package com.company;

/**
 * Created by k on 19.11.2015.
 */


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    private Map<String, ImageIcon> typeIcons;

    public PathIconsManager ()
    {
        this.folderIcon = null;
        this.fileIcon = null;
        this.chekedIcon = null;
        this.unchekedIcon = null;
        typeIcons = new HashMap<>();
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

    public void addTypeIcon (String name, String location)
    {
        typeIcons.put(name, new ImageIcon(getImage(location)));
    }

    public ImageIcon getTypeIcon (String name)
    {
        if (typeIcons.containsKey(name) == false) {
            return fileIcon;
        }
        return typeIcons.get(name);
    }

    public ImageIcon getTypeIcon (File file)
    {
        String name = file.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        return getTypeIcon(name);
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

}