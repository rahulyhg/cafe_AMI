package co.geeksters.hq.global.helpers;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by soukaina on 04/12/14.
 */
public class ParseHelper {

    public static TypedInput createTypedInputFromModel(Object modelObject){
        TypedInput inputHttpRequest = null;
        try {
            inputHttpRequest = new TypedByteArray("application/json", new Gson().toJson(modelObject).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inputHttpRequest;
    }

    public static TypedInput createTypedInputFromModelByMethod(Object modelObject, String method){
        TypedInput inputHttpRequest = null;
        try {
            JSONObject modelJson = new JSONObject(new Gson().toJson(modelObject));
            modelJson.put("_method", method);

            inputHttpRequest = createTypedInputFromJsonObject(modelJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return inputHttpRequest;
    }

    public static TypedInput createTypedInputFromOneKeyValue(String key, Object value){
        TypedInput inputHttpRequest = null;
        try {
            JSONObject modelJson = new JSONObject();
            modelJson.put(key, value);

            inputHttpRequest = createTypedInputFromJsonObject(modelJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return inputHttpRequest;
    }

    public static TypedInput createTypedInputFromJsonObject(JSONObject object){
        TypedInput inputHttpRequest = null;
        try {
            inputHttpRequest = new TypedByteArray("application/json", object.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inputHttpRequest;
    }

    public static JsonElement parseMemberResponse(JsonElement response){
        if(response.getAsJsonObject().get("newsletter").toString().equals("false")){
            response.getAsJsonObject().addProperty("newsletter", "0");
        }

        if(response.getAsJsonObject().get("newsletter").toString().equals("true")){
            response.getAsJsonObject().addProperty("newsletter", "1");
        }

        if(response.getAsJsonObject().get("references") != null) {
            JsonArray references = response.getAsJsonObject().get("references").getAsJsonArray();

            for (int i = 0; i < references.size(); i++) {
                if (references.get(i).getAsJsonObject().get("newsletter").toString().equals("false")) {
                    references.get(i).getAsJsonObject().addProperty("newsletter", "0");
                }

                if (references.get(i).getAsJsonObject().get("newsletter").toString().equals("true")) {
                    references.get(i).getAsJsonObject().addProperty("newsletter", "1");
                }
            }
        }

        if(response.getAsJsonObject().get("created_at").isJsonObject()){
            response.getAsJsonObject().addProperty("created_at", response.getAsJsonObject().get("created_at").getAsJsonObject().get("date").toString().replace("\"", "")
            );
        }

        if(response.getAsJsonObject().get("updated_at").isJsonObject()){
            response.getAsJsonObject().addProperty("updated_at", response.getAsJsonObject().get("updated_at").getAsJsonObject().get("date").toString().replace("\"", "")
            );
        }

        if(response.getAsJsonObject().get("ambassador").toString().equals(null)){
            response.getAsJsonObject().addProperty("ambassador", "false"
            );
        }

        return response;
    }
}
