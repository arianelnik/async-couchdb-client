package com.n1global.acc.examples.directupdater;

import com.n1global.acc.json.CouchDbDocument;

public class User extends CouchDbDocument {
    private String name;

    public User() {
        /* empty */
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
