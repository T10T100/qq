package com.company;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Afony on 01.11.2015.
 */
public class KeyParser {

    private class TemplateItem {
        public String value;
        public String name;

        public TemplateItem (String name, String value)
        {
            this.name = name;
            this.value = value;
        }

        public TemplateItem (String... s)
        {
            this.name = s[0];
            this.value = s[1];
        }
    }


    public KeyParser ()
    {

    }

    public String removeChars (String input, char removable)
    {
        char[] array = input.toCharArray();
        int length = input.length();
        String output = "";
        for (int i = 0; i < length; i++) {
            if (array[i] != removable) {
                output += array[i];
            }
        }
        return output;
    }

    public String removeSpaces (String input)
    {
        char[] array = input.toCharArray();
        int length = input.length();
        String output = "";
        for (int i = 0; i < length; i++) {
            if (Character.isSpaceChar(array[i]) == false) {
                output += array[i];
            }
        }
        return output;
    }

    public String removeDigits (String input)
    {
        char[] array = input.toCharArray();
        int length = input.length();
        String output = "";
        for (int i = 0; i < length; i++) {
            if (Character.isDigit(array[i]) == false) {
                output += array[i];
            }
        }
        return output;
    }

    public String[] parseToDefault (PathComparator comparator, String in, String... args)
    {
        String output = "";
        String input = removeSpaces(in);
        String garbage;

        if (input.contains(DefaultParseKeys.CLEAR.get()) == true || input.contains(DefaultParseKeys.SKIP_TEMPLATES.get()) == true) {
            output = "()";
        } else if (input.contains(DefaultParseKeys.ANY.get()) == true){
            output = "('any')";
        } else {
            output = input;
        }

        String additionString = "";




        /*
        if (garbage.contains(getKey(DefaultKeys.WNOSIZE)) == true) {
            additionString += " -!s ";
        }
        if (garbage.contains(getKey(DefaultKeys.FINAL)) == true) {
            additionString += " -f ";
        }
        */


        ArrayList<String> listOfTemplates = separate(output, '<', '>');
        garbage = listOfTemplates.remove(0);

        ArrayList<String> list = separateEnum(garbage, ',');



        LinkedList<TemplateItem> templates = new LinkedList<>();

        for (String t : listOfTemplates) {
            templates.add(new TemplateItem(extractName(t, '[', ']')));
        }

        int i = 0;
        String[] outCollection = new String[list.size() + listOfTemplates.size() + 1];
        String name = "";
        for (String s : list) {

            name = concatTemplates(s + additionString, templates);
            if (s.contains(DefaultParseKeys.WNO_DIGITS.get()) == true) {
                outCollection[i++] = new String(removeDigits(name));
            }
            outCollection[i++] = new String(name);

        }

        comparator.setArgs(outCollection);

        outCollection[i++] = new String("\ntemplates : \n");

        for (TemplateItem template : templates) {
            outCollection[i++] = new String(template.name + " -> " + template.value);
        }

        return outCollection;
    }

    private String[] extractName (String input, char openChar, char closeChar)
    {
        int length = input.length();
        if (length < 1) {
            length = 1;
            input += " ";
        }
        int i = 0;
        char[] temp = input.toCharArray();
        String[] output = {new String(""), new String("")};

        char separator = openChar;
        int bufferIndex = 1;
        boolean copyAble = true;
        do {
            if (temp[i] == separator && copyAble == true) {
                if (bufferIndex == 1) {
                    separator = closeChar;
                    bufferIndex = 0;
                } else {
                    copyAble = false;
                    bufferIndex = 1;
                }
            } else {
                    output[bufferIndex] += temp[i];
            }
        } while (++i < length);
        return output;
    }

    private ArrayList<String> separate (String input, char openChar, char closeChar)
    {
        int length = input.length();
        if (length < 1) {
            length = 1;
            input += " ";
        }
        ArrayList<String> outputCollection = new ArrayList<>();
        String[] bag = {new String(""), new String("")};
        int copyIndex = 1;
        char separator = openChar;
        char[] parsing = input.toCharArray();
        for (int i = 0; i < length; i++) {
            if (parsing[i] == separator) {
                if (copyIndex == 1) {
                    copyIndex = 0;
                    separator = closeChar;
                    bag[copyIndex] = "";
                } else {
                    copyIndex = 1;
                    separator = openChar;
                    outputCollection.add(new String(bag[0]));
                }
            } else {
                bag[copyIndex] += parsing[i];
            }
        }
        outputCollection.add(0, new String(bag[1]));
        return outputCollection;
    }

    private ArrayList<String> separateEnum (String input, char separator)
    {
        int length = input.length();
        if (length < 1) {
            length = 1;
            input += " ";
        }
        ArrayList<String> outputCollection = new ArrayList<>();
        String bag = "";
        char[] parsing = input.toCharArray();
        for (int i = 0; i < length; i++) {
            if (parsing[i] == separator) {
                outputCollection.add(new String(bag));
                bag = "";
            } else {
                bag += parsing[i];
            }
        }
        outputCollection.add(new String(bag));
        return outputCollection;
    }

    public String concatTemplates (String input, LinkedList<TemplateItem> templates)
    {
        int length = input.length();
        if (length < 1) {
            length = 1;
            input += " ";
        }
        String output = "";
        String name = "";
        char[] parsing = input.toCharArray();
        for (int i = 0; i < length; i++) {
            if (parsing[i] == '+') {
                name = input.substring(i + 1);
                for (TemplateItem template : templates) {
                    if (name.contentEquals(template.name) == true) {
                        output += template.value;
                        return output;
                    }
                }
                output += "'template'";
            } else {
                output += parsing[i];
            }
        }
        return output;
    }
}
