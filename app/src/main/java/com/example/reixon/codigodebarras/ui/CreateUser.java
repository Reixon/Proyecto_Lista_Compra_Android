package com.example.reixon.codigodebarras.ui;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reixon.codigodebarras.R;
import com.example.reixon.codigodebarras.db.MySQL;
import com.example.reixon.codigodebarras.sync.AccountAuthenticator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class CreateUser extends AccountAuthenticatorActivity {


    private CheckUser mAuthTask = null;
    private EditText mNameView, mPasswordView, mEmailView;
    private View mProgressView;
    private View mLoginFormView;
    private AccountManager mAccountManager;

    public static final String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String ARG_AUTH_TYPE = "AUTH_TYPE_CODIGO_BARRAS";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String PARAM_USER_PASS = "USER_PASS";
    public static final String PARAM_USER_NAME ="USER_NAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        mNameView = (EditText)findViewById(R.id.name_register);
        mEmailView = (EditText)findViewById(R.id.email_register);
        mPasswordView = (EditText)findViewById(R.id.password_register);
        Button bt = (Button)findViewById(R.id.btnRegister);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress_register);
        mAccountManager = AccountManager.get(this);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempRegister();

            }
        });
    }

    private void attempRegister() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mNameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(name) ){
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password, if the result entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        /*else if(mysql.searchUserEmail(email,db)){
            mEmailView.setError(getString(R.string.error_this_account_exist));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the result login attempt.
            mAuthTask = new CheckUser(this.mEmailView.getText().toString(),this.mNameView.getText().toString());
            try {
                if(mAuthTask.execute((Void) null).get()){
                    RegisterUser registerUser = new RegisterUser(this.mNameView.getText().toString(), mEmailView.getText().toString(),mPasswordView.getText().toString());
                    registerUser.execute((Void)null);
                }
                else{
                    Toast.makeText(this,getString(R.string.error_this_account_exist),Toast.LENGTH_LONG).show();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    //comprobar que no exista ni email ni nombre en la bd externa
    public class CheckUser extends AsyncTask<Void, String, Boolean> {

        private final String mEmail;
        private final String mName;
        private String result;

        CheckUser(String email, String name) {
            mEmail = email;
            mName = name;
        }

        protected void onPreExecute(){
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... param) {
            // TODO: attempt authentication against a network service.
            HttpURLConnection client = null;
            Boolean b=false;
            BufferedReader reader=null;

            try {

                URL url = new URL("http://webserviceslistshop.ddns.net/Android/Web_Service_List_Shop/check_usuario.php");
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setRequestProperty("User-Agent","Mozilla/5.0");
                client.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                client.setRequestProperty("Accept-Charset","UTF-8,*");
                client.setReadTimeout(5000);
                client.setConnectTimeout(5000);
                client.setDoOutput(true);
                client.setDoInput(true);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", mEmail));
                params.add(new BasicNameValuePair("name", mName));

                OutputStream out = client.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                out.close();

                int response_code = client.getResponseCode();
                if(response_code == HttpURLConnection.HTTP_OK){
                    InputStream input = client.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        result.append(line);
                    }
                    this.result = result.toString();
                    if(this.result.equals(" 0")){
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                else{
                    return false;
                }
            }catch (IOException e){
                e.printStackTrace();
                return false;
            }
            finally {
                try {
                    reader.close();
                }catch (Exception e){}
                client.disconnect();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        @Override
        protected void onPostExecute(Boolean success){
            showProgress(false);
            mAuthTask = null;
        }
    }




    //Hilo de registro
    public class RegisterUser extends AsyncTask<Void, String, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mName;
        private String result;

        RegisterUser(String name, String email, String password) {
            mEmail = email;
            mPassword = password;
            mName = name;
        }

        protected void onPreExecute(){
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected String doInBackground(Void... param) {
            // TODO: attempt authentication against a network service.
            HttpURLConnection client = null;
            Boolean b=false;
            BufferedReader reader=null;

            try {

                URL url = new URL("http://webserviceslistshop.ddns.net/Android/Web_Service_List_Shop/insertar_usuario.php");
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setRequestProperty("User-Agent","Mozilla/5.0");
                client.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                client.setRequestProperty("Accept-Charset","UTF-8,*");
                client.setReadTimeout(5000);
                client.setConnectTimeout(5000);
                client.setDoOutput(true);
                client.setDoInput(true);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", mEmail));
                params.add(new BasicNameValuePair("pass", mPassword));
                params.add(new BasicNameValuePair("name",mName));

                OutputStream out = client.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                out.close();

                int response_code = client.getResponseCode();
                if(response_code == HttpURLConnection.HTTP_OK){
                    InputStream input = client.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        result.append(line);
                    }
                    this.result = result.toString();
                    if(!this.result.equals(" ")){
                        return "true";
                    }
                    else {
                        return "false";
                    }
                }
                else{
                    return "false";
                }


            }catch (IOException e){
                e.printStackTrace();
                return "exception";
            }
            finally {
                try {
                    reader.close();
                }catch (Exception e){}
                client.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String success) {
            mAuthTask = null;
            showProgress(false);
            if (success.contentEquals("true")) {
                String[] userParameters = this.result.split(" ");

                final Intent res = new Intent();
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userParameters[3]);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountAuthenticator.ACCOUNT_TYPE);
                res.putExtra(AccountManager.KEY_AUTHTOKEN, userParameters[2]);
                res.putExtra(PARAM_USER_PASS, userParameters[1]);
                res.putExtra(PARAM_USER_NAME, userParameters[0]);

                /*Bundle userData = new Bundle();
                userData.putString(USERDATA_USER_OBJ_ID, user.getObjectId());
                data.putBundle(AccountManager.KEY_USERDATA, userData);

                data.putString(PARAM_USER_PASS, userPass);*/

                finishLog(res);
            } else if (success.contentEquals("false")){
                Toast.makeText(CreateUser.this,"Incorrecto",Toast.LENGTH_SHORT).show();
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
            else if(success.contentEquals("exception")){
                Toast.makeText(CreateUser.this,"ocurrio un error",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    public void finishLog(Intent i){
        String accountName = i.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String pass = i.getStringExtra(PARAM_USER_PASS);
        String name = i.getStringExtra(PARAM_USER_NAME);

        final Account account = new Account(accountName,i.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        String authToken = i.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String authTokenType = LoginActivity.ARG_AUTH_TYPE;
        mAccountManager.addAccountExplicitly(account,pass,null);
        mAccountManager.setAuthToken(account,authTokenType,authToken);

        MySQL mysql = new MySQL(this);
        SQLiteDatabase db = mysql.getWritableDatabase();
        mysql.insertUser(name, accountName, authToken, db);

        Toast.makeText(CreateUser.this,"Bienvenido "+
                accountName,Toast.LENGTH_SHORT).show();
        setAccountAuthenticatorResult(i.getExtras());
        Intent intent = new Intent(getBaseContext(), Lista_compra.class);
        setResult(RESULT_OK, intent);
        startActivity(intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
