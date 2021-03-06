package com.n1global.acc.query;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1global.acc.util.UrlBuilder;

public class CouchDbQueryObject<K> {
    private ObjectMapper mapper;

    private K[] keys;

    private K key;

    private boolean descending;

    private boolean isSetKey;

    private boolean isSetStartKey;

    private boolean isSetEndKey;

    private K endKey;

    private String endKeyDocId;

    private K startKey;

    private String startKeyDocId;

    private boolean group;

    private int groupLevel;

    private boolean inclusiveEnd = true;

    private int limit;

    private String stale;

    private int skip;

    private boolean reduce;

    private boolean includeDocs;

    public CouchDbQueryObject(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String toQuery() throws IOException {
        UrlBuilder urlBuilder = new UrlBuilder("");

        if (isSetKey) urlBuilder.addQueryParam("key", mapper.writeValueAsString(key));

        if (isSetEndKey) urlBuilder.addQueryParam("endkey", mapper.writeValueAsString(endKey));

        if (endKeyDocId != null) urlBuilder.addQueryParam("endkey_docid", endKeyDocId);

        if (isSetStartKey) urlBuilder.addQueryParam("startkey", mapper.writeValueAsString(startKey));

        if (startKeyDocId != null) urlBuilder.addQueryParam("startkey_docid", startKeyDocId + "");

        if (descending) urlBuilder.addQueryParam("descending", "true");

        if (group) urlBuilder.addQueryParam("group", "true");

        if (groupLevel != 0) urlBuilder.addQueryParam("group_level", groupLevel + "");

        if (!inclusiveEnd) urlBuilder.addQueryParam("inclusive_end", "false");

        if (limit != 0) urlBuilder.addQueryParam("limit", limit + "");

        if (stale != null) urlBuilder.addQueryParam("stale", stale +"");

        if (skip != 0) urlBuilder.addQueryParam("skip", skip + "");

        if (!reduce) urlBuilder.addQueryParam("reduce", "false");

        if (includeDocs) urlBuilder.addQueryParam("include_docs", "true");

        return urlBuilder.toString();
    }

    public String jsonKeys() throws JsonGenerationException, JsonMappingException, IOException {
        return mapper.writeValueAsString(new Object() {
            @SuppressWarnings({ "synthetic-access", "hiding" })
            @JsonProperty("keys")
            K[] keys = CouchDbQueryObject.this.keys;
        });
    }

    public void setSetKey(boolean isSetKey) {
        this.isSetKey = isSetKey;
    }

    public void setSetStartKey(boolean isSetStartKey) {
        this.isSetStartKey = isSetStartKey;
    }

    public void setSetEndKey(boolean isSetEndKey) {
        this.isSetEndKey = isSetEndKey;
    }

    public K[] getKeys() {
        return keys;
    }

    public void setKeys(K[] keys) {
        this.keys = keys;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public void setEndKey(K endKey) {
        this.endKey = endKey;
    }

    public void setEndKeyDocId(String endKeyDocId) {
        this.endKeyDocId = endKeyDocId;
    }

    public void setStartKey(K startKey) {
        this.startKey = startKey;
    }

    public void setStartKeyDocId(String startKeyDocId) {
        this.startKeyDocId = startKeyDocId;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public void setGroupLevel(int groupLevel) {
        this.groupLevel = groupLevel;
    }

    public void setInclusiveEnd(boolean inclusiveEnd) {
        this.inclusiveEnd = inclusiveEnd;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setStale(String stale) {
        this.stale = stale;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public void setReduce(boolean reduce) {
        this.reduce = reduce;
    }

    public void setIncludeDocs(boolean includeDocs) {
        this.includeDocs = includeDocs;
    }
}
