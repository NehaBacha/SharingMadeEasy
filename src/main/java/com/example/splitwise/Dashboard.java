package com.example.splitwise;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Dashboard extends AppCompatActivity {

    private TextView textViewYouOwe,textViewYouAreOwed,total;
    private Button buttonFriends,buttonAddBill;
    private String email;
    private ListView youoweList,youareowedlist;
    ArrayList<HashMap<String,String>> YouoweList;
    ArrayList<HashMap<String,String>> YouareowedList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        YouoweList=new ArrayList<>();
        YouareowedList=new ArrayList<>();
        textViewYouOwe=(TextView) findViewById(R.id.youOweDlrTextview);
        textViewYouAreOwed=(TextView) findViewById(R.id.owedDlrTextview);
        total=(TextView)findViewById(R.id.totalDlrTextview);
        youoweList=(ListView)findViewById(R.id.youOweListview);
        youareowedlist=(ListView)findViewById(R.id.owedListview);

        buttonFriends=(Button)findViewById(R.id.friendsButton);
        buttonAddBill=(Button)findViewById(R.id.addBillButton);


       // setListViewHeightBasedOnChildren(youoweList);
        //setListViewHeightBasedOnChildren(youareowedlist);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        email=sharedPreferences.getString(ConstantValues.KEY_EMAIL,"");
        if (email!=null) {
            Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();
        }

        buttonFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Dashboard.this,FriendsActivity.class);
                startActivity(i);

            }
        });

        buttonAddBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Dashboard.this,AddBillActivity.class);
                startActivity(i);
            }
        });

        new Asynctack().execute();

    }

    private class Asynctack extends AsyncTask<String,String,Void>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(Dashboard.this);
            progressDialog.setMessage("Processing..");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... args) {
            VolleyUtility volleyUtility=new VolleyUtility();

            final HashMap<String,String> putvalues=new HashMap<String, String>();
            putvalues.put("email",email);


            ServerCallback sv=new ServerCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    String ajsonString;
                    try {
                        String youareowed = jsonObject.getString("youareowed");
                        textViewYouAreOwed.setText(youareowed);
                        String youowe = jsonObject.getString("oweyou");
                        textViewYouOwe.setText(youowe);
                        String totalValue=jsonObject.getString("total_bal");
                        total.setText(totalValue);
                        String list=jsonObject.getString("activity");

                        JSONArray youareowelist=jsonObject.getJSONArray("uareowelist");

                        for (int i=0;i<youareowelist.length();i++){
                            JSONObject yao=youareowelist.getJSONObject(i);

                            String email=yao.getString("id");
                            String value=yao.getString("value");

                            HashMap<String,String > yaol=new HashMap<>();

                            yaol.put("id",email);
                            yaol.put("value",value);
                            YouareowedList.add(yaol);
                        }
                        ListAdapter adapter=new SimpleAdapter(Dashboard.this,YouareowedList,
                                R.layout.you_are_owed_list_item,new String[]{"id","value"},
                                new int[]{R.id.email,R.id.value});
                        youareowedlist.setAdapter(adapter);

                        JSONArray youowelist=jsonObject.getJSONArray("youowelist");

                        for(int i=0;i<youowelist.length();i++){
                            JSONObject yo=youowelist.getJSONObject(i);

                            String email=yo.getString("id");
                            String value=yo.getString("value");

                            HashMap<String,String> yol=new HashMap<>();
                            yol.put("id",email);
                            yol.put("value",value);

                            YouoweList.add(yol);
                        }
                        ListAdapter adapter1=new SimpleAdapter(Dashboard.this,YouoweList,
                                R.layout.you_owe_list_item,new String[]{"id","value"},
                                new int[]{R.id.email1,R.id.value1});
                        youoweList.setAdapter(adapter1);


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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /*public void onBackPressed(){
        Intent i=new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_logout:
                finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
