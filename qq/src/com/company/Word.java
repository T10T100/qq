package com.company;

/**
 * Created by Operator on 04.11.2015.
 */
public class Word {
    private String name;
    private String word;

    public Word (String name, String word)
    {
        this.name = name;
        this.word = word;
    }

    public Word (String word)
    {
        this.name = new String("[name]");
        this.word = word;
    }

    public Word ()
    {
        this.name = new String("[name]");
        this.word = new String("[word]");
    }



    public boolean containsName (DefaultParseKeys key)
    {
        return this.name.contains(key.get());
    }

    public  boolean containsWord (DefaultParseKeys key)
    {
        return this.word.contains(key.get());
    }

    public boolean equalsName (String name)
    {
        return this.name.contentEquals(name);
    }

    public boolean equalsWord (String name)
    {
        return this.word.contentEquals(name);
    }


}
