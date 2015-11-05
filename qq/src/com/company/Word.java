package com.company;

import com.sun.xml.internal.fastinfoset.util.CharArray;

import java.util.ArrayList;

/**
 * Created by Operator on 04.11.2015.
 */
public class Word {
    private String name;
    private String template;


    public Word (Word word)
    {
        this.name = word.name;
        this.template = word.template;
    }
    public Word (ArrayList<Word> words)
    {
        if (words.isEmpty() == false) {
            this.template = words.remove(0).template;
        } else {
            this.name = new String(" ");
            this.template = new String(" ");
        }
        if (words.isEmpty() == false) {
            this.name = words.remove(0).template;
        } else {
            this.name = new String(" ");
        }
    }
    public Word (String name, String template)
    {
        this.name = name;
        this.template = template;
    }
    public Word (String... args)
    {
        this.name = args[0];
        this.template = args[1];
    }

    public Word (String template)
    {
        this.name = new String(" ");
        this.template = template;
    }

    public Word ()
    {
        this.name = new String(" ");
        this.template = new String(" ");
    }






    public char[] toCharArrayOfName ()
    {
        return this.name.toCharArray();
    }
    public char[] toCharArrayOfTemplate ()
    {
        return this.template.toCharArray();
    }

    public void addToName (String add)
    {
        this.name += add;
    }
    public void addToTemplate (String add)
    {
        this.template += add;
    }
    public void addToName (char add)
    {
        this.name += add;
    }
    public void addToTemplate (char add)
    {
        this.template += add;
    }

    public void setName (String name)
    {
        this.name = new String(name);
    }
    public  void setTemplate (String template)
    {
        this.template = new String(template);
    }
    public void set (String name, String template)
    {
        this.name = new String(name);
        this.template = new String(template);
    }


    public String getName()
    {
        return name;
    }
    public String getTemplate ()
    {
        return this.template;
    }
    public String[] get ()
    {
        String[] output = {this.name, this.template};
        return output;
    }

    public int getNameLength ()
    {
        return this.name.length();
    }

    public int getTemplateLength ()
    {
        return this.template.length();
    }

    public boolean containsKeysInName (DefaultParseKeys... keys)
    {
        for (DefaultParseKeys key : keys) {
            if (this.name.contains(key.get()) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean containsKeyInName (DefaultParseKeys key)
    {
        return this.name.contains(key.get());
    }

    public  boolean containsKeyInTemplate (DefaultParseKeys key)
    {
        return this.template.contains(key.get());
    }

    public boolean containsKeysInTemplate (DefaultParseKeys... keys)
    {
        for (DefaultParseKeys key : keys) {
            if (this.template.contains(key.get()) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean equalsNameTo (String name)
    {
        return this.name.contentEquals(name);
    }

    public boolean equalsTemplateTo (String name)
    {
        return this.template.contentEquals(name);
    }


    @Override
    public String toString()
    {
        return "[" + this.name + "->" + this.template + "];";
    }

}
