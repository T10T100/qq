package com.company;

import java.util.ArrayList;

/**
 * Created by Afony on 01.11.2015.
 */
public class KeyParser {


    public KeyParser ()
    {

    }


    public ArrayList<String> parseToDefault (PathComparator comparator, String in, String... args)
    {
        String garbage = "[garbage]";
        ArrayList<String> output = new ArrayList<>();

        ArrayList<Word> templates = this.getArrayOfWords(in, '<', '>');
        if (templates.isEmpty() == false) {
            garbage = templates.remove(0).getValue();
        }

        ArrayList<Word> patterns = this.getArrayOfWords(garbage, '{', '}');
        if (patterns.isEmpty() == false) {
            garbage = patterns.remove(0).getValue();
        }

        ArrayList<Word> keys = this.getArrayOfWords(garbage, ',');
        if (keys.isEmpty() == false) {
            garbage = keys.remove(0).getValue();
        }




        for (Word w :templates) {
            w.removeSpacesFromWord();
        }
        for (Word key : keys) {
            key.insertWordValueToNameBetween(templates, '[', ']');
            key.removeSpacesFromWord();
        }

        ArrayList<String> arg;
        for (Word w : patterns) {
            arg = w.getArrayFromValue('\'', '\'');
            w.setValue(arg.remove(0));
            w.setArgs(arg);
            w.removeSpacesFromWord();
            if (w.name.contentEquals("key")) {
                for (String key : w.getArgs()) {
                    int indexOf = key.indexOf("-");
                    if (indexOf >= 1 && key.length() >= 3) {
                        char start = key.charAt(indexOf - 1);
                        char end = key.charAt(indexOf + 1);
                        int startIndex = (int)start;
                        int endIndex = (int)end;
                        if (startIndex <= endIndex) {
                            for (int i = startIndex; i < endIndex; i++) {
                                keys.add(new Word(Character.toString((char)i), Integer.toString(i)));
                            }
                        } else {
                            /*Error!*/
                        }
                    }
                }
            } else {

            }
        }

        comparator.setUp(keys);

        output.add("Keys : ");
        for (Word w : keys) {
            output.add(w.toString());
        }
        output.add("\nTemplates : ");
        for (Word w :templates) {
            output.add(w.toString());
        }
        output.add("\nPatterns : ");
        for (Word w :patterns) {
            output.add(w.toString());
        }
        output.add("\nGarbage : ");
        output.add(garbage);
        output.add("\nSystem : ");
        output.add(comparator.getSystemInfo());
        output.add("\nRegular : \n");
        /*
        if (setted == true) {
            Pattern p = Pattern.compile(pattern.value);
            Matcher matcher = p.matcher("0123456789abcd");
            output.add("0123456789abcd\n");
            output.add(matcher.toMatchResult().toString());
        }
        */
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
    private ArrayList<Word> getArrayOfWords (String input, char delimiter)
    {
        String garbage = "[garbage]";
        Word word = new Word(input);
        ArrayList<String> array = word.getArrayFromValue(delimiter);
        if (array.isEmpty() == false) {
            garbage = array.remove(array.size() - 1);
        }
        ArrayList<Word> words = new ArrayList<>();

        for (String name : array) {
            words.add(new Word(name, ':'));
        }
        words.add(0, new Word(garbage));
        return words;
    }

}
