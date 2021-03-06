package com.qallta.bang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qallta.bang.dialog.CodeDialog;
import com.qallta.bang.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrarseActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mCI;
    private EditText mNombre;
    private EditText mApellido;
    private EditText mDireccion;
    private EditText mTelefono;
    private EditText mCorreo;
    private EditText mUsuario;
    private EditText mPassword;
    private EditText mRepeatPassword;
    private Button registrar;

    FragmentManager fragmentManager;
    private String TAG;
    private View mProgressView;
    private View mLoginFormView;

    private SharedPreferences sharedPreferences;
    private ConnectivityManager  connectivityManager;


    private RegistrarseTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_registrarse);
        mCI = (EditText)findViewById(R.id.ci);
        mNombre = (EditText) findViewById(R.id.nombre);
        mApellido = (EditText) findViewById(R.id.apellido);
        mDireccion = (EditText) findViewById(R.id.direccion);
        mTelefono = (EditText) findViewById(R.id.telefono);
        mCorreo = (EditText) findViewById(R.id.email);
        mUsuario = (EditText) findViewById(R.id.user);
        mPassword = (EditText) findViewById(R.id.password);
        mRepeatPassword = (EditText) findViewById(R.id.repeat_password);
        registrar = (Button)findViewById(R.id.btn_registrar);
        registrar.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_registrar:
                attempRegistrar();
                break;
        }
    }


    private void attempRegistrar() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mCI.setError(null);
        mNombre.setError(null);
        mApellido.setError(null);
        mDireccion.setError(null);
        mTelefono.setError(null);
        mCorreo.setError(null);
        mUsuario.setError(null);
        mPassword.setError(null);
        mRepeatPassword.setError(null);
        String ci = mCI.getText().toString();
        String nombre = mNombre.getText().toString();
        String apellido = mApellido.getText().toString();
        String direccion = mDireccion.getText().toString();
        direccion = direccion.replace("/","%");
        String telefono = mTelefono.getText().toString();
        String correo = mCorreo.getText().toString();
        String usuario = mUsuario.getText().toString();
        String password = mPassword.getText().toString();
        String repetPassword = mRepeatPassword.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if(TextUtils.isEmpty(ci)){
            mCI.setError(getString(R.string.error_required));
            focusView = mCI;
            cancel = true;
        }else if(ci.length() < 7) {
            mCI.setError(getString(R.string.invalid_ci));
            focusView = mCI;
            cancel = true;
        }else if(!TextUtils.isDigitsOnly(ci)){
            mCI.setError(getString(R.string.invalid_phone2));
            focusView = mCI;
            cancel = true;
        }
        if(TextUtils.isEmpty(nombre)){
            mNombre.setError(getString(R.string.error_required));
            focusView = mTelefono;
            cancel = true;
        }
        if(TextUtils.isEmpty(apellido)){
            mApellido.setError(getString(R.string.error_required));
            focusView = mTelefono;
            cancel = true;
        }
        if(TextUtils.isEmpty(direccion)){
            mDireccion.setError(getString(R.string.error_required));
            focusView = mDireccion;
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
        if(TextUtils.isEmpty(correo)){
            mCorreo.setError(getString(R.string.error_required));
            focusView = mCorreo;
            cancel = true;
        }else if (!validateEmail(correo)){
            mCorreo.setError(getString(R.string.invalid_email));
            focusView = mCorreo;
            cancel = true;
        }
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
        if(TextUtils.isEmpty(repetPassword)){
            mRepeatPassword.setError(getString(R.string.error_required));
            focusView = mRepeatPassword;
            cancel = true;
        }else if(!password.equals(repetPassword)) {
            mRepeatPassword.setError(getString(R.string.different_passwords));
            focusView = mRepeatPassword;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            if(isConnected()){
                showProgress(true);
                mAuthTask = new RegistrarseTask(ci,nombre.trim(),apellido.trim(),direccion.trim(),telefono,correo.trim(),usuario.trim(),password.trim());
                mAuthTask.execute((Void) null);
            }else{
                Toast.makeText(getApplicationContext(),"Necesitas una conexion a Internet",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void verificar(String direccino){

    }

    public static boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(Util.PATTERN_EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isConnected(){
        boolean sw = false;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Util.connectivityManager = connectivityManager;
        if(connectivityManager != null){
            NetworkInfo networkInfo_WIFI = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo networkInfo_MOVIL = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo_WIFI.isConnected() || networkInfo_MOVIL.isConnected()){
                sw = true;
            }
        }
        return sw;
    }



    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    /**
     * Muestra la interfaz de usuario de progreso y oculta el formulario de acceso.
     */
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


    /**
     * Tarea asincrona para la autenticacion del registro
     */
    public class RegistrarseTask extends AsyncTask<Void, Void, String> {

        private final String ci;
        private final String nombre;
        private final String apellido;
        private String direccion;
        private final String telefono;
        private final String correo;
        private final String usuario;
        private final String contraseña;

        RegistrarseTask(String ci, String nombre, String apellido,String direccion,String telefono, String correo, String usuario, String contraseña) {
            this.ci = ci;
            this.nombre = nombre.replace(" ","%20");
            this.apellido = apellido.replace(" ","%20");;
            String direccion1 = direccion;
            try {
                this.direccion = URLEncoder.encode(direccion1, "UTF-8");
                this.direccion = this.direccion.replace("+","%20");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            this.telefono = telefono;
            this.correo = correo;
            this.usuario = usuario;
            this.contraseña = contraseña;
        }

        @Override
        protected String doInBackground(Void... params) {

            String respuesta = "";
            HttpClient httpClient = new DefaultHttpClient();
            String url  = Util.URL_SERVICE+"registrarse/"+ci+"/"+nombre+"/"+apellido+"/"+direccion+"/"+telefono+"/"+correo+"/"+usuario+"/"+contraseña;
            HttpGet del = new HttpGet(Util.URL_SERVICE+"registrarse/"+ci+"/"+nombre+"/"+apellido+"/"+direccion+"/"+telefono+"/"+correo+"/"+usuario+"/"+contraseña);
            del.setHeader("content-type", "application/json");
            del.setHeader("default_charset", "UTF-8");
            try {
                HttpResponse httpResponse = httpClient.execute(del);
                String respon = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = new JSONObject(respon);
                respuesta= jsonObject.getString("result");
                if(respuesta.equals("OK")){
                    String token = jsonObject.getString("token");
                    String name = jsonObject.getString("name");
                    Util.TOKEN = token;
                    sharedPreferences = Util.preferences;
                    if(sharedPreferences != null){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token",Util.TOKEN);
                        editor.putInt("saldo", 0);
                        editor.putString("name", name);
                        editor.putString("account",telefono);
                        editor.apply();
                        Util.preferences = sharedPreferences;
                        Log.v("daniel", "commit preferences");
                    }
                    /*
                    preferences = getSharedPreferences("bangPreferences", Context.MODE_PRIVATE);
                    preferences.getString("token", Util.TOKEN);
                    preferences.getInt("saldo", 0);
                    preferences.getString("name", name);
                    preferences.getString("account",telefono);
                    Util.preferences = preferences;
                    */
                    //Toast.makeText(getApplicationContext(),preferences.getString("name","default"),Toast.LENGTH_LONG).show();
                }else{
                    return respuesta;
                }
            } catch (Exception e) {
                Log.e("UserLoginTask->ERROR LOCAL ", "Error: " + e.getMessage());
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(String success) {
            mAuthTask = null;
            showProgress(false);
            if (success.equals("OK")) {
                TAG = "CodeDialog";
                new CodeDialog().show(fragmentManager,"CodeDialog");
                    //finish();
            } else if(success.equals("EXIST_USER")){
                mUsuario.setError(getString(R.string.usuario_ocupado));
                mUsuario.requestFocus();
            }else if(success.equals("CONFLICT")){
                mCI.setError(getString(R.string.error));
                mCI.requestFocus();
            }else if(success.equals("EXISTS_ACCOUNT")){
                mTelefono.setError(getString(R.string.phone_invalid));
                mTelefono.requestFocus();
            }else if(success.equals("REGISTRADO")){
                Toast.makeText(getApplicationContext(),"Señor Usuario usted ya se encuentra registrado.",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


}
