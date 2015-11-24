package com.company;

import java.util.ArrayList;


/**
 * Created by Afony on 04.11.2015.
 */
public class Word {
    ArrayList<Word> bounds;
    ArrayList<String> args;
    String name;
    String value;

    public Word (String string, char splitter)
    {
        this.setValue(string);
        this.splitValueAndSet(splitter);
        this.bounds = new ArrayList<>();
        this.args = new ArrayList<>();
    }
    public Word (Word word)
    {
        this.setName(word.name);
        this.setValue(word.value);
        this.bounds = new ArrayList<>();
        this.args = new ArrayList<>();
    }
    public Word (ArrayList<Word> words)
    {
        this.bounds = new ArrayList<>();
        this.args = new ArrayList<>();
        if (words.isEmpty() == true) {
            this.setName("[name]");
            this.setValue("[value]");
            return;
        } else {
            this.bounds.addAll(words);
            this.setName(words.get(0).name);
            this.setValue(words.get(0).value);
            words.remove(0);
        }
    }
    public Word (String name, String value)
    {
        this.setName(name);
        this.setValue(value);
        this.bounds = new ArrayList<>();
        this.args = new ArrayList<>();
    }
    public Word (String value)
    {
        this.setValue(value);
        this.setName("[name]");
        this.bounds = new ArrayList<>();
        this.args = new ArrayList<>();
    }
    public Word ()
    {
        this.setName("[name]");
        this.setValue("[value]");
        this.bounds = new ArrayList<>();
        this.args = new ArrayList<>();
    }

