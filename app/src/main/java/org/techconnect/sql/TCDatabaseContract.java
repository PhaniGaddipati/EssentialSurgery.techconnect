package org.techconnect.sql;

import android.provider.BaseColumns;

public class TCDatabaseContract {

    private TCDatabaseContract() {

    }

    /**
     * User table definitions.
     */
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String ID = "_id";
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String COUNTRY_CODE = "country_code";
        public static final String COUNTRY = "country";
        public static final String ORGANIZATION = "organization";
        public static final String PIC = "pic";
        public static final String EXPERTISES = "expertises";
        public static final String UPCHARTS = "upCharts";
        public static final String DOWNCHARTS = "downCharts";
        // Create the users table
        public static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
                EMAIL + " TEXT," +
                NAME + " TEXT," +
                COUNTRY_CODE + " DATE," +
                COUNTRY + " TEXT," +
                ORGANIZATION + " TEXT," +
                PIC + " TEXT," +
                EXPERTISES + " TEXT," +
                UPCHARTS + " TEXT," +
                DOWNCHARTS + " TEXT);";
        public static final String UPGRADE_V1_V2_ADD_UPCHARTS = "ALTER TABLE "
                + TABLE_NAME + " ADD " + UPCHARTS + " TEXT DEFAULT ''";
        public static final String UPGRADE_V1_V2_ADD_DOWNCHARTS = "ALTER TABLE "
                + TABLE_NAME + " ADD " + DOWNCHARTS + " TEXT DEFAULT ''";
    }

    /**
     * Chart table definitions.
     */
    public static class ChartEntry implements BaseColumns {
        public static final String TABLE_NAME = "charts";
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String UPDATED_DATE = "updateDate";
        public static final String VERSION = "version";
        public static final String OWNER = "owner";
        public static final String GRAPH_ID = "graphId";
        public static final String ALL_RESOURCES = "all_res";
        public static final String IMAGE = "image";
        public static final String RESOURCES = "resources";
        public static final String TYPE = "type";
        public static final String SCORE = "score";
        public static final String UPVOTES = "upvotes";
        public static final String DOWNVOTES = "downvotes";

        //Create the Chart Table object
        public static final String CREATE_CHART_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
                NAME + " TEXT," +
                DESCRIPTION + " TEXT," +
                UPDATED_DATE + " DATE," +
                VERSION + " TEXT," +
                OWNER + " TEXT," +
                GRAPH_ID + " TEXT," +
                ALL_RESOURCES + " TEXT," +
                IMAGE + " TEXT," +
                RESOURCES + " TEXT," +
                TYPE + " TEXT," +
                SCORE + " INTEGER," +
                UPVOTES + " INTEGER," +
                DOWNVOTES + " INTEGER," +
                " FOREIGN KEY (graphId) REFERENCES " + GraphEntry.TABLE_NAME + " (_id)) WITHOUT ROWID;";

        public static final String CREATE_CHART_TABLE_KITKAT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
                NAME + " TEXT," +
                DESCRIPTION + " TEXT," +
                UPDATED_DATE + " DATE," +
                VERSION + " TEXT," +
                OWNER + " TEXT," +
                GRAPH_ID + " TEXT," +
                ALL_RESOURCES + " TEXT," +
                IMAGE + " TEXT," +
                RESOURCES + " TEXT," +
                TYPE + " TEXT," +
                SCORE + " INTEGER," +
                UPVOTES + " INTEGER," +
                DOWNVOTES + " INTEGER," +
                " FOREIGN KEY (graphId) REFERENCES " + GraphEntry.TABLE_NAME + " (_id));";

        public static final String UPGRADE_V1_V2_ADD_UPVOTES = "ALTER TABLE "
                + TABLE_NAME + " ADD " + UPVOTES + " INTEGER DEFAULT 0";
        public static final String UPGRADE_V1_V2_ADD_DOWNVOTES = "ALTER TABLE "
                + TABLE_NAME + " ADD " + DOWNVOTES + " INTEGER DEFAULT 0";

        private ChartEntry() {

        }

    }

    /**
     * Comment table definitions.
     */
    public static class CommentEntry implements BaseColumns {
        public static final String TABLE_NAME = "comments";
        public static final String ID = "_id";
        public static final String PARENT_ID = "parentId";
        public static final String PARENT_TYPE = "parentType";
        public static final String OWNER = "owner";
        public static final String OWNER_NAME = "ownerName";
        public static final String TEXT = "text";
        public static final String CREATED_DATE = "createdDate";
        public static final String ATTACHMENT = "attachment";
        //Create the Comment Table
        public static final String CREATE_COMMENT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
                PARENT_ID + " TEXT," +
                PARENT_TYPE + " TEXT," +
                OWNER + " TEXT," +
                OWNER_NAME + " TEXT," +
                TEXT + " TEXT," +
                CREATED_DATE + " DATE," +
                ATTACHMENT + " TEXT);";

        private CommentEntry() {
        }

    }

    /**
     * Graph table definitions.
     */
    public static class GraphEntry implements BaseColumns {
        public static final String TABLE_NAME = "graphs";
        public static final String ID = "_id";
        public static final String FIRST_VERTEX = "firstVertex";
        //Create the Table
        public static final String CREATE_GRAPH_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " TEXT PRIMARY KEY NOT NULL UNIQUE, " +
                FIRST_VERTEX + " TEXT, FOREIGN KEY (firstVertex) REFERENCES " + VertexEntry.TABLE_NAME +
                " (_id) DEFERRABLE INITIALLY DEFERRED);";

        private GraphEntry() {
        }

    }

    /**
     * Edge table definitions.
     */
    public static class EdgeEntry implements BaseColumns {
        public static final String TABLE_NAME = "edges";
        public static final String ID = "_id";
        public static final String GRAPH_ID = "graphId";
        public static final String LABEL = "_label";
        public static final String OUT_VERTEX = "_outV";
        public static final String IN_VERTEX = "_inV";
        public static final String DETAILS = "details";
        //Create the edges table
        public static final String CREATE_EDGE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
                GRAPH_ID + " TEXT," +
                LABEL + " TEXT," +
                OUT_VERTEX + " TEXT," +
                IN_VERTEX + " TEXT," +
                DETAILS + " TEXT, FOREIGN KEY (graphId) REFERENCES graphs (_id)," +
                " FOREIGN KEY (_outV) REFERENCES " + VertexEntry.TABLE_NAME + " (_id), FOREIGN KEY (_inV) REFERENCES " +
                VertexEntry.TABLE_NAME + " (_id));";

        private EdgeEntry() {
        }

    }

    /**
     * Vertex table definitions.
     */
    public static class VertexEntry implements BaseColumns {
        public static final String TABLE_NAME = "vertices";
        public static final String ID = "_id";
        public static final String GRAPH_ID = "graphId";
        public static final String NAME = "name";
        public static final String DETAILS = "details";
        public static final String RESOURCES = "resources";
        public static final String IMAGES = "images";
        //Creates the Vertices Table Object
        public static final String CREATE_VERTEX_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
                GRAPH_ID + " TEXT," +
                NAME + " TEXT," +
                DETAILS + " TEXT," +
                RESOURCES + " TEXT," +
                IMAGES + " TEXT, FOREIGN KEY (graphId) REFERENCES " + GraphEntry.TABLE_NAME + " (_id));";

        private VertexEntry() {
        }

    }

    public static class SessionEntry implements BaseColumns {
        public static final String TABLE_NAME = "sessions";
        public static final String ID = "_id";
        public static final String CREATED_DATE = "createdDate";
        public static final String FINISHED = "finished";
        public static final String FINISHED_DATE = "finishDate";
        public static final String MANUFACTURER = "manufacturer";
        public static final String DEPARTMENT = "department";
        public static final String MODEL = "modelNumber";
        public static final String SERIAL = "serialNumber";
        public static final String NOTES = "notes";
        public static final String HISTORY = "history";
        public static final String OPTION_HISTORY = "optionHistory";
        public static final String FLOWCHART_ID = "flowchart_id";


        public static final String CREATE_SESSION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (\n" +
                ID + " TEXT PRIMARY KEY NOT NULL UNIQUE,\n" +
                CREATED_DATE + " DATE,\n" +
                FINISHED + " BOOLEAN,\n" +
                FINISHED_DATE + " DATE,\n" +
                MANUFACTURER + " TEXT,\n" +
                DEPARTMENT + " TEXT,\n" +
                MODEL + " TEXT,\n" +
                SERIAL + " TEXT,\n" +
                NOTES + " TEXT,\n" +
                HISTORY + " TEXT,\n" +
                OPTION_HISTORY + " TEXT,\n" +
                FLOWCHART_ID + " TEXT" +
                ");";

        public static final String UPGRADE_V1_V2_ADD_FINISHED_DATE = "ALTER TABLE "
                + TABLE_NAME + " ADD " + FINISHED_DATE + " DATE DEFAULT 0";
        public static final String UPGRADE_V1_V2_ADD_MANUFACTURER = "ALTER TABLE "
                + TABLE_NAME + " ADD " + MANUFACTURER + " TEXT DEFAULT ''";

        private SessionEntry() {
        }
    }
}
