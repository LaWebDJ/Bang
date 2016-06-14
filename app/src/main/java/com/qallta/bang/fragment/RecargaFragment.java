package com.qallta.bang.fragment;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qallta.bang.MainActivity;
import com.qallta.bang.R;
import com.qallta.bang.SimpleDialog;
import com.qallta.bang.asyncTask.SaldoTask;
import com.qallta.bang.asyncTask.ReporteTask;
import com.qallta.bang.util.Util;

import de.hdodenhof.circleimageview.CircleImageView;


public class RecargaFragment extends Fragment implements View.OnClickListener{


    private EditText telefonoEditText;
    private EditText montoEditText;
    private TextView saldo;
    private TextView reporte;
    private Spinner  comboSpinner;
    private Button enviarButton;
    private ImageView imageTigo;
    private ImageView imageTigogris;
    private ImageView imageViva;
    private ImageView imageVivagris;
    private ImageView imageEntel;
    private ImageView imageEntelgris;
    private ImageLoader imageLoader;
    private String mOperador;
    private String mTelefono;
    private String mMonto;
    private Context context;
    private SharedPreferences preferences;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private final int VISIBLE = 1;
    private final int INVISIBLE = 0;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recarga, container, false);
        context = view.getContext();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Recarga");
        ((MainActivity) getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        telefonoEditText = (EditText) view.findViewById(R.id.telefono1);
        montoEditText  = (EditText) view.findViewById(R.id.monto);
        preferences = ((MainActivity) getActivity()).getSharedPreferences("bangPreferences", Context.MODE_PRIVATE);
        saldo = (TextView)view.findViewById(R.id.textViewSaldo);
        saldo.setText(saldo.getText()+" : "+preferences.getInt("saldo", 0)+"Bs.");
        reporte = (TextView)view.findViewById(R.id.textViewReporte);
        enviarButton = (Button)view.findViewById(R.id.btn_eviar);
        imageTigo = (ImageView) view.findViewById(R.id.imageTigo);
        imageTigogris = (ImageView) view.findViewById(R.id.imageTigogris);
        imageViva = (ImageView)view.findViewById(R.id.imageViva);
        imageVivagris = (ImageView) view.findViewById(R.id.imageVivagris);
        imageEntel = (ImageView)view.findViewById(R.id.imageEntel);
        imageEntelgris = (ImageView) view.findViewById(R.id.imageEntelgris);
        reporte.setOnClickListener(this);
        imageTigo.setOnClickListener(this);
        imageTigogris.setOnClickListener(this);
        imageViva.setOnClickListener(this);
        imageVivagris.setOnClickListener(this);
        imageEntel.setOnClickListener(this);
        imageEntelgris.setOnClickListener(this);
        enviarButton.setOnClickListener(this);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
/*      comboSpinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),R.array.Games,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboSpinner.setAdapter(adapter);
        comboSpinner.setOnItemSelectedListener(this);
*/      Util.view = view;
        return view;//inflater.inflate(R.layout.fragment_recarga, container, false);
    }

