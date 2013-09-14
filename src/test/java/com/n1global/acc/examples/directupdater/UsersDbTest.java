package com.n1global.acc.examples.directupdater;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.FluentIterable;
import com.n1global.acc.CouchDbConfig;
import com.ning.http.client.AsyncHttpClient;

public class UsersDbTest {
    private UsersDb db;

    private AsyncHttpClient httpClient = new AsyncHttpClient();

    @Before
    public void before() {
        db = new UsersDb(new CouchDbConfig.Builder().setUser("root")
                                                    .setPassword("root")
                                                    .setHttpClient(httpClient)
                                                    .build());
    }

    @After
    public void after() {
        db.deleteDb();

        httpClient.close();
    }

    @Test
    public void shouldUpdateAllDocs() {
        User user1 = new User("John Smith");
        User user2 = new User("Adam Fox");
        User user3 = new User("Amanda Black");

        db.bulk(user1, user2, user3);

        for (User user : FluentIterable.from(db.getBuiltInView().createDocsQuery().asDocsIterator()).filter(User.class)) {
            db.getTestUpdater().update(user.getDocId());
        }

        Assert.assertEquals("John",   db.<User>get(user1.getDocId()).getName());
        Assert.assertEquals("Adam",   db.<User>get(user2.getDocId()).getName());
        Assert.assertEquals("Amanda", db.<User>get(user3.getDocId()).getName());
    }
}
