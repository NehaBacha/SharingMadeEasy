package com.example.splitwise;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends Activity {

    public EditText editTextEmail, editTextPassword;
    Button buttonLogin,buttonError;
    private static final String TAG_ID="id";
    private static final String TAG_EMAIL="email";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        buttonLogin = (Button) findViewById(R.id.button);
        editTextEmail = (EditText) findViewById(R.id.emailLogin);
        editTextPassword = (EditText) findViewById(R.id.passwordLogin);
        buttonError=findViewById(R.id.button2);



        InitializeEventListners();

    }



    void InitializeEventListners() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!Is_Valid_Email(editTextEmail)) {
                    Toast.makeText(getApplicationContext(), "please enter valid username", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Is_Valid_Password(editTextPassword)) {
                    Toast.makeText(getApplicationContext(), "please enter valid password", Toast.LENGTH_LONG).show();
                    return;
                }
                buttonError.setVisibility(View.INVISIBLE);

                String email=editTextEmail.getText().toString();
                final SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(ConstantValues.KEY_EMAIL,email);
                editor.apply();

                new JSONParse().execute();

            }
        }
        );




        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    Is_Valid_Password(editTextPassword);
            }
        });
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    Is_Valid_Email(editTextEmail);
            }
        });


    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog=new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Processing ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override

        protected JSONObject doInBackground(String... args) {
            VolleyUtility volleyUtility=new VolleyUtility();
            HashMap<String,String> post_values = new HashMap<>();
            post_values.put("email", editTextEmail.getText().toString());
            post_values.put("password", editTextPassword.getText().toString());

            ServerCallback sv = new ServerCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    String ajsonString;
                    try {
                        ajsonString = jsonObject.getString("login_details");
                        if (ajsonString.equals("Valid")) {
                            goToDashBoardActivity();
                        }else {
                            buttonError.setVisibility(View.VISIBLE);
                            editTextEmail.setError("Invalid email");
                            editTextPassword.setError("Invalid password");
                            Toast.makeText(getApplicationContext(),jsonObject.toString(),Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };

            try {

                volleyUtility.makeCall(ConstantValues.urlForLogin,post_values, sv, getApplicationContext());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json){
            pDialog.dismiss();

        }
    }

   /* public  void makeCall(String url,final HashMap<String, String> params1, final Context c) throws JSONException {
        url = "http://10.0.0.63/check_login_details";
        RequestQueue queue = Volley.newRequestQueue(c);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            String ajsonString=jsonObject.getString("login_details");
                            if (ajsonString.equals("Valid")){
                                Intent i=new Intent(LoginActivity.this,Dashboard.class);
                                startActivity(i);
                            }else {
                                editTextEmail.setError("Invalid email");
                                editTextPassword.setError("Invalid password");
                                buttonError.setVisibility(View.VISIBLE);
                                Toast.makeText(c, response, Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                        // Display the first 500 characters of the response string.
                        //

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HttpClient", "error: " + error.toString());
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                //Toast.makeText(c, "Setting params", Toast.LENGTH_LONG).show();
                return params1;
            }

        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        stringRequest.setTag("postRequest");
        queue.add(stringRequest);
    }*/


    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void goToDashBoardActivity() {

        Intent dashBoard = new Intent(this.getApplicationContext(), Dashboard.class);
        startActivity(dashBoard);
    }


    public static boolean isEmailValid(CharSequence editTextEmail) {
        return Patterns.EMAIL_ADDRESS.matcher(editTextEmail).matches();
    }

    public static boolean Is_Valid_Email(EditText edt) {
        if (edt.getText().toString() == null) {
            edt.setError("Email should not be empty");
            return false;
        } else if (isEmailValid(edt.getText().toString()) == false) {
            edt.setError("Invalid Email Address");
            return false;
        } else
            edt.setError(null);
            return true;

    }

    public static boolean Is_Valid_Password(EditText edt) {
        if (edt.getText().toString() == null) {
            edt.setError("Password should not be empty");
            return false;
        } else if (edt.getText().toString().length() < 3) {
            edt.setError("Password should be minimum 3 characters");
            return false;
        } else
            edt.setError(null);
            return true;

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode) {
            case R.id.action_logout:
                setResult(R.id.action_logout);
                closeActivity();            // to close this activity
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void closeActivity() {
        finish();
    }


}


