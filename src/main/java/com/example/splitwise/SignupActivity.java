package com.example.splitwise;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignupActivity extends Activity {

    private EditText editTextName,editTextEmail,editTextPhone,editTextPassword,editTextConfirmPassword;
    private Button buttonSignup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        buttonSignup=(Button)findViewById(R.id.button_signup);
        editTextName=findViewById(R.id.editText_Name);
        editTextEmail=findViewById(R.id.editText_email);
        editTextPassword=findViewById(R.id.editText_password);
        editTextConfirmPassword=findViewById(R.id.editText_ConfirmPassword);
        editTextPhone=findViewById(R.id.editText_phone);

        InitializeEventListner();


    }

    void InitializeEventListner(){
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!LoginActivity.Is_Valid_Email(editTextEmail)){
                    Toast.makeText(getApplicationContext(), "please enter valid email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!LoginActivity.Is_Valid_Password(editTextPassword)) {
                    Toast.makeText(getApplicationContext(), "please enter valid password", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!Password_Confirmation(editTextPassword,editTextConfirmPassword)){
                    Toast.makeText(getApplicationContext(),"Password should match",Toast.LENGTH_LONG).show();
                    return;
                }
                if((Is_Phone_Valid(editTextPhone))==false){
                    Toast.makeText(getApplicationContext(),"Enter valid phone number",Toast.LENGTH_LONG).show();
                    return;
                }
                new JSONParse().execute();
            }
        });
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog=new ProgressDialog(SignupActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override

        protected JSONObject doInBackground(final String... args) {
            VolleyUtility volleyUtility=new VolleyUtility();

            HashMap<String,String> putValues=new HashMap<String, String>();
            putValues.put("name",editTextName.getText().toString());
            putValues.put("email",editTextEmail.getText().toString());
            putValues.put("password",editTextPassword.getText().toString());
            putValues.put("phone",editTextPhone.getText().toString());

            ServerCallback sv=new ServerCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    //String aString=null;
                    try {
                        String aString=jsonObject.getString("request");
                        if(aString.equals("successful")){
                            gotoDashboardActivity();
                        }else{
                            Toast.makeText(getApplicationContext(),"account already exist with this emailid.",Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            try {
                volleyUtility.makeCall(ConstantValues.urlForNewUser,putValues,sv,getApplicationContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject json){
            pDialog.dismiss();

        }
    }


    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
    public void gotoDashboardActivity(){
        Intent i=new Intent(SignupActivity.this,Dashboard.class);
        startActivity(i);
    }

    boolean isValidPhone(CharSequence charSequence){
        return Patterns.PHONE.matcher(charSequence).matches();
    }
    public boolean Is_Phone_Valid(EditText editText){
        if(editText.length()==10 && (isValidPhone(editText.getText().toString())==true)){
            return true;
        }else {
            return false;
        }
    }

    public boolean Password_Confirmation(EditText editText,EditText editText1){
        if(!(editText.getText().toString().equals(editText1.getText().toString()))) {
            return false;
        }else
        return true;
    }

}
