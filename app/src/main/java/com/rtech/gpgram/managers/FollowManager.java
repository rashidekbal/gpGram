package com.rtech.gpgram.managers;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.gpgram.BuildConfig;
import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.NetworkCallbackInterface;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.gpgram.models.Profile_Model_minimal;
import com.rtech.gpgram.constants.ApiEndPoints;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class FollowManager {
    SharedPreferences loginInfo;
    String myid,token;
    String Followurl= ApiEndPoints.FOLLOW;
    String UnFollowurl=ApiEndPoints.UNFOLLOW;
    ArrayList<Profile_Model_minimal> dataList = new ArrayList<>();
Context context;
    public FollowManager(Context context){
        this.context=context;
        AndroidNetworking.initialize(context);
        this.loginInfo=context.getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME,MODE_PRIVATE);
        this.myid=loginInfo.getString(SharedPreferencesKeys.USER_ID,"null");
        this.token=loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null");
    }

    public void  follow(String userid, NetworkCallbackInterface callbackIterface) {
        String url=ApiEndPoints.FOLLOW;

        JSONObject packet = new JSONObject();
        try {
            packet.put("followingid", userid);

            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addApplicationJsonBody(packet)
                    .addHeaders("Authorization", "Bearer ".concat(token)).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    callbackIterface.onSuccess();

                }


                @Override
                public void onError(ANError anError) {
                    if(BuildConfig.DEBUG){
                        Log.d("ApiError", "Error"+ anError.getMessage());

                    }
                    int errorCode=anError.getErrorCode();
                    callbackIterface.onError(anError.toString());

                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public void unfollow(String userid, NetworkCallbackInterface callbackIterface) {
        String url=ApiEndPoints.UNFOLLOW;
        JSONObject packet = new JSONObject();
        try {
            packet.put("followingid", userid);

            AndroidNetworking.post(url).setPriority(Priority.HIGH).addHeaders("Authorization", "Bearer ".concat(token)).addApplicationJsonBody(packet).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("unfollowerror", response.toString());
                    callbackIterface.onSuccess();

                }


                @Override
                public void onError(ANError anError) {

                    int errorCode=anError.getErrorCode();
                    if(BuildConfig.DEBUG){
                        Log.d("ApiError", "Error"+ anError.getMessage());

                    }
                    callbackIterface.onError(anError.toString());

                }
            });



        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public void getFollowers(String userid, NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        Log.d("feddhit", "getFpllowers" +
                ": ");
        String url=ApiEndPoints.GET_FOLLOWERS.concat(userid);
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization", "Bearer ".concat(token))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callback.onError(anError.getErrorDetail());

                    }
                });
    }
    public  void getFollowings(String userid, NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        String url=ApiEndPoints.GET_FOLLOWINGS.concat(userid);
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization", "Bearer ".concat(token))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callback.onError(anError.getErrorDetail());

                    }
                });
    }
}
