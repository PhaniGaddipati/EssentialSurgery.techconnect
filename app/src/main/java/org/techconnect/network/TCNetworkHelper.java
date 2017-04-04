package org.techconnect.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.techconnect.model.Comment;
import org.techconnect.model.FlowChart;
import org.techconnect.model.JsendResponse;
import org.techconnect.model.User;
import org.techconnect.model.UserAuth;
import org.techconnect.model.Vertex;
import org.techconnect.network.serializers.FlowChartDeserializer;
import org.techconnect.network.serializers.FlowChartSerializer;
import org.techconnect.network.serializers.JsendResponseDeserializer;
import org.techconnect.network.serializers.VertexDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TCNetworkHelper {

    public static final String BASE_URL = "https://jhtechconnect.me/";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private Gson gson;
    private TCRetrofit service;

    private JsendResponse lastError = null;
    private int lastCode = -1;

    public TCNetworkHelper() {
        gson = buildGson();
        service = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(TCRetrofit.class);
    }

    private Gson buildGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(FlowChart.class, new FlowChartDeserializer());
        gsonBuilder.registerTypeAdapter(FlowChart.class, new FlowChartSerializer());
        gsonBuilder.registerTypeAdapter(JsendResponse.class, new JsendResponseDeserializer());
        gsonBuilder.registerTypeAdapter(Vertex.class, new VertexDeserializer());
        return gsonBuilder.create();
    }

    public boolean postAppFeedback(String userId, String text) throws IOException {
        Response<JsendResponse> resp = service.postAppFeedback(userId, text).execute();
        lastCode = resp.code();
        //First check to see if the request succeeded
        if (!resp.isSuccessful()) {
            return false;
        } else {
            return true;
        }
    }

    public UserAuth login(String email, String password) throws IOException {
        Response<JsendResponse> resp = service.login(email, password).execute();
        lastCode = resp.code();
        //First check to see if the request succeeded
        if (!resp.isSuccessful()) {
            try {
                lastError = gson.fromJson(resp.errorBody().string(), JsendResponse.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            //Now, I'm expecting a data object with fields relevant
            JsonObject obj = resp.body().getData().getAsJsonObject();
            return gson.fromJson(obj, UserAuth.class);
        }
    }

    public boolean logout(UserAuth auth) throws IOException {
        Response<JsendResponse> resp = service.logout(auth.getAuthToken(), auth.getUserId()).execute();
        lastCode = resp.code();
        if (!resp.isSuccessful()) {
            try {
                lastError = gson.fromJson(resp.errorBody().string(), JsendResponse.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public User register(String email, String password,
                         String countryCode, String name, String organization,
                         String[] expertises) throws IOException {
        Response<JsendResponse> resp = service.register(email, password, countryCode, name, organization, expertises).execute();
        lastCode = resp.code();
        if (!resp.isSuccessful()) {
            try {
                lastError = gson.fromJson(resp.errorBody().string(), JsendResponse.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            JsonObject obj = resp.body().getData();
            return gson.fromJson(obj.get("user"), User.class);
        }
    }

    public User updateUser(User user, UserAuth userAuth) throws IOException {
        JsonObject user_obj = new JsonObject();
        user_obj.add("user", gson.toJsonTree(user));
        Log.d("Update User",user_obj.toString());
        Response<JsendResponse> resp = service.updateUser(userAuth.getAuthToken(), userAuth.getUserId(), userAuth.getUserId(), user_obj).execute();
        lastCode = resp.code();
        if (!resp.isSuccessful()) {
            try {
                lastError = gson.fromJson(resp.errorBody().string(), JsendResponse.class);
                Log.e("Update User failed", lastError.getMessage());
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            JsonObject obj = resp.body().getData();
            Log.d("Update User",obj.toString());
            return gson.fromJson(obj.get("user"), User.class);
        }
    }

    public User getUser(String id) throws IOException {
        Response<JsendResponse> resp = service.getUser(id).execute();
        lastCode = resp.code();
        if (!resp.isSuccessful()) {
            try {
                lastError = gson.fromJson(resp.errorBody().string(), JsendResponse.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            JsonObject obj = resp.body().getData();
            return gson.fromJson(obj.get("user"), User.class);
        }
    }

    /**
     * Goal is to search user fields for the filter. Used to identify users which fit a particular filter
     *
     * @param filter - string search query
     * @return List of users which satisfy the search query
     */
    public List<User> searchUsers(String filter, int limit, int skip) throws IOException {
        JsonObject body = new JsonObject();
        body.addProperty("query", filter);
        body.addProperty("limit", limit);
        body.addProperty("skip", skip);
        RequestBody requestBody = RequestBody.create(JSON, body.toString());
        Response<JsendResponse> resp = service.searchUsers(requestBody).execute();
        lastCode = resp.code();
        if (!resp.isSuccessful()) {
            try {
                lastError = gson.fromJson(resp.errorBody().string(), JsendResponse.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            //Now, expecting a JsendResponse with a list of user objects
            JsonObject obj = resp.body().getData();
            ArrayList<User> users = new ArrayList<User>();
            Log.d("Directory Setup", String.format("Num Users: %d", obj.get("results").getAsJsonArray().size()));
            for (JsonElement j : obj.get("results").getAsJsonArray()) {
                User u = gson.fromJson(j, User.class);
                users.add(u);
            }
            return users;
        }
    }

    /**
     * This function retrieves the getCatalog of devices from the server.
     *
     * @return All flowcharts in the catalog. These flowcharts do not have the graph loaded.
     * @throws IOException
     */
    public FlowChart[] getCatalog() throws IOException {
        Response<JsendResponse> resp = service.getCatalog().execute();
        lastCode = resp.code();
        //First, check whether there is an error. HAVE TO DO THIS TO SATISFY RETROFIT!
        //Cheking first if the http request was successful. if not, have to manually deserialize the JSON
        if (!resp.isSuccessful()) {
            try {
                lastError = gson.fromJson(resp.errorBody().string(), JsendResponse.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            //Now, I'm expecting a getCatalog
            JsonObject obj = resp.body().getData();
            //Obj should have a JsonArray of flowcharts
            ArrayList<FlowChart> flowcharts = new ArrayList<FlowChart>();
            for (JsonElement j : obj.get("flowcharts").getAsJsonArray()) {
                //For sake of the virtual study, only want a couple guides to appear
                FlowChart f = gson.fromJson(j, FlowChart.class);
                //Only Suction machine and Autoclave
                if (f.getId().equals("B9C9E7A23G9F3GDJ8") || f.getId().equals("JE2C662E39H5E2265")) {
                    flowcharts.add(f);
                }
            }
            return flowcharts.toArray(new FlowChart[flowcharts.size()]);
        }
    }

    public FlowChart getChart(String id) throws IOException {
        Response<JsendResponse> resp = service.getFlowchart(id).execute();
        lastCode = resp.code();
        //First, check whether there is an error
        if (!resp.isSuccessful()) {
            try {
                lastError = gson.fromJson(resp.errorBody().string(), JsendResponse.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            //Now, I know that there is a FlowChart contained in this resp. Just get it
            JsonObject obj = resp.body().getData();
            return gson.fromJson(obj.get("flowchart"), FlowChart.class);
        }
    }

    /**
     * This function is used to get a list of specific charts. This list must be sent to
     * Retrofit as a comma separated list of ids as a String. This converts from a list of integers
     * for now. May not want that format in the future, but it's a place to start.
     *
     * @param ids
     * @return
     * @throws IOException
     */
    public FlowChart[] getCharts(String[] ids) throws IOException {
        Response<JsendResponse> resp = service.getFlowcharts(ids).execute();
        lastCode = resp.code();
        if (!resp.isSuccessful()) {
            try {
                lastError = gson.fromJson(resp.errorBody().string(), JsendResponse.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            //Now I know, I should be getting two objects. bad ID strings as well as the actual flowcharts
            JsonObject obj = resp.body().getData();
            //For now, let's just look at the good charts
            ArrayList<FlowChart> flowcharts = new ArrayList<FlowChart>();
            for (JsonElement j : obj.get("flowcharts").getAsJsonArray()) {
                flowcharts.add(gson.fromJson(j, FlowChart.class));
            }
            return flowcharts.toArray(new FlowChart[flowcharts.size()]);
        }
    }

    /**
     * Use this method to comment on a flowchart
     *
     * @param chart_id - ID of the flowchart to post to
     * @param c        - The actual comment object
     * @throws IOException
     */
    public Comment comment(String chart_id, Comment c, UserAuth auth) throws IOException {
        Response<JsendResponse> resp = service.postComment(auth.getAuthToken(), auth.getUserId(), chart_id, c).execute();
        lastCode = resp.code();
        if (!resp.isSuccessful()) {
            try {
                lastError = gson.fromJson(resp.errorBody().string(), JsendResponse.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        }
        return gson.fromJson(resp.body().getData().get("comment"), Comment.class);
    }

    /**
     * Use this method to delete a comment on a flowchart on the server
     *
     * @param chart_id   - ID of the chart
     * @param comment_id - ID of the
     */
    public FlowChart deleteComment(String chart_id, String comment_id, UserAuth auth) throws IOException {
        //First, I need to build the RequestBody object to handle the single string
        JsonObject body = new JsonObject();
        body.addProperty("commentId", comment_id);
        RequestBody requestBody = RequestBody.create(JSON, body.toString());
        Response<JsendResponse> resp = service.deleteComment(auth.getAuthToken(), auth.getUserId(),
                chart_id, requestBody).execute();
        lastCode = resp.code();
        if (!resp.isSuccessful()) {
            try {
                lastError = gson.fromJson(resp.errorBody().string(), JsendResponse.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        }
        return gson.fromJson(resp.body().getData().get("flowchart"), FlowChart.class);

    }

    /**
     * Use this method to provide feedback on the chart (up-vote, down-vote currently)
     *
     * @param vote - String which determines whether up-vote (true)
     * @param clear - boolean that determines whether server should remove all feedback from user
     */
    public FlowChart postFeedback(String chart_id, String vote, UserAuth auth, boolean clear) throws IOException {
        JsonObject body = new JsonObject();
        body.addProperty("feedback", vote);
        if (clear) {
            body.addProperty("clear", true);
        }
        RequestBody requestBody = RequestBody.create(JSON, body.toString());

        Response<JsendResponse> resp = service.postFeedback(auth.getAuthToken(),
                auth.getUserId(), chart_id, requestBody).execute();
        lastCode = resp.code();
        if (!resp.isSuccessful()) {
            try {
                lastError = gson.fromJson(resp.errorBody().string(), JsendResponse.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        }
        return gson.fromJson(resp.body().getData().get("flowchart"), FlowChart.class);
    }

    public JsendResponse getLastError() {
        return lastError;
    }

    public int getLastCode() {
        return lastCode;
    }
}
