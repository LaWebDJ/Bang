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

}

