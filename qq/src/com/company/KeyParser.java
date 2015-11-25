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
            String value = key.getValue();
            ArrayList<String> namesArray = key.collectCharactersFromName('-');
            if (namesArray.isEmpty() == false) {
                keysToRemove.add(key);
                for (String newName : namesArray) {
                    keysToinsert.add(new Word(newName, value));
                }
            } else if ((namesArray = key.collectEnumFromName('<')).isEmpty() == false) {
                keysToRemove.add(key);
                for (String newName : namesArray) {
                    keysToinsert.add(new Word(newName, value));
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
            words.add(new Word(name, '='));
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
            words.add(new Word(name, '='));
        }
        words.add(0, new Word(garbage));
        return words;
    }


}