/*
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mOperador = parent.getItemAtPosition(position).toString();
        //Toast.makeText(getContext(),mOperador,Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getContext(),"No selecciono el Operador.",Toast.LENGTH_LONG).show();
    }
    */

    public void setSaldo(String saldo){
        this.saldo = (TextView)Util.view.findViewById(R.id.textViewSaldo);
        this.saldo.setText("Saldo: " + saldo + "Bs.");
    }

    @Override
    public void onResume() {
        super.onResume();
        SaldoTask saldoTask = new SaldoTask(Util.preferences.getString("token",""));
        saldoTask.execute();
        MainActivity mainActivity = new MainActivity();
        mainActivity.setHeaderView();
    }

    @Override
    public void onClick(View v) {
        fragmentManager = getFragmentManager();
        switch (v.getId()){
            case R.id.btn_eviar:
                mTelefono = telefonoEditText.getText().toString();
                mMonto = montoEditText.getText().toString();
                if(mOperador != null){
                    attemptRecarga();
                }else{
                    Toast.makeText(getContext(),"Seleccione un Operador.",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.imageTigogris:
                mOperador = "TIGO";
                imageTigogris.setVisibility(View.INVISIBLE);
                imageTigo.setVisibility(View.VISIBLE);
                imageEntel.setVisibility(View.INVISIBLE);
                imageEntelgris.setVisibility(View.VISIBLE);
                imageViva.setVisibility(View.INVISIBLE);
                imageVivagris.setVisibility(View.VISIBLE);
                break;
            case R.id.imageVivagris:
                mOperador = "VIVA";
                imageVivagris.setVisibility(View.INVISIBLE);
                imageViva.setVisibility(View.VISIBLE);
                imageTigo.setVisibility(View.INVISIBLE);
                imageTigogris.setVisibility(View.VISIBLE);
                imageEntel.setVisibility(View.INVISIBLE);
                imageEntelgris.setVisibility(View.VISIBLE);
                break;
            case R.id.imageEntelgris:
                mOperador = "ENTEL";
                imageEntelgris.setVisibility(View.INVISIBLE);
                imageEntel.setVisibility(View.VISIBLE);
                imageTigo.setVisibility(View.INVISIBLE);
                imageTigogris.setVisibility(View.VISIBLE);
                imageViva.setVisibility(View.INVISIBLE);
                imageVivagris.setVisibility(View.VISIBLE);
                break;
            case R.id.textViewReporte:
                if (isConnected()) {
                    ReporteTask reporteTask = new ReporteTask(Util.TOKEN, getFragmentManager());
                    reporteTask.execute();
                }else{
                    Toast.makeText(getContext(),"Necesitas una conexion a Internet",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private boolean isConnected(){
        boolean sw = false;
        //connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Util.connectivityManager != null){
            NetworkInfo networkInfo_WIFI = Util.connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo networkInfo_MOVIL = Util.connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo_WIFI.isConnected() || networkInfo_MOVIL.isConnected()){
                sw = true;
            }
        }
        return sw;
    }



    private void attemptRecarga(){
        boolean sw = false;
        // Reset errors.
        telefonoEditText.setError(null);
        montoEditText.setError(null);
        String telefono = telefonoEditText.getText().toString();
        String monto = montoEditText.getText().toString();
        View focusView = null;
        if (TextUtils.isEmpty(telefono)) {
            telefonoEditText.setError(getString(R.string.error_required));
            focusView = telefonoEditText;
            sw = true;
        }else if(telefono.length() < 8) {
            telefonoEditText.setError(getString(R.string.invalid_phone));
            focusView = telefonoEditText;
            sw = true;
        }else if(!TextUtils.isDigitsOnly(telefono)){
            telefonoEditText.setError(getString(R.string.invalid_phone2));
            focusView = telefonoEditText;
            sw = true;
        }else if(Integer.parseInt(telefono) < 60000000){
            telefonoEditText.setError(getString(R.string.invalid_phone2));
            focusView = telefonoEditText;
            sw = true;
        }
        if (TextUtils.isEmpty(monto)) {
            montoEditText.setError(getString(R.string.error_required));
            focusView = montoEditText;
            sw = true;
        }else if(!TextUtils.isDigitsOnly(monto)){
            montoEditText.setError(getString(R.string.invalid_phone2));
            focusView = montoEditText;
            sw = true;
        }else if(Integer.parseInt(monto) <= 0){
            montoEditText.setError(getString(R.string.monto_invalido));
            focusView = montoEditText;
            sw = true;
        }
        if (sw) {
            focusView.requestFocus();
        }else{
            SimpleDialog simpleDialog = new SimpleDialog();
            simpleDialog.setmTelefono(mTelefono);
            simpleDialog.setmMonto(mMonto);
            simpleDialog.setmOperador(mOperador);
            simpleDialog.setContext(context);
            simpleDialog.setTitle("Recarga");
            simpleDialog.setMessage("¿Desea enviar " + mMonto + "Bs. al número " + mTelefono + "?");
            simpleDialog.setFragmentManager(fragmentManager);
            simpleDialog.show(fragmentManager,"SimpleDialog");
            telefonoEditText.setText("");
            montoEditText.setText("");
        }

    }

}
