package com.qallta.bang;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.qallta.bang.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Fragmento con diálogo básico
 */
public class SimpleDialog extends DialogFragment {

    private String mTelefono;
    private String mMonto;
    private String mOperador;
    private Context context;

    public SimpleDialog(String telefono, String monto,String mOperador,Context context) {
        this.mTelefono = telefono;
        this.mMonto = monto;
        this.mOperador = mOperador;
        this.context = context;
    }

    public interface OnSimpleDialogListener {
        void onPossitiveButtonClick();
        void onNegativeButtonClick();
    }

    // Interfaz de comunicación
    OnSimpleDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createSimpleDialog();
    }

    /**
     * Crea un diálogo de alerta sencillo
     * @return Nuevo diálogo
     */
    public AlertDialog createSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Recarga")
                .setMessage("¿Desea enviar "+mMonto+" Bs. al "+mTelefono+"?")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listener.onPossitiveButtonClick();
                                RecargaTask recargaTask = new RecargaTask(Util.TOKEN,mTelefono,mMonto,mOperador);
                                recargaTask.execute();
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onNegativeButtonClick();
                            }
                        });

        return builder.create();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (OnSimpleDialogListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() +
                            " no implementó OnSimpleDialogListener");

        }
    }




    private class RecargaTask extends AsyncTask<Void, Void, String> {

        private final String mToken;
        private final String mTelefono;
        private final String mMonto;
        private final String mOperador;


        public RecargaTask(String mToken, String mTelefono, String mMonto,String mOperador) {
            this.mToken = mToken;
            this.mTelefono = mTelefono;
            this.mMonto = mMonto;
            this.mOperador = mOperador;
        }

        @Override
        protected String doInBackground(Void... params) {
            String respuesta = "";
            HttpClient httpClient = new DefaultHttpClient();
            String url  = Util.URL_SERVICE+"recarga/"+mOperador+"/"+mTelefono+"/"+mMonto+"/"+mToken;
            HttpGet del = new HttpGet(Util.URL_SERVICE+"recarga/"+mOperador+"/"+mTelefono+"/"+mMonto+"/"+mToken);
            del.setHeader("content-type", "application/json");
            del.setHeader("default_charset", "UTF-8");
            try {
                HttpResponse httpResponse = httpClient.execute(del);
                String respon = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = new JSONObject(respon);
                respuesta= jsonObject.getString("result");
                if(respuesta.equals("OK")){
                    double s = Double.parseDouble(jsonObject.getString("saldo"));
                    int saldo = (int)(s);
                    SharedPreferences sharedPreferences = Util.preferences;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("saldo", saldo);
                    editor.apply();
                    Util.preferences = sharedPreferences;
                }
            } catch (Exception e) {
                Log.e("LogoutTask->ERROR LOCAL ", "Error: " + e.getMessage());
                //respuesta = "ERROR LOCAL";
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(String success) {
            if (success.equals("OK")) {
                Toast.makeText(context, "Recarga enviada.", Toast.LENGTH_LONG).show();
            } else if(success.equals("UNKNOWN_ACCOUNT")){
                Toast.makeText(context, "Error.", Toast.LENGTH_LONG).show();
            } else if(success.equals("SIN_SALDO")){
                Toast.makeText(context, "Su cuenta no tiene saldo suficiente", Toast.LENGTH_LONG).show();
            }
        }

    }

}

