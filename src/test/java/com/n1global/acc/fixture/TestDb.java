package com.n1global.acc.fixture;

import com.n1global.acc.CouchDb;
import com.n1global.acc.CouchDbConfig;
import com.n1global.acc.CouchDbDirectUpdater;
import com.n1global.acc.CouchDbFilter;
import com.n1global.acc.CouchDbValidator;
import com.n1global.acc.annotation.Filter;
import com.n1global.acc.annotation.JsView;
import com.n1global.acc.annotation.Security;
import com.n1global.acc.annotation.SecurityPattern;
import com.n1global.acc.annotation.UpdateHandler;
import com.n1global.acc.annotation.ValidateDocUpdate;
import com.n1global.acc.view.CouchDbMapView;
import com.n1global.acc.view.CouchDbReduceView;

@Security(admins = @SecurityPattern(names = "admin"))
public class TestDb extends CouchDb {
    @JsView(map = "emit(doc._id, doc.name)")
    private CouchDbMapView<String, String> testView;

    @JsView(map = "emit(doc._id, 1)", reduce = "return sum(values)")
    private CouchDbReduceView<String, Integer> reducedTestView;

    @Filter(predicate = "return (doc._id.indexOf(\"design\") == -1);")
    private CouchDbFilter testFilter;
    
    @ValidateDocUpdate(predicate = "if (newDoc.name === 'bomb') throw({forbidden: 'Only admins may plant bombs.'}); ")
    private CouchDbValidator validator;

    @UpdateHandler(func = "return [{'_id' : req.id, '@class' : 'com.n1global.acc.json.CouchDbDocument'}, 'New doc inserted!']")
    private CouchDbDirectUpdater testUpdater;

    public TestDb(CouchDbConfig config) {
        super(config);
    }

    public CouchDbMapView<String, String> getTestView() {
        return testView;
    }

    public CouchDbReduceView<String, Integer> getReducedTestView() {
        return reducedTestView;
    }

    public CouchDbFilter getTestFilter() {
        return testFilter;
    }

    public CouchDbDirectUpdater getTestUpdater() {
        return testUpdater;
    }
}
