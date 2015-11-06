package com.company;

import jdk.nashorn.internal.runtime.regexp.joni.MatcherFactory;
import sun.misc.Regexp;

import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Afony on 04.11.2015.
 */
public class Word {
    ArrayList<Word> bounds;
    ArrayList<String> args;
    String name;
    String value;
    public Word (ArrayList<Word> words)
    {
        if (words.isEmpty() == true) {
            this.name = new String("[name]");
            this.value = new String("[value]");
            return;
        }
        for (Word word : words) {
            this.bounds.add(word);
        }
        this.name = words.get(0).name;
        this.value = words.get(0).value;
    }
    public  Word (String... strings)
    {
        int length = strings.length;
        this.name = new String(strings[0]);
        this.value = new String(strings[1]);
        length = strings.length - length;
        for (int i = 2; i < length; i++) {
            this.args.add(i, new String(strings[i]));
        }
    }
    public Word (String name, String value)
    {
        this.name = new String(name);
        this.value = new String(value);
    }
    public Word (String value)
    {
        this.value = new String(value);
    }
    public Word ()
    {
        this.name = "[name]";
        this.value = "[value]";
    }

    private String[] split (char[] parsing, char splitter)
    {
        String[] outputCollection = new String[2];
        String bag = new String();
        int i = 0;
        for (; i < parsing.length; i++) {
            if (parsing[i] == splitter) {
                i++;
                break;
            }
            bag += parsing[i];
        }
        outputCollection[0] = bag;
        bag = "";
        for (; i < parsing.length; i++) {
            bag += parsing[i];
        }
        outputCollection[1] = bag;
        return outputCollection;
    }
    public String[] splitName (char splitter)
    {
        return this.split(this.name.toCharArray(), splitter);
    }
    public String[] splitValue (char splitter)
    {
        return this.split(this.value.toCharArray(), splitter);
    }

    private String splitTo (char[] parsing, char splitter)
    {
        String bag = new String();
        for (int i = 0; i < parsing.length; i++) {
            if (parsing[i] == splitter) {
                break;
            }
            bag += parsing[i];
        }
        return bag;
    }
    public String splitNameTo (char splitter)
    {
        return this.splitTo(name.toCharArray(), splitter);
    }
    public String splitValueTo (char splitter)
    {
        return this.splitTo(value.toCharArray(), splitter);
    }

    private String splitFrom (char[] parsing, char splitter)
    {
        String bag = new String();
        int index = 0;
        for (int i = 0; i < parsing.length; i++) {
            if (parsing[i] == splitter) {
                index = i + 1;
                break;
            }
        }
        for (int i = index; i < parsing.length; i++) {
            bag += parsing[i];
        }
        return bag;
    }
    public String splitNameFrom (char splitter)
    {
        return this.splitFrom(this.name.toCharArray(), splitter);
    }
    public String splitValueFrom (char splitter)
    {
        return this.splitFrom(this.value.toCharArray(), splitter);
    }
    public String splitNameToAndSetName (char splitter)
    {
        this.name = this.splitNameTo(splitter);
        return this.name;
    }
    public String splitNameFromAndSetName (char splitter)
    {
        this.name = this.splitNameFrom(splitter);
        return this.name;
    }
    public String splitNameToAndSetValue (char splitter)
    {
        this.value = this.splitValueTo(splitter);
        return this.value;
    }
    public String splitNameFromAndSetValue (char splitter)
    {
        this.value = this.splitValueFrom(splitter);
        return this.value;
    }
    public  String[] splitNameAndSet (char splitter)
    {
        String[] output = split(this.name.toCharArray(), splitter);
        this.name = output[0];
        this.value = output[1];
        return output;
    }
    public  String[] splitValueAndSet (char splitter)
    {
        String[] output = split(this.value.toCharArray(), splitter);
        this.name = output[0];
        this.value = output[1];
        return output;
    }
    private String[] extractFromOnce (char[] parsing, char begin, char end)
    {
        int i = 0;
        String[] output = {new String(""), new String("")};
        char separator = begin;
        int bufferIndex = 1;
        boolean copyAble = true;
        do {
            if (parsing[i] == separator && copyAble == true) {
                if (bufferIndex == 1) {
                    separator = end;
                    bufferIndex = 0;
                } else {
                    copyAble = false;
                    bufferIndex = 1;
                }
            } else {
                output[bufferIndex] += parsing[i];
            }
        } while (++i < parsing.length);
        return output;
    }
    public String[] getFromNameOnce (char begin, char end)
    {
        return this.extractFromOnce(this.name.toCharArray(), begin, end);
    }
    public String[] getFromValueOnce (char begin, char end)
    {
        return this.extractFromOnce(this.value.toCharArray(), begin, end);
    }
    public String getFromNameOnceAndSetName (char begin, char end)
    {
        this.name = this.extractFromOnce(this.name.toCharArray(), begin, end)[0];
        return this.name;
    }
    public String getFromValueOnceAndSetValue (char begin, char end)
    {
        if (this.value.isEmpty() == true) {
            this.value += " ";
        }
        this.value = this.extractFromOnce(this.name.toCharArray(), begin, end)[0];
        return this.value;
    }

