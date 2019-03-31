package com.google.cloud.android.speech;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiService {
    private static final String TAG = "GoogleVoiceToText";
    Gson gson = new Gson();

    private static ApiService apiService = new ApiService();

    public ApiService() {
    }

    public static ApiService getInstance() {
        return apiService;
    }

    public void addSinhalaQuestion(Context context, final Question sinhalaQuestion, final VolleyCallback callback) {


        RequestQueue queue = Volley.newRequestQueue(context);


        final String stringUser = gson.toJson(sinhalaQuestion);
        Log.d(TAG, stringUser);
        Log.d(TAG, Config.getSinhalaQuestion);

        JSONObject object = null;

        try {
            object = new JSONObject(stringUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, Config.getSinhalaQuestion, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccessResponse(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        queue.add(jsonobj);

    }

    public void checkSinhalaQuestion(Context context, final GetQuestion sinhalaQuestion, final VolleyCallback callback) {


        RequestQueue queue = Volley.newRequestQueue(context);
        String url = null;

        if (Config.Instance.getLanguageCode().equals("si-LK")) {
            url = Config.checkSinhalaQuestion;
        } else if (Config.Instance.getLanguageCode().equals("ta-LK")) {
            url = Config.checkTamilQuestion;

        } else {
            url = Config.checkEnglishQuestion;

        }

        final String stringUser = gson.toJson(sinhalaQuestion);
        Log.d(TAG, stringUser);
        Log.d(TAG, Config.checkSinhalaQuestion);

        JSONObject object = null;

        try {
            object = new JSONObject(stringUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccessResponse(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        queue.add(jsonobj);

    }

    public void checkTamilQuestion(Context context, final GetQuestion tamilQuestion, final VolleyCallback callback) {


        RequestQueue queue = Volley.newRequestQueue(context);


        final String stringUser = gson.toJson(tamilQuestion);
        Log.d(TAG, stringUser);
        Log.d(TAG, Config.checkTamilQuestion);

        JSONObject object = null;

        try {
            object = new JSONObject(stringUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, Config.checkTamilQuestion, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccessResponse(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        queue.add(jsonobj);

    }


    public void checkEnglishaQuestion(Context context, final GetQuestion englishQuestion, final VolleyCallback callback) {


        RequestQueue queue = Volley.newRequestQueue(context);


        final String stringUser = gson.toJson(englishQuestion);
        Log.d(TAG, stringUser);
        Log.d(TAG, Config.checkEnglishQuestion);

        JSONObject object = null;

        try {
            object = new JSONObject(stringUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, Config.checkEnglishQuestion, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccessResponse(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        queue.add(jsonobj);

    }


}
