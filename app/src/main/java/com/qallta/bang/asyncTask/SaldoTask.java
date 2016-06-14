package com.qallta.bang.asyncTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.qallta.bang.LoginActivity;
import com.qallta.bang.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by DANIEL on 24/05/2016.
 */
public class SaldoTask extends AsyncTask<Void, Void, String>  {

    private String mToken;
    private Context mContext;

    public SaldoTask(String token, Context context) {
        this.mToken = token;
        this.mContext = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        String respuesta = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet del = new HttpGet(Util.URL_SERVICE+"getSaldo/"+mToken);
        del.setHeader("content-type", "application/json");
        del.setHeader("default_charset", "UTF-8");
        try {
            HttpResponse httpResponse = httpClient.execute(del);
            String respon = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonObject = new JSONObject(respon);
            respuesta= jsonObject.getString("result");
            if(respuesta.equals("OK")){
                String saldo = jsonObject.getString("saldo");
                SharedPreferences sharedPreferences = Util.preferences;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("saldo", Integer.parseInt(saldo));
                editor.apply();
                Util.preferences = sharedPreferences;
            }
        } catch (Exception e) {
            Log.e("LogoutTask->ERROR LOCAL ", "Error: " + e.getMessage());
            respuesta = "ERROR LOCAL";
        }
        return respuesta;
    }
}
