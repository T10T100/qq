package com.company;

/**
 * Created by Operator on 03.11.2015.
 */
public enum DefaultParseKeys {
    CLEAR ("-c"),
    ANY("-a"),
    SKIP_TEMPLATES("-skip"),
    WNO_DIGITS("-d");

    private String key = "";
    DefaultParseKeys (String key)
    {
        this.key = key;
    }

    public String get ()
    {
        return this.key;
    }
}
