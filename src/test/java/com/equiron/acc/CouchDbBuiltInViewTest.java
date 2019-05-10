package com.equiron.acc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.equiron.acc.json.CouchDbDocument;

public class CouchDbBuiltInViewTest extends CouchDbAbstractTest {
    @Test
    public void shouldIterateDocs() {
        for (int i = 0; i < 11; i++) {
            db.saveOrUpdate(new CouchDbDocument(i + ""));
        }

        int docsIterated = 0;

        long docsCount = db.getInfo().getDocCount();

        for (CouchDbDocument d : db.getBuiltInView().createDocQuery().asDocIterator(5)) {
            Assertions.assertFalse(d.getDocId().isEmpty());

            docsIterated++;
        }

        Assertions.assertEquals(docsCount, docsIterated);
    }
}
