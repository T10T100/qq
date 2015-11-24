package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Operator on 24.11.2015.
 */
public class colorPalette {
    Map<String, Color> map;
    public colorPalette ()
    {
        map = new HashMap<>();
        map.put("black", Color.BLACK);
        map.put("red", Color.red);
        map.put("orange", Color.orange);
        map.put("yellow", Color.yellow);
        map.put("magenta", Color.magenta);
        map.put("black", Color.BLACK);
        map.put("maroon", new Color (127, 0, 0, 200));
        map.put("green", new Color(10, 150, 0, 200));
        map.put("blue", new Color (0, 10, 180, 200));
    }

    public Color getColorByName (String name)
    {
        if (map.containsKey(name) == true) {
            return map.get(name);
        }
        return Color.BLACK;
    }
}
