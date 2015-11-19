package com.company;
import com.jniwrapper.win32.jexcel.Application;
import com.jniwrapper.win32.jexcel.ExcelException;
import com.jniwrapper.win32.jexcel.Workbook;
import com.jniwrapper.win32.jexcel.Worksheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by k on 17.11.2015.
 */
public class exelBook  {
    private Path location;
    private File file;
    private String name;

    public exelBook (File root, String name)
    {
        this.location = root.toPath();
        Application application = null;
        try {
           application = new Application();
        } catch (ExcelException exception) {
            exception.printStackTrace();
            return;
        } catch (ExceptionInInitializerError exception) {
            exception.printStackTrace();
            return;
        }
        if (!application.isVisible())
        {
            application.setVisible(true);
        }

        this.file = new File(this.location.toString() + File.separator + name + ".xls");
        file.mkdirs();
        try {
            file.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }
        Workbook workbook = null;
        try {
            workbook = application.openWorkbook(this.file, true, "password");
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return;
        } catch (ExcelException exception) {
            exception.printStackTrace();
            return;
        }
        Worksheet customSheet = workbook.getWorksheet("Custom sheet");
    }

    public void cleanUp ()
    {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException exception) {

        }
    }
}
