package com.qallta.bang.util;

import android.content.SharedPreferences;

import com.qallta.bang.RecyclerView.Transferencia;

import java.util.ArrayList;

/**
 * Created by DANIEL on 13/05/2016.
 */
public class Util {

    //public static final String IP = "192.168.1.4";
    public static final String IP = "54.175.110.6";
    public static final String URL_SERVICE = "http://"+IP+":8080/ServiceBang/rest/1/";
    //public static final String URL_SERVICE = "http://"+IP+":8080/PayPlan/rest/members/";
    public static String TOKEN = "";
    public static SharedPreferences preferences;
    public static ArrayList<Transferencia> datos;

}
