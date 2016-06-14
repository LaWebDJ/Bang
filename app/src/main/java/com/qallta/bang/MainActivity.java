package com.qallta.bang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qallta.bang.RecyclerView.Titular;
import com.qallta.bang.RecyclerView.Transferencia;
import com.qallta.bang.fragment.RecargaFragment;
import com.qallta.bang.fragment.ReporteFragment;
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

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SimpleDialog.OnSimpleDialogListener{

    private Toolbar toolbar;
    private TextView user;
    private TextView cuenta;
    private TextView saldo;
    private TextView fecha;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private SharedPreferences preferences;

    private JSONArray lista;
    private ArrayList<Transferencia> datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.inflateHeaderView(R.layout.nav_header_main);//getHeaderView(R.id.nav_header_main);
        user = (TextView) view.findViewById(R.id.name);
        cuenta = (TextView) view.findViewById(R.id.cuenta);
        saldo = (TextView) view.findViewById(R.id.saldo);
        //SharedPreferences sharedPreferences = getSharedPreferences("bangPreferences", Context.MODE_PRIVATE); ESTO CAMBIE
        preferences = getSharedPreferences("bangPreferences", Context.MODE_PRIVATE);
        user.setText(preferences.getString("name", ""));
        cuenta.setText(preferences.getString("account",""));
        saldo.setText(String.valueOf(preferences.getInt("saldo",0))+" Bs.");
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setFragment(0);
    }

    public void setFragment(int position) {

        switch (position) {
            case 0:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                RecargaFragment recargaFragment = new RecargaFragment();
                fragmentTransaction.replace(R.id.fragment, recargaFragment);
                fragmentTransaction.commit();
                break;

            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                ReporteFragment reporteFragment = new ReporteFragment();
                fragmentTransaction.replace(R.id.fragment, reporteFragment);
                fragmentTransaction.commit();
                break;

        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //setFragment(0);
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            toolbar.setTitle("Recarga");
            setFragment(0);
        } else if (id == R.id.nav_gallery) {
            toolbar.setTitle("Reporte");
            ReporteTask reporteTask = new ReporteTask(Util.TOKEN);
            reporteTask.execute();
        }  else if (id == R.id.salir) {
            super.onBackPressed();
            preferences = getSharedPreferences("bangPreferences", Context.MODE_PRIVATE);
            String token = preferences.getString("token","");
            LogoutTask logoutTask = new LogoutTask(token);
            logoutTask.execute();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void cargarLista(JSONArray lista) {
        datos = new ArrayList<>();
          /*
        JSONObject json = null;

        try {
            json = (JSONObject) lista.get(0);
            String fechaRegistro = String.valueOf(json.get("fechaRegistro"));
            preferences = getSharedPreferences("bangPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("fecha",fechaRegistro);
            editor.apply();
        } catch (JSONException e) {
            Log.e("daniel->JSONException: ", e.getMessage());
        }
        */

        for (int i = 0; i < lista.length(); i++){
            try {
                JSONObject jsonObject = (JSONObject) lista.get(i);
                String telefono = String.valueOf(jsonObject.get("telefono"));
                String m = String.valueOf(jsonObject.get("monto"));
                double mo = Double.valueOf(m);
                int mon = (int)mo;
                String monto = String.valueOf(mon);
                String fechaRegistro = String.valueOf(jsonObject.get("fechaRegistro"));
                Transferencia transferencia = new Transferencia(monto,fechaRegistro,telefono);
                datos.add(transferencia);
            } catch (JSONException e) {
                Log.e("daniel->JSONException: ", e.getMessage());
            }
        }
        Util.datos = datos;
        setFragment(1);
    }

    @Override
    public void onPossitiveButtonClick() {

    }

    @Override
    public void onNegativeButtonClick() {

    }


    private class LogoutTask extends AsyncTask<Void, Void, String> {

        private final String mtoken;

        LogoutTask(String token) {
            mtoken = token;
        }

        @Override
        protected String doInBackground(Void... params) {
            String respuesta = "";
            HttpClient httpClient = new DefaultHttpClient();
            String url  = Util.URL_SERVICE+"logout/"+mtoken;
            HttpGet del = new HttpGet(Util.URL_SERVICE+"logout/"+mtoken);
            del.setHeader("content-type", "application/json");
            del.setHeader("default_charset", "UTF-8");
            try {
                HttpResponse httpResponse = httpClient.execute(del);
                String respon = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = new JSONObject(respon);
                respuesta= jsonObject.getString("result");

            } catch (Exception e) {
                Log.e("LogoutTask->ERROR LOCAL ","Error: "+e.getMessage());
                respuesta = "ERROR LOCAL";
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(String success) {
            //mAuthTask = null;
            //showProgress(false);
            if (success.equals("OK")) {
                preferences = getSharedPreferences("bangPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token", "");
                editor.putInt("saldo", 0);
                editor.putString("name", "");
                editor.putString("account", "");
                editor.apply();
                Util.preferences = preferences;
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            } else if(success.equals("UNKNOWN_ACCOUNT")){
                Toast.makeText(getApplicationContext(),"Error.",Toast.LENGTH_LONG).show();
            }
        }

    }




    public class ReporteTask extends AsyncTask<Void, Void, String> {

        private final String token;


        ReporteTask(String token) {
            this.token = token;
        }

        @Override
        protected String doInBackground(Void... params) {
            String respuesta = "";
            HttpClient httpClient = new DefaultHttpClient();
            String url  = Util.URL_SERVICE+"findAllTransferencia/"+token;
            HttpGet del = new HttpGet(Util.URL_SERVICE+"findAllTransferencia/"+token);
            del.setHeader("content-type", "application/json");
            del.setHeader("default_charset", "UTF-8");
            try {
                HttpResponse httpResponse = httpClient.execute(del);
                String respon = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = new JSONObject(respon);
                respuesta= jsonObject.getString("result");
                if(respuesta.equals("OK")){
                    lista =  jsonObject.getJSONArray("lista");
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

            //showProgress(false);
            if (success.equals("OK")) {
                cargarLista(lista);
            } else if(success.equals("phone_occupied")){
                //mTelefono.setError(getString(R.string.phone_invalid));
                //mTelefono.requestFocus();
            }
        }
    }





}
