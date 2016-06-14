package com.qallta.bang.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.qallta.bang.MainActivity;
import com.qallta.bang.R;
import com.qallta.bang.RecyclerView.AdaptadorTitulares;
import com.qallta.bang.RecyclerView.DividerItemDecoration;
import com.qallta.bang.RecyclerView.Titular;
import com.qallta.bang.RecyclerView.Transferencia;
import com.qallta.bang.model.Transfer;
import com.qallta.bang.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReporteFragment extends Fragment {


    private RecyclerView lstLista;


    public static ReporteFragment newInstance(Bundle arguments){
        ReporteFragment reporteFragment = new ReporteFragment();
        if (arguments != null){
            reporteFragment.setArguments(arguments);
        }
        return reporteFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_reporte, container, false);
        lstLista = (RecyclerView) view.findViewById(R.id.lstLista);
        if(Util.datos.size() > 0){
            AdaptadorTitulares adaptador = new AdaptadorTitulares(Util.datos,getContext());
            lstLista.setAdapter(adaptador);
            lstLista.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            lstLista.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            lstLista.setItemAnimator(new DefaultItemAnimator());
        }else {
            Toast.makeText(getContext(), "No tienen Transferencia realizadas.", Toast.LENGTH_LONG).show();
        }
        ((MainActivity) getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return  view;
    }

}
