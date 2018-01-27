package com.example.reixon.codigodebarras.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reixon.codigodebarras.R;

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

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class CreateUser extends AppCompatActivity {


    private RegisterUser mAuthTask = null;
    private EditText mNameView, mPasswordView, mEmailView;
    private View mProgressView;
    private View mLoginFormView;

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

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();

            }
        });
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
            showProgress(true);
            mAuthTask = new RegisterUser(this.mNameView.getText().toString(), mEmailView.getText().toString(),mPasswordView.getText().toString());
            mAuthTask.execute((Void) null);
    }

    public class RegisterUser extends AsyncTask<Void, String, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mName;


        RegisterUser(String name, String email, String password) {
            mEmail = email;
            mPassword = password;
            mName = name;
        }

        protected void onPreExecute(){
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... param) {
            // TODO: attempt authentication against a network service.
            HttpURLConnection client = null;
            Boolean b=false;
            String text="";
            BufferedReader reader=null;
            try {

                URL url = new URL("http://192.168.1.36:8080/Android/Web_Service_List_Shop/insertar_usuario.php");
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                //client.setReadTimeout(10000);
                //  client.setConnectTimeout(15000);
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
                client.connect();

            } catch(Exception e1){
                e1.printStackTrace();
            }

            try{
                int response_code = client.getResponseCode();
                if(response_code == HttpURLConnection.HTTP_OK){
                    InputStream input = client.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        result.append(line);
                    }
                    text = " true";
                }

            }catch (IOException e){
                e.printStackTrace();
                text="exception";
            }
            finally {
                try {
                    reader.close();
                }catch (Exception e){}
                client.disconnect();
            }

           return text.toString();
        }

        @Override
        protected void onPostExecute(String success) {
            mAuthTask = null;
            showProgress(false);
            finish();
            if (success.contentEquals(" true")) {
                Toast.makeText(CreateUser.this,"Usuario correcto",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getBaseContext(), lista_compra.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            } else if (success.contentEquals(" false")){
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
