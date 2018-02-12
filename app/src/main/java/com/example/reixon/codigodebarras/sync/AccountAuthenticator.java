package com.example.reixon.codigodebarras.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.reixon.codigodebarras.ui.Login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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

/**
 * Created by reixon on 27/01/2018.
 */

public class AccountAuthenticator extends AbstractAccountAuthenticator {

    public static final String ACCOUNT_TYPE = "com.example.reixon.codigodebarras";
    private String authToken;

    private Context context;
    public AccountAuthenticator(Context cont) {
        super(cont);
        context = cont;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(context, Login.class);
        intent.putExtra(ACCOUNT_TYPE, accountType);
        intent.putExtra(Login.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        final AccountManager am = AccountManager.get(context);
        authToken = am.peekAuthToken(account,authTokenType);
        if(TextUtils.isEmpty(authToken)){
            final String password = am.getPassword(account);
            if(password!=null){
               LogUser mAuthTask = new LogUser(account.name, password);
                try {
                    authToken= mAuthTask.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        if(!TextUtils.isEmpty(authToken)){
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME,account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE,account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }
    /*    final Intent intent = new Intent(context, Login.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(Login.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(Login.ARG_AUTH_TYPE, authTokenType);*/

     /*   final Bundle bundle= new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT,intent);*/

        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean("Autentificacion",false);

        return result;
    }

    public class LogUser extends AsyncTask<Void, Void, String> {
        private String mName, mPassword;

        public LogUser(String name, String password) {
            mName=name;
            mPassword=password;
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection client = null;

            BufferedReader reader=null;
            try {
                URL url = new URL("http://webserviceslistshop.ddns.net/Android/Web_Service_List_Shop/log_user.php");
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
                params.add(new BasicNameValuePair("email", mName));
                params.add(new BasicNameValuePair("pass", mPassword));
                OutputStream out = client.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                out.close();
                int response_code = client.getResponseCode();
                if(response_code == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return result.toString();
                }

            } catch(Exception e1){
                e1.printStackTrace();

            }
            finally {

                try {
                    reader.close();
                    client.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
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
}
