package com.example.splitwise;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddBillActivity extends AppCompatActivity {

    private String email;
    private Button buttonGo;
    private MultiAutoCompleteTextView emailEdittext;
    private ListView lv;
    Cursor cursor;
    ArrayList<HashMap<String,String>> FriendsList;
    List<String> responseList = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_bill);
        //initialize a MultiAutoCompleteTextView
        emailEdittext = (MultiAutoCompleteTextView) findViewById(R.id.editText);
        buttonGo = (Button) findViewById(R.id.buttonGo);
        lv = (ListView) findViewById(R.id.list_friends);




        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, responseList);
        emailEdittext.setAdapter(adapter);
        //set thresold value 1
        emailEdittext.setThreshold(1);
        //set tokenizer that distinguis the various substring by comma
        emailEdittext.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String info=emailEdittext.getAdapter().getItem(position).toString();
                Toast.makeText(AddBillActivity.this,
                        info,
                        Toast.LENGTH_SHORT).show();

                emailEdittext.setText(info);
            }
        });

        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!BillDescription.Is_Valid_Email(emailEdittext)){
                    Toast.makeText(getApplicationContext(), "please enter valid email", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent i=new Intent(AddBillActivity.this,BillDescription.class);
                startActivity(i);
            }
        });



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



    private class asynkParser extends AsyncTask<String, String, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AddBillActivity.this);
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

                        for (int i = 0; i < list.length(); i++) {
                            JSONObject yao = list.getJSONObject(i);

                            String id = yao.getString("id");
                            String value = yao.getString("value");

                            String name=yao.getString("value");
                            responseList.add(name);
                            HashMap<String, String> yaol = new HashMap<>();

                            yaol.put("id", id);
                            yaol.put("value", value);
                            FriendsList.add(yaol);

                        }
                        ListAdapter adapter = new SimpleAdapter(AddBillActivity.this, FriendsList,
                                R.layout.simple_friends_list, new String[]{"id", "value"},
                                new int[]{R.id.id, R.id.value2});
                        lv.setAdapter(adapter);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            };
            try {

                volleyUtility.makeCall(ConstantValues.urlForDashboard, putvalues, sv, getApplicationContext());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing())
                progressDialog.dismiss();


        }
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_add_bill, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {


            switch (item.getItemId()) {

                case android.R.id.home:
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                case R.id.action_save:
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }


        }



}
