package com.company;

/**
 * Created by Operator on 04.11.2015.
 */
public class Word {
    private String name;
    private String template;

    public Word (String name, String template)
    {
        this.name = new String(name);
        this.template = new String(template);
    }

    public Word (String template)
    {
        this.name = new String("[name]");
        this.template = new String(template);
    }

    public Word ()
    {
        this.name = new String("[name]");
        this.template = new String("[word]");
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


}
