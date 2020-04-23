package com.equiron.acc.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CouchDbInfo {
    @JsonProperty("db_name")
    private String dbName;

    @JsonProperty("doc_count")
    private long docCount;

    @JsonProperty("doc_del_count")
    private long docDelCount;
    
    @JsonProperty("update_seq")
    private String updateSeq;

    private Size sizes;
    
    private CouchDbClusterInfo cluster;

    @JsonProperty("instance_start_time")
    private long instanceStartTime;

    /**
     * @return name of the database.
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @return number of documents (including design documents) in the database.
     */
    public long getDocCount() {
        return docCount;
    }

    /**
     * When you delete a document the database will create a new revision which contains the _id and _rev fields
     * as well as a deleted flag. This revision will remain even after a database compaction so that the deletion
     * can be replicated. Deleted documents, like non-deleted documents, can affect view build times, PUT and
     * DELETE requests time and size of database on disk, since they increase the size of the B+Tree's. You can
     * see the number of deleted documents in this field. If your use case creates lots of deleted documents
     * (for example, if you are storing short-term data like logfile entries, message queues, etc), you might want to
     * periodically switch to a new database and delete the old one (once the entries in it have all expired).
     */
    public long getDocDelCount() {
        return docDelCount;
    }
    
    /**
     * @return opaque string that describes the state of the database. Do not rely on this string for counting the number of updates.
     */
    public String getUpdateSeq() {
        return updateSeq;
    }

    public Size getSizes() {
        return sizes;
    }
    
    public CouchDbClusterInfo getCluster() {
        return cluster;
    }

    /**
     * @return timestamp of CouchDBs start time (int in ms).
     */
    public long getInstanceStartTime() {
        return instanceStartTime;
    }

    @Override
    public String toString() {
        return "CouchDbInfo [dbName=" + dbName + ", docCount=" + docCount + ", docDelCount=" + docDelCount
                + ", diskSize=" + sizes.getFile() + ", dataSize=" + sizes.getActive() + ", instanceStartTime=" + instanceStartTime
                + "]";
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Size {
        private long active;

        private long file;
        
        private long external;

        /**
         * @return size of live data inside the database, in bytes.
         */
        public long getActive() {
            return active;
        }

        /**
         * @return size of the database file on disk in bytes. Views indexes are not included in the calculation.
         */
        public long getFile() {
            return file;
        }

        /**
         * @return uncompressed size of database contents in bytes.
         */
        public long getExternal() {
            return external;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CouchDbClusterInfo {
        private int q;
        
        private int n;
        
        private int w;
        
        private int r;

        /**
         * @return number of database shards to maintain.
         */
        public int getQ() {
            return q;
        }

        /**
         * @return number of copies of each document to distribute.
         */
        public int getN() {
            return n;
        }

        /**
         * @return size of a write quorum.
         */
        public int getW() {
            return w;
        }

        /**
         * @return size of a read quorum.
         */
        public int getR() {
            return r;
        }
    }
}
