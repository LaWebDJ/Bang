package com.qallta.bang.asyncTask;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.qallta.bang.MainActivity;
import com.qallta.bang.R;
import com.qallta.bang.RecyclerView.Transferencia;
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

/**
 * Created by DANIEL on 09/06/2016.
 */
public class ReporteTask extends AsyncTask<Void, Void, String> {

    private final String token;
    private JSONArray lista;
    private ArrayList<Transferencia> datos;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    public ReporteTask(String token, FragmentManager manager) {
        this.token = token;
        this.fragmentManager = manager;
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


    public void cargarLista(JSONArray lista) {
        datos = new ArrayList<>();
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
        fragmentTransaction = fragmentManager.beginTransaction();
        ReporteFragment reporteFragment = new ReporteFragment();
        fragmentTransaction.replace(R.id.fragment, reporteFragment);
        fragmentTransaction.commit();
    }
}


