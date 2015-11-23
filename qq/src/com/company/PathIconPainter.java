package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by k on 23.11.2015.
 */
public class PathIconPainter extends JComponent {
    private ArrayList<File> cleanUpArchive;


    public PathIconPainter ()
    {
        cleanUpArchive = new ArrayList<>();
    }

    @Override
    public void paint (Graphics g) {
        super.paint(g);

    }


    public ImageIcon drawIcon (String location, String text)
    {
        String filePath = location + File.separator + "type_" + text + ".jpg";
        BufferedImage image = new BufferedImage(48, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setFont(new Font("TimesRoman", Font.ITALIC, 16));
        g.setBackground(Color.PINK);
        g.fillRect(0, 0, 48, 16);
        g.setColor(Color.red);
        g.drawString(text, 5, 16);
        g.finalize();
        File imageFile = new File(filePath);
        if (imageFile.exists() != true) {
            try {
                ImageIO.write(image, "jpg", imageFile);
            } catch (IOException exception) {

            }
        }
        cleanUpArchive.add(imageFile);
        return new ImageIcon(image);
    }
    public ImageIcon drawIcon (Path location, String text)
    {
        String filePath = location.toString() + File.separator + "type_" + text + ".jpg";
        BufferedImage image = new BufferedImage(text.length() * 12, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setFont(new Font("TimesRoman", Font.ITALIC, 16));
        g.setBackground(Color.PINK);
        g.fillRect(0, 0, text.length() * 12, 16);
        g.setColor(Color.red);
        g.drawString(text, 5, 16);
        g.finalize();
        File imageFile = new File(filePath);
        if (imageFile.exists() != true) {
            try {
                ImageIO.write(image, "jpeg", imageFile);
            } catch (IOException exception) {

            }
        }
        cleanUpArchive.add(imageFile);
        return new ImageIcon(image);
    }

    public void cleanUp ()
    {
        for (File file : cleanUpArchive) {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException exception) {

            }
        }
        cleanUpArchive.removeAll(cleanUpArchive);
    }


    public ArrayList<File> getCleanUpArchive()
    {
        return cleanUpArchive;
    }
}

/*
* private BufferedImage getImage (String location)
    {

        return image;
    }*/