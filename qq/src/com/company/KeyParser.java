package com.company;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Afony on 01.11.2015.
 */
public class KeyParser {

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
        Word output = new Word();
        Word input = new Word(in);
        Word garbage;

        if (input.containsKeyInTemplate(DefaultParseKeys.CLEAR) == true || input.containsKeyInTemplate(DefaultParseKeys.SKIP_TEMPLATES) == true) {
            output.setTemplate("()");
        } else if (input.containsKeyInTemplate(DefaultParseKeys.ANY) == true){
            output.setTemplate("'any'");
        } else {
            output = input;
        }

        String additionString = new String("");


        ArrayList<Word> listOfTemplates = separate(output, '<', '>');
        garbage = listOfTemplates.remove(0);

        ArrayList<Word> keyWordList = separateEnum(garbage, ',');
        ArrayList<Word> keyAttributesList;
        String wordTemplate = "";
        Word word;
        for (Word key : keyWordList) {
            keyAttributesList = separateEnum(key, ':');
            if (keyAttributesList.size() > 1) {
                word = keyAttributesList.remove(0);
                key.setName(word.getTemplate());
                for (Word w : keyAttributesList) {
                    wordTemplate += w.getTemplate();
                }
                key.setTemplate(wordTemplate);
            } else if (keyAttributesList.isEmpty() == false) {
                word = keyAttributesList.remove(0);
                key.setTemplate(word.getTemplate());
            }
        }



        LinkedList<Word> templates = new LinkedList<>();

        for (Word t : listOfTemplates) {
            templates.add(new Word(separateEnum(t, ':')));
        }

        int i = 0;
        String[] outCollection = new String[keyWordList.size() + listOfTemplates.size() + 1];
        String name = "";
        for (Word s : keyWordList) {
            name = concatTemplates(s + additionString, templates);
            if (s.containsKeyInTemplate(DefaultParseKeys.WNO_DIGITS) == true) {
                outCollection[i++] = new String(removeDigits(name));
            }
            outCollection[i++] = new String(name);

        }

        comparator.setArgs(outCollection);

        outCollection[i++] = new String("\ntemplates : \n");

        for (Word template : templates) {
            outCollection[i++] = new String(template.toString());
        }

        return outCollection;
    }

    private String[] extractName (Word input, char openChar, char closeChar)
    {
        int length = input.getTemplateLength();
        if (length < 1) {
            length = 1;
            input.addToTemplate(" ");
        }
        int i = 0;
        char[] temp = input.toCharArrayOfTemplate();
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

    private ArrayList<Word> separate (Word input, char openChar, char closeChar)
    {
        int length = input.getTemplateLength();
        if (length < 1) {
            length = 1;
            input.addToTemplate(" ");
        }
        ArrayList<Word> outputCollection = new ArrayList<>();
        String[] bag = {new String(""), new String("")};
        int copyIndex = 1;
        char separator = openChar;
        char[] parsing = input.toCharArrayOfTemplate();
        for (int i = 0; i < length; i++) {
            if (parsing[i] == separator) {
                if (copyIndex == 1) {
                    copyIndex = 0;
                    separator = closeChar;
                    bag[copyIndex] = "";
                } else {
                    copyIndex = 1;
                    separator = openChar;
                    outputCollection.add(new Word(bag[0]));
                }
            } else {
                bag[copyIndex] += parsing[i];
            }
        }
        outputCollection.add(0, new Word(bag[1]));
        return outputCollection;
    }

    private ArrayList<Word> separateEnum (Word input, char separator)
    {
        int length = input.getTemplateLength();
        if (length < 1) {
            length = 1;
            input.addToTemplate(" ");
        }
        ArrayList<Word> outputCollection = new ArrayList<>();
        String bag = "";
        char[] parsing = input.toCharArrayOfTemplate();
        for (int i = 0; i < length; i++) {
            if (parsing[i] == separator) {
                outputCollection.add(new Word(bag));
                bag = "";
            } else {
                bag += parsing[i];
            }
        }
        outputCollection.add(0, new Word(bag));
        return outputCollection;
    }

    public String concatTemplates (String input, LinkedList<Word> templates)
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
                for (Word template : templates) {
                    if (name.contains(template.getName()) == true) {
                        output += template.getTemplate();
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
