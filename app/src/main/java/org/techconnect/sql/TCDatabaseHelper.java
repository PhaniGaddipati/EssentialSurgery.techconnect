package org.techconnect.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;

import org.techconnect.model.Comment;
import org.techconnect.model.Edge;
import org.techconnect.model.FlowChart;
import org.techconnect.model.Graph;
import org.techconnect.model.User;
import org.techconnect.model.Vertex;
import org.techconnect.model.session.Session;
import org.techconnect.sql.TCDatabaseContract.ChartEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Using a singleton class in order to make the same database visible/accessible to any activity
 * during the operation of the app
 * Created by doranwalsten on 10/13/16.
 */
public class TCDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FlowChart.db";
    private static TCDatabaseHelper instance = null;
    private Context context;

    private TCDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static TCDatabaseHelper get(Context context) {
        if (instance == null) {
            instance = new TCDatabaseHelper(context);
        }
        return instance;
    }

    /**
     * Generates a random ID 17 chars long.
     *
     * @return A random ID
     */
    private static String getRandomId() {
        String validChars = "23456789ABCDEFGHJKLMNPQRSTWXYZabcdefghijkmnopqrstuvwxyz";
        char chars[] = new char[17];
        for (int i = 0; i < chars.length; i++) {
            int rand = (int) (Math.random() * chars.length);
            chars[i] = validChars.charAt(rand);
        }
        return new String(chars);
    }

    @Override
    public void onCreate(SQLiteDatabase sql) {
        sql.execSQL(TCDatabaseContract.UserEntry.CREATE_USER_TABLE);
        sql.execSQL(TCDatabaseContract.ChartEntry.CREATE_CHART_TABLE);
        sql.execSQL(TCDatabaseContract.GraphEntry.CREATE_GRAPH_TABLE);
        sql.execSQL(TCDatabaseContract.VertexEntry.CREATE_VERTEX_TABLE);
        sql.execSQL(TCDatabaseContract.EdgeEntry.CREATE_EDGE_TABLE);
        sql.execSQL(TCDatabaseContract.CommentEntry.CREATE_COMMENT_TABLE);
        sql.execSQL(TCDatabaseContract.SessionEntry.CREATE_SESSION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int fromV, int toV) {
    }

    public void upsertCharts(FlowChart charts[]) {
        for (FlowChart f : charts) {
            upsertChart(f);
        }
    }

    public void insertCharts(FlowChart charts[]) {
        for (FlowChart f : charts) {
            insertChart(f);
        }
    }

    public void upsertUser(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TCDatabaseContract.UserEntry.ID, user.get_id());
        contentValues.put(TCDatabaseContract.UserEntry.COUNTRY, user.getCountry());
        contentValues.put(TCDatabaseContract.UserEntry.COUNTRY_CODE, user.getCountryCode());
        contentValues.put(TCDatabaseContract.UserEntry.EMAIL, user.getEmail());
        contentValues.put(TCDatabaseContract.UserEntry.NAME, user.getName());
        contentValues.put(TCDatabaseContract.UserEntry.ORGANIZATION, user.getOrganization());
        contentValues.put(TCDatabaseContract.UserEntry.PIC, user.getPic());
        contentValues.put(TCDatabaseContract.UserEntry.EXPERTISES, TextUtils.join(",", user.getExpertises()));
        getWritableDatabase().insertWithOnConflict(TCDatabaseContract.UserEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public User getUser(String id) {
        String selection = TCDatabaseContract.UserEntry.ID + " = ?";
        String[] selectionArgs = {id};
        Cursor c = getReadableDatabase().query(TCDatabaseContract.UserEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        if (c.getCount() < 1) {
            return null;
        }
        User user = new User();
        user.set_id(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.ID)));
        user.setCountry(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.COUNTRY)));
        user.setCountryCode(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.COUNTRY_CODE)));
        user.setEmail(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.EMAIL)));
        user.setName(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.NAME)));
        user.setOrganization(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.ORGANIZATION)));
        user.setPic(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.PIC)));
        String expertises = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.EXPERTISES));
        if (TextUtils.isEmpty(expertises.trim())) {
            user.setExpertises(new ArrayList<String>(0));
        } else {
            user.setExpertises(Arrays.asList(expertises.split(",")));
        }
        c.close();
        return user;
    }

    public List<User> getAllUsers() {
        Cursor c = getReadableDatabase().query(TCDatabaseContract.UserEntry.TABLE_NAME,
                null, null, null, null, null, null);
        c.moveToFirst();
        if (c.getCount() < 1) {
            return new ArrayList<>();
        }
        List<User> users = new ArrayList<>();
        while (!c.isAfterLast()) {
            User user = new User();
            user.set_id(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.ID)));
            user.setCountry(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.COUNTRY)));
            user.setCountryCode(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.COUNTRY_CODE)));
            user.setEmail(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.EMAIL)));
            user.setName(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.NAME)));
            user.setOrganization(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.ORGANIZATION)));
            user.setPic(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.PIC)));
            String expertises = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.UserEntry.EXPERTISES));
            if (TextUtils.isEmpty(expertises.trim())) {
                user.setExpertises(new ArrayList<String>(0));
            } else {
                user.setExpertises(Arrays.asList(expertises.split(",")));
            }
            users.add(user);
            c.moveToNext();
        }
        c.close();
        return users;
    }

    /**
     * Get a map of chart names, mapping to their id.
     */
    public Map<String, String> getChartNamesAndIDs() {
        Map<String, String> set = new HashMap<>();
        Cursor c = getReadableDatabase().query(ChartEntry.TABLE_NAME, new String[]{ChartEntry.ID, ChartEntry.NAME}, null,
                null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            set.put(c.getString(c.getColumnIndexOrThrow(ChartEntry.NAME)), c.getString(c.getColumnIndexOrThrow(ChartEntry.ID)));
            c.moveToNext();
        }
        c.close();
        return set;
    }

    /**
     * Opposite of previous function.
     *
     * @return Map of Flowchart ID to name
     */
    public Map<String, String> getChartIDsAndNames() {
        Map<String, String> set = new HashMap<>();
        Cursor c = getReadableDatabase().query(ChartEntry.TABLE_NAME, new String[]{ChartEntry.ID, ChartEntry.NAME}, null,
                null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            set.put(c.getString(c.getColumnIndexOrThrow(ChartEntry.ID)), c.getString(c.getColumnIndexOrThrow(ChartEntry.NAME)));
            c.moveToNext();
        }
        c.close();
        return set;
    }

    public int getNumFlowcharts() {
        List<String> ids = new LinkedList<>();
        Cursor c = getReadableDatabase().query(ChartEntry.TABLE_NAME, new String[]{ChartEntry.ID}, null,
                null, null, null, null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public String[] getAllChartIds() {
        List<String> ids = new LinkedList<>();
        Cursor c = getReadableDatabase().query(ChartEntry.TABLE_NAME, new String[]{ChartEntry.ID}, null,
                null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            ids.add(c.getString(c.getColumnIndexOrThrow(ChartEntry.ID)));
            c.moveToNext();
        }
        c.close();
        return ids.toArray(new String[ids.size()]);
    }

    public CursorLoader getAllFlowchartsCursorLoader(final String filter) {
        return new CursorLoader(context, null, null, null, null, null) {
            @Override
            public Cursor loadInBackground() {
                return getAllFlowchartsCursor(filter);
            }
        };
    }

    public Cursor getAllFlowchartsCursor() {
        return getAllFlowchartsCursor(null);
    }

    public Cursor getAllFlowchartsCursor(String filter) {
        if (filter == null || TextUtils.isEmpty(filter.trim())) {
            return getReadableDatabase().query(ChartEntry.TABLE_NAME, null, null,
                    null, null, null, null);
        } else {
            String selection = ChartEntry.NAME + " LIKE ?";
            String selectArgs[] = new String[]{"%" + filter.trim() + "%"};
            return getReadableDatabase().query(ChartEntry.TABLE_NAME, null, selection,
                    selectArgs, null, null, null);
        }
    }


    public boolean hasChart(String id) {
        String selection = ChartEntry.ID + " = ?";
        String selectArgs[] = {id};
        Cursor c = getReadableDatabase().query(ChartEntry.TABLE_NAME, null, selection,
                selectArgs, null, null, null);
        c.moveToFirst();
        return c.getCount() > 0;
    }

    public FlowChart getChart(String id) {
        String selection = ChartEntry.ID + " = ?";
        String selectArgs[] = {id};
        Cursor c = getReadableDatabase().query(ChartEntry.TABLE_NAME, null, selection,
                selectArgs, null, null, null);
        c.moveToFirst();
        if (c.getCount() == 0) {
            return null;
        }
        FlowChart chart = getChartFromCursor(c);
        c.close();
        return chart;
    }


    /**
     * Get a stub chart, for use in listviews when you don't need comments and the graph.
     *
     * @param c
     * @return
     */
    @NonNull
    public FlowChart getChartStubFromCursor(Cursor c) {
        FlowChart chart = new FlowChart();
        chart.setId(c.getString(c.getColumnIndexOrThrow(ChartEntry.ID)));
        chart.setName(c.getString(c.getColumnIndexOrThrow(ChartEntry.NAME)));
        chart.setDescription(c.getString(c.getColumnIndexOrThrow(ChartEntry.DESCRIPTION)));
        chart.setUpdatedDate(c.getString(c.getColumnIndexOrThrow(ChartEntry.UPDATED_DATE)));
        chart.setVersion(c.getString(c.getColumnIndexOrThrow(ChartEntry.VERSION)));
        chart.setOwner(c.getString(c.getColumnIndexOrThrow(ChartEntry.OWNER)));
        chart.setImage(c.getString(c.getColumnIndexOrThrow(ChartEntry.IMAGE)));
        chart.setType(FlowChart.ChartType.valueOf(c.getString(c.getColumnIndexOrThrow(ChartEntry.TYPE))));
        chart.setScore(c.getInt(c.getColumnIndexOrThrow(ChartEntry.SCORE)));
        return chart;
    }

    @NonNull
    public FlowChart getChartFromCursor(Cursor c) {
        FlowChart chart = getChartStubFromCursor(c);
        String allRes = c.getString(c.getColumnIndexOrThrow(ChartEntry.ALL_RESOURCES));
        String res = c.getString(c.getColumnIndexOrThrow(ChartEntry.RESOURCES));
        if (allRes != null && !TextUtils.isEmpty(allRes.trim())) {
            chart.setAllRes(Arrays.asList(allRes.split(",")));
        } else {
            chart.setAllRes(new ArrayList<String>());
        }
        if (res != null && !TextUtils.isEmpty(res.trim())) {
            chart.setResources(Arrays.asList(res.split(",")));
        } else {
            chart.setResources(new ArrayList<String>());
        }

        String graphId = c.getString(c.getColumnIndexOrThrow(ChartEntry.GRAPH_ID));
        chart.setGraph(getGraph(graphId));
        chart.setComments(getComments(chart.getId(), Comment.PARENT_TYPE_CHART));
        return chart;
    }

    public void upsertChart(FlowChart flowChart) {
        if (getChart(flowChart.getId()) == null) {
            insertChart(flowChart);
        } else {
            // Delete old graph
            deleteChartsGraph(flowChart);
            // Add new graph
            String graphId = insertGraph(flowChart.getGraph());
            ContentValues chartContentValues = getChartContentValues(flowChart, graphId);
            try {
                deleteComments(flowChart.getId(), Comment.PARENT_TYPE_CHART);
                insertComments(flowChart.getComments(), flowChart.getId(), Comment.PARENT_TYPE_CHART);
                getWritableDatabase().update(ChartEntry.TABLE_NAME, chartContentValues,
                        ChartEntry.ID + " = ?", new String[]{flowChart.getId()});
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage());
            }

            Log.d(this.getClass().getName(), "Chart Info Updated Successfully");
        }
    }

    private void deleteChartsGraph(FlowChart flowChart) {
        String selection = TCDatabaseContract.GraphEntry.ID + " = ?";
        String selectionArgs[] = {flowChart.getId()};
        Cursor c = getReadableDatabase().query(TCDatabaseContract.GraphEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            String oldGraphId = c.getString(c.getColumnIndexOrThrow(ChartEntry.GRAPH_ID));
            deleteGraph(oldGraphId);
        }
    }

    /**
     * This method takes a FlowChart object and pushes all information into the SQL database
     * <p/>
     *
     * @param flowChart - The Flowchart object
     */
    public void insertChart(FlowChart flowChart) {
        String graphId = insertGraph(flowChart.getGraph());

        //Create ContentValues object with all columns and values for just Chart
        ContentValues chartContentValues = getChartContentValues(flowChart, graphId);

        //Insert chart & comments
        try {
            //Delete old comments and insert new ones
            deleteComments(flowChart.getId(), Comment.PARENT_TYPE_CHART);
            insertComments(flowChart.getComments(), flowChart.getId(), Comment.PARENT_TYPE_CHART);
            getWritableDatabase().insert(ChartEntry.TABLE_NAME, null, chartContentValues);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }

        Log.d(this.getClass().getName(), "Chart Info Inserted Successfully");
    }

    @NonNull
    private ContentValues getChartContentValues(FlowChart flowChart, String graphId) {
        ContentValues chartContentValues = new ContentValues();
        chartContentValues.put(ChartEntry.ID, flowChart.getId());
        chartContentValues.put(ChartEntry.NAME, flowChart.getName());
        chartContentValues.put(ChartEntry.DESCRIPTION, flowChart.getDescription());
        chartContentValues.put(ChartEntry.UPDATED_DATE, flowChart.getUpdatedDate());
        chartContentValues.put(ChartEntry.VERSION, flowChart.getVersion());
        chartContentValues.put(ChartEntry.OWNER, flowChart.getOwner());
        chartContentValues.put(ChartEntry.GRAPH_ID, graphId);

        //Join all resources together as a comma-separated list
        String allResList = TextUtils.join(",", flowChart.getAllRes());

        chartContentValues.put(ChartEntry.ALL_RESOURCES, allResList);
        chartContentValues.put(ChartEntry.IMAGE, flowChart.getImage());

        //Join all general resources in a list
        String resList = TextUtils.join(",", flowChart.getResources());
        chartContentValues.put(ChartEntry.RESOURCES, resList);

        chartContentValues.put(ChartEntry.TYPE, flowChart.getType().toString());
        chartContentValues.put(ChartEntry.SCORE, flowChart.getScore());
        return chartContentValues;
    }

    private List<Comment> getComments(String parentId, String parentType) {
        String selection = TCDatabaseContract.CommentEntry.PARENT_ID + " = '" + parentId + "' AND " +
                TCDatabaseContract.CommentEntry.PARENT_TYPE + " = '" + parentType + "'";
        Cursor c = getReadableDatabase().query(TCDatabaseContract.CommentEntry.TABLE_NAME,
                null, selection, null, null, null, null);
        c.moveToFirst();
        List<Comment> comments = new ArrayList<>(c.getCount());

        while (!c.isAfterLast()) {
            Comment comment = new Comment();
            comment.setOwnerId(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.CommentEntry.OWNER)));
            comment.setOwnerName(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.CommentEntry.OWNER_NAME)));
            comment.setAttachment(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.CommentEntry.ATTACHMENT)));
            comment.setCreatedDate(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.CommentEntry.CREATED_DATE)));
            comment.setText(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.CommentEntry.TEXT)));
            comments.add(comment);
            c.moveToNext();
        }
        c.close();
        return comments;
    }

    private void deleteComments(String parentId, String parentType) {
        String selection = TCDatabaseContract.CommentEntry.PARENT_ID + " = '" + parentId + "' AND " +
                TCDatabaseContract.CommentEntry.PARENT_TYPE + " = '" + parentType + "'";
        getWritableDatabase().delete(TCDatabaseContract.CommentEntry.TABLE_NAME, selection, null);
    }

    public void insertComments(List<Comment> comments, String parentId, String parentType) {
        for (Comment comment : comments) {
            insertComment(comment, parentId, parentType);
        }
    }

    public void insertComment(Comment comment, String parentId, String parentType) {
        ContentValues commentContentValues = new ContentValues();
        commentContentValues.put(TCDatabaseContract.CommentEntry.ID, getRandomId());
        commentContentValues.put(TCDatabaseContract.CommentEntry.PARENT_ID, parentId);
        commentContentValues.put(TCDatabaseContract.CommentEntry.PARENT_TYPE, parentType);
        commentContentValues.put(TCDatabaseContract.CommentEntry.OWNER, comment.getOwnerId());
        commentContentValues.put(TCDatabaseContract.CommentEntry.OWNER_NAME, comment.getOwnerName());
        commentContentValues.put(TCDatabaseContract.CommentEntry.TEXT, comment.getText());
        commentContentValues.put(TCDatabaseContract.CommentEntry.CREATED_DATE, comment.getCreatedDate());
        commentContentValues.put(TCDatabaseContract.CommentEntry.ATTACHMENT, comment.getAttachment());

        try {
            getWritableDatabase().insert(TCDatabaseContract.CommentEntry.TABLE_NAME, null, commentContentValues);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }

        Log.d(this.getClass().getName(), "Comment Info Inserted Successfully");
    }

    private void deleteGraph(String id) {
        String selection = TCDatabaseContract.GraphEntry.ID + " = ?";
        String selectionArgs[] = {id};
        getWritableDatabase().delete(TCDatabaseContract.GraphEntry.TABLE_NAME, selection, selectionArgs);
    }

    private Graph getGraph(String id) {
        String selection = TCDatabaseContract.GraphEntry.ID + " = ?";
        String selectionArgs[] = {id};
        Cursor c = getReadableDatabase().query(TCDatabaseContract.GraphEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            String firstVertex = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.GraphEntry.FIRST_VERTEX));
            return new Graph(getVertices(id), getEdges(id), firstVertex);
        }
        return null;
    }

    private List<Vertex> getVertices(String graphId) {
        String selection = TCDatabaseContract.VertexEntry.GRAPH_ID + " = ?";
        String selectionArgs[] = {graphId};
        Cursor c = getReadableDatabase().query(TCDatabaseContract.VertexEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        List<Vertex> verticies = new ArrayList<>(c.getCount());

        while (!c.isAfterLast()) {
            Vertex vertex = new Vertex();
            vertex.setId(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.VertexEntry.ID)));
            vertex.setName(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.VertexEntry.NAME)));
            vertex.setDetails(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.VertexEntry.DETAILS)));

            String images = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.VertexEntry.IMAGES));
            String res = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.VertexEntry.RESOURCES));
            if (images != null && !TextUtils.isEmpty(images.trim())) {
                vertex.setImages(Arrays.asList(images.split(",")));
            } else {
                vertex.setImages(new ArrayList<String>());
            }
            if (res != null && !TextUtils.isEmpty(res.trim())) {
                vertex.setResources(Arrays.asList(res.split(",")));
            } else {
                vertex.setResources(new ArrayList<String>());
            }

            verticies.add(vertex);
            c.moveToNext();
        }
        c.close();
        for (Vertex v : verticies) {
            v.setComments(getComments(v.getId(), Comment.PARENT_TYPE_VERTEX));
        }
        return verticies;
    }

    private List<Edge> getEdges(String graphId) {
        String selection = TCDatabaseContract.EdgeEntry.GRAPH_ID + " = ?";
        String selectionArgs[] = {graphId};
        Cursor c = getReadableDatabase().query(TCDatabaseContract.EdgeEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        List<Edge> edges = new ArrayList<>(c.getCount());

        while (!c.isAfterLast()) {
            Edge edge = new Edge();
            edge.setId(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.EdgeEntry.ID)));
            edge.setLabel(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.EdgeEntry.LABEL)));
            edge.setInV(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.EdgeEntry.IN_VERTEX)));
            edge.setOutV(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.EdgeEntry.OUT_VERTEX)));
            edge.setDetails(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.EdgeEntry.DETAILS)));
            edges.add(edge);
            c.moveToNext();
        }
        return edges;
    }

    /**
     * This method takes a Graph object and pushes all information into the SQL database
     *
     * @param g
     */
    private String insertGraph(Graph g) {
        String graphId = getRandomId();
        SQLiteDatabase sql = getWritableDatabase();
        sql.beginTransaction();//Insert Graph into graph table, vertices into vertex table

        //Create ContentValues object with all columns and values for just Graph
        ContentValues graphContentValues = new ContentValues();
        graphContentValues.put(TCDatabaseContract.GraphEntry.ID, graphId);
        graphContentValues.put(TCDatabaseContract.GraphEntry.FIRST_VERTEX, g.getFirstVertex());

        //Insert Graph w/o firstVertex
        try {
            sql.insert(TCDatabaseContract.GraphEntry.TABLE_NAME, null, graphContentValues);
            Log.d(this.getClass().getName(), "Graph Info Inserted Successfully");
            //Insert all vertices
            upsertVertices(sql, g.getVertices(), graphId);
            sql.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
            sql.endTransaction();
        }
        sql.endTransaction(); //End the transaction of inserting into graph and vertices

        //Insert all edges
        sql.beginTransaction(); //Insert edges
        try {
            upsertEdges(sql, g.getEdges(), graphId);
            sql.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
            sql.endTransaction();
        }
        sql.endTransaction();
        return graphId;
    }

    /**
     * Create Content values for vertices and insert
     *
     * @param sql      - SQLDatabase writer to put vertices in table
     * @param vertices - List of Vertices to insert
     */
    private void upsertVertices(SQLiteDatabase sql, List<Vertex> vertices, String graphID) {
        for (Vertex v : vertices) {
            upsertVertex(sql, graphID, v);
        }
        Log.d(this.getClass().getName(), "Vertices Info Inserted Successfully");
    }

    private void upsertVertex(SQLiteDatabase sql, String graphID, Vertex v) {
        ContentValues vertexContentValues = new ContentValues();
        vertexContentValues.put(TCDatabaseContract.VertexEntry.ID, v.getId());
        vertexContentValues.put(TCDatabaseContract.VertexEntry.GRAPH_ID, graphID);
        vertexContentValues.put(TCDatabaseContract.VertexEntry.NAME, v.getName());
        vertexContentValues.put(TCDatabaseContract.VertexEntry.DETAILS, v.getDetails());

        //Join all resources together as a comma-separated list
        String allRes = TextUtils.join(",", v.getResources());
        String allImgs = TextUtils.join(",", v.getImages());

        vertexContentValues.put(TCDatabaseContract.VertexEntry.RESOURCES, allRes);
        vertexContentValues.put(TCDatabaseContract.VertexEntry.IMAGES, allImgs);

        // Delete old vertices and insert new ones
        deleteComments(v.getId(), Comment.PARENT_TYPE_VERTEX);
        insertComments(v.getComments(), v.getId(), Comment.PARENT_TYPE_VERTEX);

        sql.insertWithOnConflict(TCDatabaseContract.VertexEntry.TABLE_NAME, null, vertexContentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Create Content values for edges and insert
     *
     * @param sql   - SQLDatabase writer to put edges in table
     * @param edges - List of edges to insert
     */
    private void upsertEdges(SQLiteDatabase sql, List<Edge> edges, String graphID) {
        for (Edge e : edges) {
            ContentValues edgeContentValues = new ContentValues();
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.ID, e.getId());
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.GRAPH_ID, graphID);
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.LABEL, e.getLabel());
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.OUT_VERTEX, e.getOutV());
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.IN_VERTEX, e.getInV());
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.DETAILS, e.getDetails());

            sql.insertWithOnConflict(TCDatabaseContract.EdgeEntry.TABLE_NAME, null, edgeContentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        Log.d(this.getClass().getName(), "Edges Info Inserted Successfully");
    }

    public void insertSession(Session s) {
        ContentValues sessionContentValues = getSessionContentValues(s);
        //Insert session into database
        try {
            getWritableDatabase().insert(TCDatabaseContract.SessionEntry.TABLE_NAME, null, sessionContentValues);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }

        Log.d(this.getClass().getName(), "Session Info Inserted Successfully");
    }

    public void upsertSession(Session s) {
        ContentValues sessionContentValues = getSessionContentValues(s);
        //Insert session into database
        try {
            int id = (int) getWritableDatabase().insertWithOnConflict(TCDatabaseContract.SessionEntry.TABLE_NAME, null, sessionContentValues, SQLiteDatabase.CONFLICT_IGNORE);
            if (id == -1) {
                Log.d(this.getClass().getName(), "Attempting Update of Existing Entry");
                String selection = TCDatabaseContract.SessionEntry.ID + " = ?";
                String[] selectionArgs = new String[] {sessionContentValues.getAsString(TCDatabaseContract.SessionEntry.ID)};
                getWritableDatabase().update(TCDatabaseContract.SessionEntry.TABLE_NAME, sessionContentValues, selection, selectionArgs);  // number 1 is the _id here, update to variable for your code
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }

        Log.d(this.getClass().getName(), "Session Info Inserted Successfully");
    }

    public Session getSession(String id, Context context) {
        String selection = TCDatabaseContract.SessionEntry.ID + " = ?";
        String selectionArgs[] = {id};
        Cursor c = getReadableDatabase().query(TCDatabaseContract.SessionEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            String flowchart_id = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.FLOWCHART_ID));
            FlowChart flow = getChart(flowchart_id);
            Session s = new Session(flow);
            s.setId(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.ID)));
            s.setCreatedDate(c.getLong(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.CREATED_DATE)));
            s.setManufacturer(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.MANUFACTURER)));
            s.setDepartment(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.DEPARTMENT)));
            s.setModelNumber(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.MODEL)));
            s.setSerialNumber(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.SERIAL)));
            s.setNotes(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.NOTES)));
            s.setFinished(c.getInt(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.FINISHED)) != 0);

            //Restore the Session status based on History
            String raw_history = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.HISTORY));
            ArrayList<String> history = new ArrayList<>(Arrays.asList(raw_history.split(",")));
            String raw_opt_history = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.OPTION_HISTORY));
            ArrayList<String> opt_history = new ArrayList<>(Arrays.asList(raw_opt_history.split(",")));
            s.setHistory(history);
            s.setOptionHistory(opt_history);

            //Set the current vertex in the traversal as the most recent vertex ID in the history list
            s.setCurrentVertex(history.get(history.size() - 1));
            s.updateHistoryStack();
            return s;
        }
        return null;
    }

    /**
     * Return list of all Session IDs currently stored in the database
     *
     * @return
     */
    public List<String> getSessions() {
        List<String> ids = new ArrayList<String>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TCDatabaseContract.SessionEntry.TABLE_NAME + " ", null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            ids.add(cursor.getString(cursor.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.ID)));
        }
        return ids;
    }

    public void deleteSession(Session s) {
        String selection = TCDatabaseContract.SessionEntry.ID + " = ?";
        String selectionArgs[] = {s.getId()};
        int result = getWritableDatabase().delete(TCDatabaseContract.SessionEntry.TABLE_NAME, selection, selectionArgs);
        Log.d("Delete Session",String.format("%d",result));
    }

    /**
     * Used to return a list of all active sessions currently stored in database
     *
     * @return The actual list of te active sessions (as objects)
     */
    public List<Session> getActiveSessions() {
        String selection = TCDatabaseContract.SessionEntry.FINISHED + " = ?";
        String selectionArgs[] = {"0"}; //False in boolean
        Cursor c = getReadableDatabase().query(TCDatabaseContract.GraphEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        List<Session> sessions = new ArrayList<Session>(c.getCount());

        while (!c.isAfterLast()) {
            String flowchart_id = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.FLOWCHART_ID));
            FlowChart flow = getChart(flowchart_id);
            Session s = new Session(flow);
            s.setCreatedDate(c.getLong(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.CREATED_DATE)));
            s.setManufacturer(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.MANUFACTURER)));
            s.setDepartment(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.DEPARTMENT)));
            s.setModelNumber(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.MODEL)));
            s.setSerialNumber(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.SERIAL)));
            s.setNotes(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.NOTES)));
            s.setFinished(c.getInt(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.FINISHED)) != 0);

            //Restore the Session status based on History
            String raw_history = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.HISTORY));
            ArrayList<String> history = new ArrayList<>(Arrays.asList(raw_history.split(",")));
            String raw_opt_history = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.OPTION_HISTORY));
            ArrayList<String> opt_history = new ArrayList<>(Arrays.asList(raw_opt_history.split(",")));
            s.setHistory(history);
            s.setOptionHistory(opt_history);

            //Set the current vertex in the traversal as the most recent vertex ID in the history list
            s.setCurrentVertex(history.get(history.size() - 1));
            s.updateHistoryStack();

            sessions.add(s);
            c.moveToNext();
        }
        return sessions;
    }

    public Cursor getActiveSessionsCursor() {
        String selection = TCDatabaseContract.SessionEntry.FINISHED + " = ?";
        String selectionArgs[] = {"0"}; //False in boolean
        return getReadableDatabase().query(TCDatabaseContract.SessionEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
    }

    public CursorLoader getActiveSessionsCursorLoader() {
        return new CursorLoader(context, null, null, null, null, null) {
            @Override
            public Cursor loadInBackground() {
                return getActiveSessionsCursor();
            }
        };
    }

    //Need a method to get all unique devices stored in the

    /**
     * @param id - ID of chart of interest
     * @return Count of sessions associated with chart of interest
     */
    public int getSessionsChartCount(String id) {
        String selection = "SELECT COUNT( " + TCDatabaseContract.SessionEntry.FLOWCHART_ID + " ) FROM "
                + TCDatabaseContract.SessionEntry.TABLE_NAME + " WHERE " + TCDatabaseContract.SessionEntry.FLOWCHART_ID
                + " = ?";
        String selectionArgs[] = {id};
        Cursor cursor = getReadableDatabase().rawQuery(selection, selectionArgs);
        cursor.moveToFirst();
        int counter = cursor.getInt(0);
        cursor.close();
        return counter;
    }

    /**
     *
     * @param id - ID of chart of interest
     * @return Cursor that points to all stored sessions associated with the flowchart of interest
     */
    public Cursor getSessionsFromChart(String id) {
        String selection = TCDatabaseContract.SessionEntry.FLOWCHART_ID + " = ?";
        return getReadableDatabase().query(TCDatabaseContract.SessionEntry.TABLE_NAME,
                null, selection, new String[] {id}, null, null, null);
    }

    /**
     *
     * @return
     */
    public CursorLoader getSessionsFromChartCursorLoader(final String id) {
        return new CursorLoader(context, null, null, null, null, null) {
            @Override
            public Cursor loadInBackground() {
                return getSessionsFromChart(id);
            }
        };
    }

    /**
     * @return Get Count of all sessions in unique (Month, Year)
     */
    public Map<String, Integer> getSessionDatesCounts() {
        HashMap<String, Integer> map = new HashMap<>();
        String selection = "Select " + TCDatabaseContract.SessionEntry.CREATED_DATE + " FROM " +
                TCDatabaseContract.SessionEntry.TABLE_NAME;
        Cursor cursor = getReadableDatabase().rawQuery(selection, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            //Read in Date into format MM, YYYY
            SimpleDateFormat dayFormat = new SimpleDateFormat("MMMM yyyy");
            String dateTime = dayFormat.format(cursor.getLong(0));
            //Log.d("SQL Database", new SimpleDateFormat("M").format(cursor.getLong(0)));
            if (map.containsKey(dateTime)) {
                int curr = map.get(dateTime);
                map.put(dateTime, ++curr);
            } else {
                map.put(dateTime, 1);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return map;
    }

    /**
     *
     * @param date - Date in the format "MMMM yyyy"
     * @return Cursor representing all entries belonging to that month/year combo
     */
    public Cursor getSessionsFromDate(String date) {
        String selection = "strftime('%m', " +
                TCDatabaseContract.SessionEntry.CREATED_DATE + "/1000, 'unixepoch') = ? AND strftime('%Y', " + TCDatabaseContract.SessionEntry.CREATED_DATE +
                "/1000, 'unixepoch') = ?";
        String[] selectionArgs = date.split(" "); //Split into Month and Date
        try {
            Date d = new SimpleDateFormat("MMMM").parse(selectionArgs[0]);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            selectionArgs[0] = String.format("%02d",cal.get(Calendar.MONTH)+1);
            Log.d("SQL Database",String.format("%s, %s",selectionArgs[0],selectionArgs[1]));
        } catch (ParseException e) {
            Log.e("SQL Database", "Error in Month format for session recovery");
        }

        //If made it, place the query
        return getReadableDatabase().query(TCDatabaseContract.SessionEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
    }

    /**
     *
     * @return
     */
    public CursorLoader getSessionsFromDateCursorLoader(final String date) {
        return new CursorLoader(context, null, null, null, null, null) {
            @Override
            public Cursor loadInBackground() {
                return getSessionsFromDate(date);
            }
        };
    }


    public Session getSessionFromCursor(Cursor c) {
        String flowchart_id = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.FLOWCHART_ID));
        FlowChart flow = getChart(flowchart_id);
        Session s = new Session(flow);
        s.setId(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.ID)));
        s.setCreatedDate(c.getLong(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.CREATED_DATE)));
        s.setManufacturer(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.MANUFACTURER)));
        s.setDepartment(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.DEPARTMENT)));
        s.setModelNumber(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.MODEL)));
        s.setSerialNumber(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.SERIAL)));
        s.setNotes(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.NOTES)));
        s.setFinished(c.getInt(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.FINISHED)) != 0);

        //Restore the Session status based on History
        String raw_history = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.HISTORY));
        ArrayList<String> history = new ArrayList<>(Arrays.asList(raw_history.split(",")));
        String raw_opt_history = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.SessionEntry.OPTION_HISTORY));
        ArrayList<String> opt_history = new ArrayList<>(Arrays.asList(raw_opt_history.split(",")));
        s.setHistory(history);
        s.setOptionHistory(opt_history);

        //Set the current vertex in the traversal as the most recent vertex ID in the history list
        s.setCurrentVertex(history.get(history.size() - 1));
        s.updateHistoryStack();

        return s;
    }

    private ContentValues getSessionContentValues(Session s) {
        ContentValues sessionContentValues = new ContentValues();
        if (s.getId() == null) {
            s.setId(getRandomId()); //Set the random ID field
        }
        sessionContentValues.put(TCDatabaseContract.SessionEntry.ID,s.getId());
        sessionContentValues.put(TCDatabaseContract.SessionEntry.CREATED_DATE, s.getCreatedDate());
        sessionContentValues.put(TCDatabaseContract.SessionEntry.MANUFACTURER, s.getManufacturer());
        sessionContentValues.put(TCDatabaseContract.SessionEntry.FINISHED, s.isFinished());
        sessionContentValues.put(TCDatabaseContract.SessionEntry.DEPARTMENT, s.getDepartment());
        sessionContentValues.put(TCDatabaseContract.SessionEntry.MODEL, s.getModelNumber());
        sessionContentValues.put(TCDatabaseContract.SessionEntry.SERIAL, s.getSerialNumber());
        sessionContentValues.put(TCDatabaseContract.SessionEntry.NOTES, s.getNotes());

        //Generate a comma separated list of strings for the two history entries
        String history = TextUtils.join(",", s.getHistory());
        String opt_history = TextUtils.join(",", s.getOptionHistory());

        sessionContentValues.put(TCDatabaseContract.SessionEntry.HISTORY, history);
        sessionContentValues.put(TCDatabaseContract.SessionEntry.OPTION_HISTORY, opt_history);
        sessionContentValues.put(TCDatabaseContract.SessionEntry.FLOWCHART_ID, s.getFlowchart().getId());

        return sessionContentValues;
    }
}