    private ArrayList<String> getArray (char[] parsing, char begin, char end)
    {
        ArrayList<String> outputCollection = new ArrayList<>();
        String[] bag = {new String(""), new String("")};
        int copyIndex = 1;
        char separator = begin;
        for (int i = 0; i < parsing.length; i++) {
            if (parsing[i] == separator) {
                if (copyIndex == 1) {
                    copyIndex = 0;
                    separator = end;
                    bag[copyIndex] = "";
                } else {
                    copyIndex = 1;
                    separator = begin;
                    outputCollection.add(new String(bag[0]));
                }
            } else {
                bag[copyIndex] += parsing[i];
            }
        }
        outputCollection.add(0, new String(bag[1]));
        return outputCollection;
    }

    public ArrayList<String> getArrayFromValue (char begin, char end)
    {
        return getArray(this.value.toCharArray(), begin, end);
    }
    public ArrayList<String> getArrayFromValueAndSetNameAsFirst (char begin, char end)
    {
        ArrayList<String> array = this.getArrayFromValue(begin, end);
        if (array.isEmpty() == false) {
            this.name = array.remove(0);
        }
        return array;
    }
    public ArrayList<String> getArrayFromValueAndSetValueAsFirst (char begin, char end)
    {
        ArrayList<String> array = new ArrayList<>();
        array.addAll(getArrayFromValue(begin, end));
        if (array.isEmpty() == false) {
            this.value = array.remove(0);
        } else {
            this.value = "[value]";
        }
        return array;
    }

    public ArrayList<String> getArrayFromName (char begin, char end)
    {
        return getArray(this.name.toCharArray(), begin, end);
    }
    public ArrayList<String> getArrayFromNameAndSetNameAsFirst (char begin, char end)
    {
        ArrayList<String> array = this.getArrayFromName(begin, end);
        if (array.isEmpty() == false) {
            this.name = array.remove(0);
        }
        return array;
    }
    public ArrayList<String> getArrayFromNameAndSetValueAsFirst (char begin, char end)
    {
        ArrayList<String> array = new ArrayList<>();
        array.addAll(getArrayFromName(begin, end));
        if (array.isEmpty() == false) {
            this.value = array.remove(0);
        } else {
            this.value = "[value]";
        }
        return array;
    }


    public boolean isNameContainsKey (DefaultParseKeys key)
    {
        return this.name.contains(key.get());
    }
    public boolean isValueContainsKey (DefaultParseKeys key)
    {
        return this.value.contains(key.get());
    }
    public boolean isNameEqualsTo (String content)
    {
        return this.name.contentEquals(content);
    }
    public boolean isValueEqualsTo (String content)
    {
        return this.value.contentEquals(content);
    }

    public int getNameLength ()
    {
        return this.name.length();
    }
    public int getValueLength ()
    {
        return this.value.length();
    }
    public ArrayList<String> getArgs()
    {
        return args;
    }

    public ArrayList<Word> getBounds()
    {
        return bounds;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public void setArgs(ArrayList<String> args)
    {
        int length = args.size();
        for (int i = 0; i < length; i++) {
            this.args.add(i, new String(args.get(i)));
        }
    }

    public void setBounds(ArrayList<Word> bounds)
    {
        this.bounds = bounds;
    }

    public void setName(String name)
    {
        this.name = new String(name);
    }

    public void setValue(String value)
    {
        this.value = new String(value);
    }

    @Override
    public String toString()
    {
        return new String("[" + this.name + "]->[" + this.value + "]");
    }
}
