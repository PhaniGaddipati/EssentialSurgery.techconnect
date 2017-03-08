package org.techconnect.network;

import com.google.gson.JsonObject;

import org.techconnect.model.Comment;
import org.techconnect.model.JsendResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TCRetrofit {

    //Define all of the headers which are needed for the communication here
    //Initially defined as null
    @Headers({
            "X-Auth-Token: ",
            "X-User-Id: "
    })

    //Post app feedback
    @FormUrlEncoded
    @POST("api/v1/sys/feedback")
    Call<JsendResponse> postAppFeedback(@Field("userId") String userId, @Field("text") String text);

    //Login the user
    @FormUrlEncoded
    @POST("api/v1/login")
    Call<JsendResponse> login(@Field("email") String email, @Field("password") String pass);

    //Register a User
    @FormUrlEncoded
    @POST("api/v1/register")
    Call<JsendResponse> register(@Field("email") String email,
                                 @Field("password") String password,
                                 @Field("countryCode") String countryCode,
                                 @Field("name") String name,
                                 @Field("organization") String org,
                                 @Field("expertises[]") String[] expertises);

    //Logout the user. I don't think that I need to pass in anything? Maybe the user?
    @POST("api/v1/logout")
    Call<JsendResponse> logout(@Header("X-Auth-Token") String auth_token, @Header("X-User-Id") String userId);

    // POST update user
    @POST("api/v1/user/{id}")
    Call<JsendResponse> updateUser(@Header("X-Auth-Token") String auth_token, @Header("X-User-Id") String userId, @Path("id") String id, @Body JsonObject user);

    // GET user
    @GET("api/v1/user/{id}")
    Call<JsendResponse> getUser(@Path("id") String id);

    // Search users
    @POST("api/v1/users/search")
    Call<JsendResponse> searchUsers(@Body RequestBody body);

    //Just want to get the getCatalog, no input needed
    @GET("api/v1/catalog")
    Call<JsendResponse> getCatalog();

    @GET("api/v1/chart/{id}")
    Call<JsendResponse> getFlowchart(@Path("id") String id);

    @FormUrlEncoded
    @POST("api/v1/charts")
    Call<JsendResponse> getFlowcharts(@Field("ids[]") String[] ids);
    //This String ids is a comma separated list of the ids desired

    //Delete a specific chart from the Server
    @DELETE("api/v1/chart/{id}")
    Call<JsendResponse> deleteChart(@Header("X-Auth-Token") String auth_token, @Header("X-User-Id") String userId, @Path("id") String id);

    //Attempts to post a comment onto a chart or node with id id.
    @POST("api/v1/chart/{id}/comment")
    Call<JsendResponse> postComment(@Header("X-Auth-Token") String auth_token, @Header("X-User-Id") String userId, @Path("id") String chartId, @Body Comment comment);

    //Attempt to delete a comment from a chart
    //Learn how to use the OkHttp structure to just send a JSON object: http://stackoverflow.com/questions/34179922/okhttp-post-body-as-json
    //Json object just has the field "commentId"
    @DELETE("api/v1/chart/{id}/comment")
    Call<JsendResponse> deleteComment(@Header("X-Auth-Token") String auth_token, @Header("X-User-Id") String userId, @Path("id") String chartId, @Body RequestBody body);

    //Post feedback on a chart, which is upvoting and downvoting
    //Use same approach as above: Json object has field "feedback"
    @POST("api/v1/chart/{id}/feedback")
    Call<JsendResponse> postFeedback(@Header("X-Auth-Token") String auth_token, @Header("X-User-Id") String userId, @Path("id") String chart_id, @Body RequestBody body);

}
