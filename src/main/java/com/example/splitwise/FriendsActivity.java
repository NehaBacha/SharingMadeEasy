package com.example.splitwise;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsActivity extends AppCompatActivity {

    private String email;
    ListView lv;
    ArrayList<HashMap<String,String>> FriendsList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        lv=(ListView) findViewById(R.id.friends_list);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        email=sharedPreferences.getString(ConstantValues.KEY_EMAIL,"");
        if (email!=null) {
            Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();
        }
        new asynkParser().execute();
        FriendsList = new ArrayList<>();
    }

    private class asynkParser extends AsyncTask<String,String,Void>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(FriendsActivity.this);
            progressDialog.setMessage("Processing..");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... agrs) {

            VolleyUtility volleyUtility = new VolleyUtility();

            final HashMap<String, String> putvalues = new HashMap<String, String>();
            putvalues.put("email", email);


            ServerCallback sv = new ServerCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    try {
                        JSONArray list = jsonObject.getJSONArray("activity");

                        for (int i=0;i<list.length();i++){
                            JSONObject yao=list.getJSONObject(i);

                            String id=yao.getString("id");
                            String value=yao.getString("value");

                            HashMap<String,String > yaol=new HashMap<>();

                            yaol.put("id",id);
                            yaol.put("value",value);
                            FriendsList.add(yaol);
                        }
                        ListAdapter adapter=new SimpleAdapter(FriendsActivity.this,FriendsList,
                                R.layout.simple_friends_list,new String[]{"id","value"},
                                new int[]{R.id.id,R.id.value2});
                        lv.setAdapter(adapter);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            };
            try {

                volleyUtility.makeCall(ConstantValues.urlForDashboard,putvalues, sv, getApplicationContext());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            if(progressDialog.isShowing())
                progressDialog.dismiss();




        }
    }
}
