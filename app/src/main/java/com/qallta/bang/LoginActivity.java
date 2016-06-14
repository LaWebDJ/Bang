package com.qallta.bang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qallta.bang.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Button iniciar;
    TextView registrarse;
    private EditText mUsuario;
    private EditText mPassword;
    private EditText mTelefono;//TEst
    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        iniciar = (Button) findViewById(R.id.btn_registrar1);
        registrarse = (TextView) findViewById(R.id.registrarse);
        mUsuario = (EditText) findViewById(R.id.login1);
        mPassword = (EditText) findViewById(R.id.password1);
        mTelefono = (EditText) findViewById(R.id.telefono);
        iniciar.setOnClickListener(this);
        registrarse.setOnClickListener(this);
        mLoginFormView = findViewById(R.id.login_form1);
        mProgressView = findViewById(R.id.login_progress1);
        preferences = getSharedPreferences("bangPreferences", Context.MODE_PRIVATE);
        String token = preferences.getString("token","");
        if(!token.isEmpty()){
            Util.preferences = preferences;
            Util.TOKEN = token;
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("name",preferences.getString("name",""));
            intent.putExtra("account", preferences.getString("account",""));
            startActivity(intent);
            finish();
        }else{
            cargarPreferencias();
        }


    }

    private void cargarPreferencias(){
        preferences = getSharedPreferences("bangPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token","");
        editor.putInt("saldo", 0);
        editor.putString("name", "");
        editor.putString("account","");
        editor.putString("fecha","");
        editor.apply();
        Util.preferences = preferences;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_registrar1:
                attemptLogin();
                break;
            case R.id.registrarse:
                intent = new Intent(getApplicationContext(),RegistrarseActivity.class);
                startActivity(intent);
                break;
        }
    }

    public boolean isConnected(){
        boolean sw = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            NetworkInfo networkInfo_WIFI = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo networkInfo_MOVIL = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo_WIFI.isConnected() || networkInfo_MOVIL.isConnected()){
                sw = true;
            }
        }
        return sw;
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsuario.setError(null);
        mPassword.setError(null);
        mTelefono.setError(null);
        // Store values at the time of the login attempt.
        String usuario = mUsuario.getText().toString();
        String password = mPassword.getText().toString();
        String telefono = mTelefono.getText().toString();
        boolean cancel = false;
        View focusView = null;
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_required));
            focusView = mPassword;
            cancel = true;
        }else if(password.length() < 6) {
            mPassword.setError(getString(R.string.error_miniCharacters));
            focusView = mPassword;
            cancel = true;
        }
        if (TextUtils.isEmpty(usuario)) {
            mUsuario.setError(getString(R.string.error_required));
            focusView = mUsuario;
            cancel = true;
        }else if(usuario.length() < 6){
            mUsuario.setError(getString(R.string.error_miniCharacters));
            focusView = mUsuario;
            cancel = true;
        }
        if(TextUtils.isEmpty(telefono)){
            mTelefono.setError(getString(R.string.error_required));
            focusView = mTelefono;
            cancel = true;
        }else if(telefono.length() < 8){
            mTelefono.setError(getString(R.string.invalid_phone));
            focusView = mTelefono;
            cancel = true;
        }else if(!TextUtils.isDigitsOnly(telefono)){
            mTelefono.setError(getString(R.string.invalid_phone2));
            focusView = mTelefono;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if(isConnected()){
                showProgress(true);
                mAuthTask = new UserLoginTask(usuario.trim(), password.trim(),telefono);
                mAuthTask.execute((Void) null);
            }else{
                Toast.makeText(getApplicationContext(),"Necesitas una conexion a Internet",Toast.LENGTH_LONG).show();
            }
        }
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




    private class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mlogin;
        private final String mContraseña;
        private final String mCuenta;

        UserLoginTask(String login, String password, String cuenta) {
            mlogin = login;
            mContraseña = password;
            mCuenta = cuenta;
        }

        @Override
        protected String doInBackground(Void... params) {
            String respuesta = "";
            HttpClient httpClient = new DefaultHttpClient();
            String url  = Util.URL_SERVICE+"login-authentication/"+mlogin+"/"+mContraseña+"/"+mCuenta;
            HttpGet del = new HttpGet(Util.URL_SERVICE+"login-authentication/"+mlogin+"/"+mContraseña+"/"+mCuenta);
            del.setHeader("content-type", "application/json");
            del.setHeader("default_charset", "UTF-8");
            try {
                HttpResponse httpResponse = httpClient.execute(del);
                String respon = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = new JSONObject(respon);
                respuesta= jsonObject.getString("result");
                if(respuesta.equals("OK")){
                    String token = jsonObject.getString("token");
                    Util.TOKEN = token;
                    double s = Double.parseDouble(jsonObject.getString("saldo"));
                    int saldo = (int)(s);
                    //int saldo = Integer.parseInt(jsonObject.getString("saldo"));
                    String account = jsonObject.getString("account");
                    String name = jsonObject.getString("name");
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token",token);
                    editor.putInt("saldo", saldo);
                    editor.putString("name", name);
                    editor.putString("account",account);
                    editor.apply();
                    Util.preferences = preferences;
                }
            } catch (Exception e) {
                Log.e("UserLoginTask->ERROR LOCAL ","Error: "+e.getMessage());
                //respuesta = "ERROR LOCAL";
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(final String success) {
            mAuthTask = null;
            showProgress(false);
            if (success.equals("OK")) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                //intent.putExtra("user","algo");
                //intent.putExtra("passw", "desde login");
                startActivity(intent);
                //onDestroy();
                finish();
            } else if(success.equals("SESSION_ACTIVE")){
                Toast.makeText(getApplicationContext(),"Ya existe alguien logueado con la cuenta.",Toast.LENGTH_LONG).show();
            }else if(success.equals("INCORRECT_DATA")){
                Toast.makeText(getApplicationContext(),"Datos de acceso incorrectos.",Toast.LENGTH_LONG).show();
            }else if(success.equals("CONFLICT")){
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }



}
