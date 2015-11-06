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

    public ArrayList<String> parseToDefault (PathComparator comparator, String in, String... args)
    {
        String garbage = "[garbage]";
        ArrayList<String> output = new ArrayList<>();
        ArrayList<Word> keys = this.getArrayOfWords(in, '[', ']');
        if (keys.isEmpty() == false) {
            garbage = keys.remove(0).getValue();
        }
        for (Word w : keys) {
            output.add(new String(w.toString()));
        }
        keys = this.getArrayOfWords(garbage, '<', '>');
        if (keys.isEmpty() == false) {
            garbage = keys.remove(0).getValue();
        }
        for (Word w : keys) {
            output.add(new String(w.toString()));
        }
        output.add(new String(garbage));
        return output;
    }

    private ArrayList<Word> getArrayOfWords (String input, char begin, char end)
    {
        String garbage = "[garbage]";
        Word word = new Word(input);
        ArrayList<String> array = word.getArrayFromValue(begin, end);
        if (array.isEmpty() == false) {
            garbage = array.remove(0);
        }
        ArrayList<Word> words = new ArrayList<>();

        for (String name : array) {
            words.add(new Word(name, ':'));
        }
        words.add(0, new Word(garbage));
        return words;
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

    private ArrayList<String> separateEnum (String input, char separator, char end)
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
            if (parsing[i] == end) {
                outputCollection.add(new String(bag));
                return outputCollection;
            }
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
                return output;
            } else {
                output += parsing[i];
            }
        }
        return output;
    }
}
