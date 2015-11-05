package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

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
public class PathIconManager {
    public ImageIcon nowFolderIcon;
    public ImageIcon closedFolderIcon;
    public ImageIcon linkedFolderIcon;
    public ImageIcon lockedFolderIcon;
    public ImageIcon completedIcon;
    public ImageIcon readyIcon;
    public ImageIcon fileIcon;
    public ImageIcon hiddenFileIcon;

    public PathIconManager (String... paths)
    {
        LinkedList<ImageIcon> list = new LinkedList<>();
        BufferedImage image = null;
        for (String s : paths) {
            try {
                image = ImageIO.read(new File(s));
            } catch (IOException e) {

            }
            list.add(null);
        }
        nowFolderIcon    = list.removeFirst();
        closedFolderIcon = list.removeFirst();
        linkedFolderIcon = list.removeFirst();
        lockedFolderIcon = list.removeFirst();
        completedIcon    = list.removeFirst();
        readyIcon        = list.removeFirst();
        fileIcon         = list.removeFirst();
        hiddenFileIcon   = list.removeFirst();
    }

}
