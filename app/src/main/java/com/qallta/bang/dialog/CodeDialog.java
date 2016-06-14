package com.qallta.bang.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.qallta.bang.MainActivity;
import com.qallta.bang.R;
import com.qallta.bang.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

/**
 * Fragmento con un diálogo personalizado
 */
public class CodeDialog extends DialogFragment {
    private static final String TAG = CodeDialog.class.getSimpleName();

    TextView timer;
    Button enviar;
    EditText txtCode;
    Contador contador;
    String code;
    SharedPreferences preferences;

    public CodeDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createLoginDialogo();
    }

/*
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.dialog_code,container,true);
        //getDialog().setCancelable(false);
        setCancelable(false);
        return view;
    }
*/

    /**
     * Crea un diálogo con personalizado para comportarse
     * como formulario de login
     *
     * @return Diálogo
     */
    public AlertDialog createLoginDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_code, null);
        setCancelable(false);

        builder.setView(v);
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        preferences = Util.preferences;
        enviar = (Button) v.findViewById(R.id.entrar_boton);
        timer = (TextView) v.findViewById(R.id.info_text);
        txtCode = (EditText) v.findViewById(R.id.nombre_input);
        contador = new Contador(300000,1000);
        contador.start();
        enviar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        code = txtCode.getText().toString();
                        if(code.length() > 0){
                            if (isConnected()){
                                VerificarTask verificarTask = new VerificarTask();
                                verificarTask.execute();
                            }else{
                                Toast.makeText(getContext(),"Necesitas una conexion a Internet.",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(getContext(),"Ingrese el codigo de verificacion.",Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        return alert;
        //return builder.create();
    }

    public boolean isConnected(){
        boolean sw = false;
        ConnectivityManager connectivityManager = Util.connectivityManager;
        if(connectivityManager != null){
            NetworkInfo networkInfo_WIFI = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo networkInfo_MOVIL = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo_WIFI.isConnected() || networkInfo_MOVIL.isConnected()){
                sw = true;
            }
        }
        return sw;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDestroy() {super.onDestroy();contador.cancel();}

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public class Contador extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public Contador(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String tiempo = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis)- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis)- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            //System.out.println(tiempo);
            timer.setText(tiempo);
        }

        @Override
        public void onFinish() {
            timer.setText("Intente de nuevo.");
            setCancelable(true);
            getDialog().setCanceledOnTouchOutside(true);
        }
    }





    private class VerificarTask extends AsyncTask<Void, Void, String> {


        VerificarTask() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String respuesta = "";
            HttpClient httpClient = new DefaultHttpClient();
            String url  = Util.URL_SERVICE+"verificacion/"+code;
            HttpGet del = new HttpGet(Util.URL_SERVICE+"verificacion/"+code);
            del.setHeader("content-type", "application/json");
            del.setHeader("default_charset", "UTF-8");
            try {
                HttpResponse httpResponse = httpClient.execute(del);
                String respon = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = new JSONObject(respon);
                respuesta= jsonObject.getString("result");
                if(respuesta.equals("OK")){
                    String token = jsonObject.getString("token");
                    int saldo = Integer.parseInt(jsonObject.getString("saldo"));
                    String account = jsonObject.getString("account");
                    String name = jsonObject.getString("name");
                    Util.TOKEN = token;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token",token);
                    editor.putInt("saldo", saldo);
                    editor.putString("name", name);
                    editor.putString("account",account);
                    editor.apply();
                    Util.preferences = preferences;
                }
            } catch (Exception e) {
                Log.e("UserLoginTask->ERROR LOCAL ", "Error: " + e.getMessage());
                //respuesta = "ERROR LOCAL";
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(final String success) {

            //showProgress(false);
            if (success.equals("OK")) {
                Intent intent = new Intent(getContext(),MainActivity.class);
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(componentName);

                startActivity(mainIntent);
                dismiss();
            }else if(success.equals("UNKNOWN_CODE")){
                Toast.makeText(getContext(),"Codigo de verificacion incorrecto.",Toast.LENGTH_SHORT).show();
                contador.cancel();
                dismiss();
            }else if(success.equals("CONFLICT")){
                Toast.makeText(getContext(),"System Error",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }


    }
}

