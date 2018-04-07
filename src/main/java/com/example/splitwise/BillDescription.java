package com.example.splitwise;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class BillDescription extends AppCompatActivity{

    private EditText EdittextBillTitle,EdittextDescription,EdittextAmount,EdittextPayerEmail,EdittextDate;
    private Button buttonSave;
    private String email;
    Calendar mycalender= Calendar.getInstance();
    String dateFormat="yyyy-mm-dd";
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat sdf=new SimpleDateFormat(dateFormat, Locale.US);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        EdittextBillTitle=(EditText)findViewById(R.id.bill_title);
        EdittextDescription=(EditText)findViewById(R.id.description);
        EdittextAmount=(EditText)findViewById(R.id.amount);
        EdittextPayerEmail=(EditText)findViewById(R.id.payer_email);
        EdittextDate=(EditText)findViewById(R.id.date);

        buttonSave=(Button)findViewById(R.id.save);
        EdittextDate.setKeyListener(null);
        long currentdate=System.currentTimeMillis();
        String dataString=sdf.format(currentdate);
        EdittextDate.setText(dataString);
        date=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthofYear, int dayOfMonth) {
                mycalender.set(Calendar.YEAR,year);
                mycalender.set(Calendar.MONTH,monthofYear);
                mycalender.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateDate();
            }
        };

        EdittextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(BillDescription.this,date,mycalender.get(Calendar.YEAR),
                        mycalender.get(Calendar.MONTH),mycalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        InitializeEventListners();

    }

    void InitializeEventListners(){

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Is_Valid_Email(EdittextPayerEmail)) {
                    Toast.makeText(getApplicationContext(), "please enter valid username", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Is_Valid_Billtitle(EdittextBillTitle)) {
                    Toast.makeText(getApplicationContext(), "please enter Bill Title", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Is_Valid_BillDesc(EdittextDescription)) {
                    Toast.makeText(getApplicationContext(), "please enter Bill Description", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Is_Valid_Amount(EdittextAmount)) {
                    Toast.makeText(getApplicationContext(), "please enter amount", Toast.LENGTH_LONG).show();
                    return;
                }





                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BillDescription.this);
                email=sharedPreferences.getString(ConstantValues.KEY_EMAIL,"");
                if (email!=null) {
                    Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();
                }

                new asynkParser().execute();
                Intent i=new Intent(BillDescription.this,Dashboard.class);
                startActivity(i);

            }
        });

    }




    private class asynkParser extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog=new ProgressDialog(BillDescription.this);
            pDialog.setMessage("Processing ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override

        protected JSONObject doInBackground(String... args) {
            VolleyUtility volleyUtility=new VolleyUtility();

            final HashMap<String, String> putvalues = new HashMap<String, String>();
            putvalues.put("email", email);
            putvalues.put("bill_title",EdittextBillTitle.getText().toString());
            putvalues.put("desc",EdittextDescription.getText().toString());
            putvalues.put("Number",EdittextAmount.getText().toString());
            putvalues.put("date",EdittextDate.getText().toString());
            putvalues.put("payer_email",EdittextPayerEmail.getText().toString());


            ServerCallback sv = new ServerCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    String ajsonString;

                }
            };

            try {

                volleyUtility.makeCall(ConstantValues.urlForAddaBill,putvalues, sv, getApplicationContext());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json){
            pDialog.dismiss();

        }
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

    public static boolean Is_Valid_Billtitle(EditText edt) {
        if (edt.getText().toString() == null) {
            edt.setError("Bill Title should not be empty");
            return false;
        } else
            edt.setError(null);
        return true;
    }
    public static boolean Is_Valid_BillDesc(EditText edt) {
        if (edt.getText().toString() == null) {
            edt.setError("Bill Description should not be empty");
            return false;
        } else
            edt.setError(null);
        return true;
    }
    public static boolean Is_Valid_Amount(EditText edt) {
        if (edt.getText().toString() == null) {
            edt.setError("Enter the amount");
            return false;
        } else
            edt.setError(null);
        return true;
    }
    private void updateDate() {
        EdittextDate.setText(sdf.format(mycalender.getTime()));
    }


}
