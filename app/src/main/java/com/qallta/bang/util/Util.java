package com.qallta.bang.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.qallta.bang.RecyclerView.Transferencia;

import java.util.ArrayList;

/**
 * Created by DANIEL on 13/05/2016.
 */
public class Util{

    //public static final String IP = "192.168.0.4";
    public static final String IP = "54.88.96.218";//esta linea de codigo subir
    public static final String URL_SERVICE = "http://" + IP + ":8080/ServiceBang/rest/1/";
    public static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static String TOKEN = "";
    public static SharedPreferences preferences;
    public static ArrayList<Transferencia> datos;
    public static ConnectivityManager  connectivityManager;
    public static NavigationView navigationView;
    public static View view;


}