    private String[] split (char[] parsing, char splitter)
    {
        String[] outputCollection = new String[2];
        String bag = "";
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
        String bag = "";
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
        String bag = "";
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
    private String[] getFromOnce (char[] parsing, char begin, char end)
    {
        String[] output = {"", ""};
        char separator = begin;
        int bufferIndex = 1;
        boolean copyAble = true;
        for (int i = 0; i < parsing.length; i++) {
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
        }
        return output;
    }
    public String[] getFromNameOnce (char begin, char end)
    {
        return this.getFromOnce(this.name.toCharArray(), begin, end);
    }
    public String[] getFromValueOnce (char begin, char end)
    {
        return this.getFromOnce(this.value.toCharArray(), begin, end);
    }
    public String getFromNameOnceAndSetName (char begin, char end)
    {
        this.name = this.getFromOnce(this.name.toCharArray(), begin, end)[0];
        return this.name;
    }
    public String getFromValueOnceAndSetValue (char begin, char end)
    {
        if (this.value.isEmpty() == true) {
            this.value += " ";
        }
        this.value = this.getFromOnce(this.name.toCharArray(), begin, end)[0];
        return this.value;
    }

    private ArrayList<String> getArray (char[] parsing, char begin, char end)
    {
        ArrayList<String> outputCollection = new ArrayList<>();
        String[] bag = {"", ""};
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
                    outputCollection.add(bag[0]);
                }
            } else {
                bag[copyIndex] += parsing[i];
            }
        }
        outputCollection.add(0, bag[1]);
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
        } else {
            name = "[name]";
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

    private ArrayList<String> getArray (char[] parsing, char delimiter)
    {
        ArrayList<String> outputCollection = new ArrayList<>();
        String bag = "";
        for (int i = 0; i < parsing.length; i++) {
            if (parsing[i] == delimiter) {
                outputCollection.add(bag);
                bag = "";
            } else {
                bag += parsing[i];
            }
        }
        outputCollection.add(bag);
        return outputCollection;
    }
    public ArrayList<String> getArrayFromName (char delimiter)
    {
        return this.getArray(this.name.toCharArray(), delimiter);
    }
    public ArrayList<String> getArrayFromNameAndSetNameAsFirst (char delimiter)
    {
        ArrayList<String> array = this.getArrayFromName(delimiter);
        if (array.isEmpty() == false) {
            this.name = array.remove(0);
        }
        return array;
    }
    public ArrayList<String> getArrayFromNameAndSetValueAsFirst (char delimiter)
    {
        ArrayList<String> array = new ArrayList<>();
        array.addAll(getArrayFromName(delimiter));
        if (array.isEmpty() == false) {
            this.value = array.remove(0);
        } else {
            this.value = "[value]";
        }
        return array;
    }
    public ArrayList<String> getArrayFromValue (char delimiter)
    {
        return this.getArray(this.value.toCharArray(), delimiter);
    }
    public ArrayList<String> getArrayFromValueAndSetNameAsFirst (char delimiter)
    {
        ArrayList<String> array = this.getArrayFromValue(delimiter);
        if (array.isEmpty() == false) {
            this.name = array.remove(0);
        } else {
            this.name = "[name]";
        }
        return array;
    }
    public ArrayList<String> getArrayFromValueAndSetValueAsFirst (char delimiter)
    {
        ArrayList<String> array = new ArrayList<>();
        array.addAll(getArrayFromValue(delimiter));
        if (array.isEmpty() == false) {
            this.value = array.remove(0);
        } else {
            this.value = "[value]";
        }
        return array;
    }

    private String insertWordValueBetween (char[] parsing, ArrayList<Word> words, char begin, char end)
    {
        String output = "";
        String name = "";
        boolean gatherName = false;
        for (int watchIndex = 0; watchIndex < parsing.length; watchIndex++) {
            if (parsing[watchIndex] == begin) {
                watchIndex++;
                if (watchIndex >= parsing.length) {
                    break;
                }
                name = "";
                gatherName = false;
                for (; watchIndex < parsing.length; watchIndex++) {
                    if (parsing[watchIndex] == end) {
                        if (gatherName == false) {
                            for (Word word : words) {
                                output += word.value;
                            }
                        }
                        watchIndex++;
                        break;
                    }
                    name += parsing[watchIndex];
                    gatherName = true;
                }
                if (watchIndex >= parsing.length) {
                    break;
                }
                for (Word word : words) {
                    if (name.contentEquals(word.name) == true) {
                        output += word.value;
                        break;
                    }
                }

            }
            output += parsing[watchIndex];
        }
        return output;
    }
    public String insertWordValueToNameBetween (ArrayList<Word> words, char begin, char end)
    {
        this.name = this.insertWordValueBetween(this.name.toCharArray(), words, begin, end);
        return this.name;
    }
    public String insertWordValueToValueBetween (ArrayList<Word> words, char begin, char end)
    {
        this.value = this.insertWordValueBetween(this.name.toCharArray(), words, begin, end);
        return this.value;
    }
    private ArrayList<BoundedNumber> getIntegersFrom (char[] input)
    {
        String parsed = "";
        ArrayList<BoundedNumber> output = new ArrayList<>();
        int startIndex = 0;
        for (int i = 0; i < input.length; i++) {
            if (Character.isDigit(input[i]) == true) {
                startIndex = i;
                parsed += input[i];
                while (Character.isDigit(input[++i]) == true) {
                    parsed += input[i];
                }
                output.add(new BoundedNumber(Long.parseLong(parsed), startIndex, i));
                parsed = "";
            } else {

            }
        }
        return output;
    }
    public ArrayList<BoundedNumber> getIntegersFromName ()
    {
        return this.getIntegersFrom(this.name.toCharArray());
    }

    public ArrayList<BoundedNumber> getIntegersFromValue ()
    {
        return this.getIntegersFrom(this.value.toCharArray());
    }

    public void removeSpacesFromName ()
    {
        char[] array = this.name.toCharArray();
        String name = "";
        for (int i = 0; i < array.length; i++) {
            if (Character.isSpaceChar(array[i]) == false && array[i] != '\n') {
                name += array[i];
            }
        }
        this.name = name;
    }
    public void removeSpacesFromValue ()
    {
        char[] array = this.value.toCharArray();
        String name = "";
        for (int i = 0; i < array.length; i++) {
            if (Character.isSpaceChar(array[i]) == false) {
                name += array[i];
            }
        }
        this.value = name;
    }
    public void removeSpacesFromWord ()
    {
        this.removeSpacesFromName();
        this.removeSpacesFromValue();
    }

    public String removeAllButDigits (char[] input)
    {
        String output = "";
        for (int i = 0; i <  input.length; i++) {
            if (Character.isDigit(input[i]) == true) {
                output += input[i];
            }
        }
        return output;
    }
    public void removeAllButDigitsFromName ()
    {
        this.name = this.removeAllButDigits(this.name.toCharArray());
    }
    public void removeAllButDigitsFromValue ()
    {
        this.value = this.removeAllButDigits(this.value.toCharArray());
    }
    public void removeAllButDigitsFromWord ()
    {
        this.name = this.removeAllButDigits(this.name.toCharArray());
        this.value = this.removeAllButDigits(this.value.toCharArray());
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
    public boolean isNameContains (String content)
    {
        return this.name.contains(content);
    }
    public boolean isValueContains (String content)
    {
        return this.value.contains(content);
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
        if (name == null) {
            return "null";
        }
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

    public void addArg (String arg)
    {
        this.args.add(arg);
    }

    public void removeAllArgs ()
    {
        this.args.removeAll(this.args);
    }

    public String getArg (int index)
    {
        return this.args.get(index);
    }

    public void setBounds(ArrayList<Word> bounds)
    {
        this.bounds = bounds;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        String args = "";
        if (this.args != null) {
            for (String arg : this.args) {
                args += arg + ", ";
            }
        }
        return  this.name + " = " + this.value + " : " + args;
    }

    public class BoundedNumber {
        private final long number;
        private final int startIndex;
        private final int endIndex;
        public BoundedNumber (long number, int startIndex, int endIndex)
        {
            this.number = number;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public int getEndIndex()
        {
            return endIndex;
        }

        public int getStartIndex()
        {
            return startIndex;
        }

        public long getNumber()
        {
            return number;
        }
    }




}
