package co.geeksters.cafe_ami.interfaces;

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedString;

public interface MemberInterface {

    @FormUrlEncoded
    @POST("/member/logout")
    void logout(@Field("access_token") String token, Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/members/{id}")
    void updateMember(@Path("id") int userId, @Field("_method") String method, @Field("access_token") String token, @Field("full_name") String fullName,
                      @Field("email") String email, @Field("hub") String hub, @Field("blurp") String blurp,@Field("goal") String goal,@Field("phone") String phone,@Field("whatsapp") String whatsapp, @Field("social[twitter]") String twitter,
                      @Field("social[facebook]") String facebook, @Field("social[linkedin]") String linkdin, @Field("social[skype]") String skype,
                      @Field("social[blog]") String blog, @Field("social[website]") String website, @Field("social[other]") String other,
                      @Field("interests") String interests, @Field("companies") String companies, @Field("latitude") float latitude,
                      @Field("longitude") float longitude, @Field("notify_by_email_on_comment") Boolean notifyByEmailOnComment,
                      @Field("notify_by_push_on_comment") Boolean notifyByPushOnComment, @Field("notify_by_email_on_todo") Boolean notifyByEmailOnTodo,
                      @Field("notify_by_push_on_todo") Boolean notifyByPushOnTodo,@Field("radar_visibility") Boolean radarVisibility,@Field("device_token") String deviceToken,@Field("device_type") String deviceType, Callback<JsonElement> callback);

    @Multipart
    @POST("/member/profile/image")
    void updateImageMember(@Part("id") int id, @Part("access_token") TypedString token, @Part("file") TypedFile photo, Callback<JsonElement> callback);

    @GET("/members")
    void listAllMembers(Callback<JsonElement> callback);

    @GET("/members/{id}")
    void getMemberInfo(@Path("id") int userId, Callback<JsonElement> callback);

    @GET("/members/list")
    void listAllMembersByPaginationOrSearch(@Query("from") int from, @Query("size") int size, @Query("order") String order, @Query("col") String col, Callback<JsonElement> callback);

    @GET("/member/search")
    void searchForMembersFromKey(@Query("string") String string,@Query("from") int from, @Query("size") int size, @Query("order") String order,@Query("col") String col, Callback<JsonElement> callback);

    @GET("/members/listForTodo")
    void listAllMembersByPaginationForTodo(@Query("from") int from, @Query("size") int size, @Query("order") String order, @Query("col") String col, Callback<JsonElement> callback);

    @GET("/member/searchForTodo")
    void searchForMembersForTodo(@Query("string") String string,@Query("from") int from, @Query("size") int size, @Query("order") String order,@Query("col") String col, Callback<JsonElement> callback);

    @GET("/member/suggest")
    void suggestionMember(@Query("string") String search, Callback<JsonElement> callback);

    @GET("/members/{id}/around")
    void getMembersArroundMe(@Path("id") int userId, @Query("radius") float radius, Callback<JsonElement> callback);

    @Multipart
    @POST("/members/{id}/image")
    void uploadImage(@Path("id") int userId,@Part("access_token") String token,@Part("_method") String method, @Part("file") TypedFile file, Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/members/{id}")
    void deleteMember(@Path("id") int userId, @Field("_method") String method, @Query("order") String order, Callback<JsonElement> callback);

    /*@POST("/members/password/remind")
    void passwordReminder(@Body TypedInput emails, Callback<JsonElement> callback);*/

    // this method is executed from the link sent by email to remind a member to reset his password
    @POST("/members/password/reset")
    void passwordReset(@Body TypedInput resetParams, Callback<JsonElement> callback);

    @POST("/members/confirmation/send")
    void sendEmailConfirmationOnRegister(@Body TypedInput userId, Callback<JsonElement> callback);

    @POST("/members/confirmation/validate")
    void validateEmailConfirmationOnRegister(@Body TypedInput token, Callback<JsonElement> callback);

    // Todo : To delete

    @POST("/members/{id}/ambassador")
    void updateStatusMember(@Path("id") int userId, @Body TypedInput ambassador, Callback<JsonElement> callback);

    @POST("/members/{id}/deviceToken")
    void updateTokenMember(@Path("id") int userId, @Body TypedInput device_token, Callback<JsonElement> callback);

    @POST("/members/{id}/notifies")
    void updateNotifyOptionsMember(@Path("id") int userId, @Body TypedInput notifOptions, Callback<JsonElement> callback);

    @POST("/members/{id}/geolocation")
    void updateLocationMember(@Path("id") int userId, @Body TypedInput locations, Callback<JsonElement> callback);

}