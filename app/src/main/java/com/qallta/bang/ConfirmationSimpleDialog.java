package com.qallta.bang;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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
public class ConfirmationSimpleDialog extends DialogFragment {


    private String mTelefono;
    private String mMonto;
    private String mOperador;
    private String message;
    private String title;
    private Context context;
    private FragmentManager fragmentManager;


    public ConfirmationSimpleDialog() {
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
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listener.onPossitiveButtonClick();
                                if (isConnected()) {
                                    RecargaTask recargaTask = new RecargaTask(Util.TOKEN, mTelefono, mMonto, mOperador);
                                    recargaTask.execute();
                                } else {
                                    Toast.makeText(getContext(), "Necesitas una conexion a Internet", Toast.LENGTH_LONG).show();
                                }

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

    private boolean isConnected(){
        boolean sw = false;
        if(Util.connectivityManager != null){
            NetworkInfo networkInfo_WIFI = Util.connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo networkInfo_MOVIL = Util.connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo_WIFI.isConnected() || networkInfo_MOVIL.isConnected()){
                sw = true;
            }
        }
        return sw;
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



    public String getmTelefono() {
        return mTelefono;
    }

    public void setmTelefono(String mTelefono) {this.mTelefono = mTelefono;}

    public String getmMonto() {return mMonto;}

    public void setmMonto(String mMonto) {this.mMonto = mMonto;}

    public String getmOperador() {
        return mOperador;
    }

    public void setmOperador(String mOperador) {
        this.mOperador = mOperador;
    }

    public String getMessage() {return message;}

    public void setMessage(String message) {this.message = message;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }





    private class RecargaTask extends AsyncTask<Void, Void, String> {

        private final String mToken;
        private final String mTelefono;
        private final String mMonto;
        private final String mOperador;
        private int saldo;


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
                    saldo = (int)(s);
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
                //Toast.makeText(context, "Recarga enviada.", Toast.LENGTH_LONG).show();
                ConfirmationSimpleDialog simpleDialog = new ConfirmationSimpleDialog();
                simpleDialog.setmTelefono(mTelefono);
                simpleDialog.setmMonto(mMonto);
                simpleDialog.setmOperador(mOperador);
                simpleDialog.setTitle("Confirmación");
                simpleDialog.setMessage("Se envio " + mMonto + "Bs. al " + mTelefono + ". Su saldo actual es " + saldo + " Bs.");
                simpleDialog.setFragmentManager(fragmentManager);
                simpleDialog.setContext(context);
                simpleDialog.show(fragmentManager, "SimpleDialog");
            } else if(success.equals("UNKNOWN_ACCOUNT")){
                Toast.makeText(context, "Error.", Toast.LENGTH_LONG).show();
            } else if(success.equals("SIN_SALDO")){
                Toast.makeText(context, "Su cuenta no tiene saldo suficiente", Toast.LENGTH_LONG).show();
            }
            MainActivity mainActivity = new MainActivity();
            mainActivity.setHeaderView();
        }

    }




}

