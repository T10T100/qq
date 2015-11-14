package com.company;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Afony on 01.11.2015.
 */
public class KeyParser {


    public KeyParser ()
    {

    }


    public void setUp(PathComparator comparator, String in, String... args)
    {
        String garbage = "[garbage]";

        ArrayList<Word> templates = this.getArrayOfWords(in, '{', '}');
        if (templates.isEmpty() == false) {
            garbage = templates.remove(0).getValue();
        }


        ArrayList<Word> keys = this.getArrayOfWords(garbage, ',');
        if (keys.isEmpty() == false) {
            garbage = keys.remove(0).getValue();
        }

        for (Word w :templates) {
            w.removeSpacesFromWord();
        }

        ArrayList<Word> keysToRemove = new ArrayList<>();
        ArrayList<Word> keysToinsert = new ArrayList<>();
        String insert = "";
        for (Word key : keys) {
            key.insertWordValueToNameBetween(templates, '[', ']');
            key.removeSpacesFromWord();
            if (key.getName().isEmpty() == true) {
                keysToRemove.add(key);
                continue;
            }
            String name = key.getName();
            if (name.contains("-") == true) {
                keysToRemove.add(key);
                int indexOf = name.indexOf("-");
                if (indexOf >= 1 && name.length() >= 3) {
                    insert = name.substring(3, name.length());
                    char start = name.charAt(indexOf - 1);
                    char end = name.charAt(indexOf + 1);
                    int startIndex = (int)start;
                    int endIndex = (int)end;
                    if (startIndex <= endIndex) {
                        for (int i = startIndex; i <= endIndex; i++) {
                            keysToinsert.add(new Word(Character.toString((char)i) + insert, insert));
                        }
                    }
                }
            } else if (name.contains("<")) {
                keysToRemove.add(key);
                Word word = new Word(name, '<');
                if (word.getName().isEmpty() == true || word.getValue().isEmpty() == true) {
                    continue;
                }
                word.removeAllButDigitsFromWord();
                int bottomValue = Integer.parseInt(word.getName());
                int topValue = Integer.parseInt(word.getValue());
                for (int i = bottomValue; i <= topValue; i++) {
                    keysToinsert.add(new Word(Integer.toString(i), ""));
                }
            }
        }
        keys.removeAll(keysToRemove);
        keys.addAll(keysToinsert);

        comparator.setUp(keys);

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
