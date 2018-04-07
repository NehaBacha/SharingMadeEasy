package com.example.splitwise;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mcontext;

    private MySingleton(Context context){
        mcontext=context;
        mRequestQueue=getRequestQueue();
    }

    public static synchronized MySingleton getInstance(Context context){
        if (mInstance==null){
            mInstance=new MySingleton(context);
        }return mInstance;

    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue==null){
            mRequestQueue= Volley.newRequestQueue(mcontext.getApplicationContext());

        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req,String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }
}
