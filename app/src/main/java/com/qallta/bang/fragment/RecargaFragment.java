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
        this.saldo.setText("Saldo: "+saldo+"Bs.");
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

        RelativeLayout.LayoutParams layoutParamsTigo;
        RelativeLayout.LayoutParams layoutParamsViva;
        RelativeLayout.LayoutParams layoutParamsEntel;
        Display display = getActivity().getWindowManager().getDefaultDisplay();

        /*
        int tigowidth = imageTigo.getWidth();
        int tigoheight = imageTigo.getHeight();
        int vivawidth = imageViva.getWidth();
        int vivaheight = imageViva.getHeight();
        int entelwidth = imageEntel.getMinimumWidth();
        int entelheight = imageEntel.getMinimumHeight();
        */
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
                //layoutParamsTigo = imageTigo.getLayoutParams();
                //int width = display.getWidth();
                //int height = display.getHeight();
                //layoutParamsTigo = new RelativeLayout.LayoutParams(width,height);
                //imageTigo.getLayoutParams().height = 192;
                //imageTigo.getLayoutParams().width = 192;
                //imageTigo.setLayoutParams(layoutParamsTigo);

                //imageTigo.setImageResource(R.drawable.tigo);
                imageTigogris.setVisibility(View.INVISIBLE);
                imageTigo.setVisibility(View.VISIBLE);
                imageEntel.setVisibility(View.INVISIBLE);
                imageEntelgris.setVisibility(View.VISIBLE);
                imageViva.setVisibility(View.INVISIBLE);
                imageVivagris.setVisibility(View.VISIBLE);


                //layoutParamsTigo.height = 192;
                //layoutParamsTigo.width = 192;
                //imageTigo.setLayoutParams(layoutParamsTigo);

                //layoutParamsViva = imageViva.getLayoutParams();
                //imageViva.setMaxWidth(152);
                //imageViva.setMaxHeight(152);
                //layoutParamsViva.height = 152;
                //layoutParamsViva.width = 152;
                    //layoutParamsViva = new RelativeLayout.LayoutParams(152,152);
                    //imageViva.setLayoutParams(layoutParamsViva);
                //imageViva.getLayoutParams().width = 152;
                //imageViva.getLayoutParams().height = 152;
                    //imageViva.setImageResource(R.drawable.vivagris);

                //layoutParamsEntel = imageEntel.getLayoutParams();
                //imageEntel.setMaxHeight(152);
                //imageEntel.setMaxWidth(152);
                //layoutParamsEntel.width = 152;
                //layoutParamsEntel.height = 152;
                    //layoutParamsEntel = new RelativeLayout.LayoutParams(152,152);
                    //imageEntel.setLayoutParams(layoutParamsEntel);
                //imageEntel.getLayoutParams().height = 152;
                //imageEntel.getLayoutParams().width = 152;
                    //imageEntel.setImageResource(R.drawable.entelgris);

                break;
            case R.id.imageVivagris:
                mOperador = "VIVA";
                //layoutParamsViva = imageViva.getLayoutParams();
                //imageViva.setMaxWidth(192);
                //imageViva.setMaxHeight(192);
                //layoutParamsViva.width = 192;
                //layoutParamsViva.height = 192;
                //layoutParamsViva = new RelativeLayout.LayoutParams(192,192);
                //imageViva.setLayoutParams(layoutParamsViva);
                //imageViva.getLayoutParams().width = 192;
                //imageViva.getLayoutParams().height = 192;
                //imageViva.setImageResource(R.drawable.viva);
                imageVivagris.setVisibility(View.INVISIBLE);
                imageViva.setVisibility(View.VISIBLE);
                imageTigo.setVisibility(View.INVISIBLE);
                imageTigogris.setVisibility(View.VISIBLE);
                imageEntel.setVisibility(View.INVISIBLE);
                imageEntelgris.setVisibility(View.VISIBLE);

                //layoutParamsTigo = imageTigo.getLayoutParams();
                //imageTigo.setMaxHeight(152);
                //imageTigo.setMaxWidth(152);
                //layoutParamsTigo.height = 152;
                //layoutParamsTigo.width = 152;
                //layoutParamsTigo = new RelativeLayout.LayoutParams(152,152);
                //imageTigo.setLayoutParams(layoutParamsTigo);
                //imageTigo.getLayoutParams().height = 152;
                //imageTigo.getLayoutParams().width = 152;
                //imageTigo.setImageResource(R.drawable.tigogris);

                //layoutParamsEntel = imageEntel.getLayoutParams();
                //imageEntel.setMaxHeight(152);
                //imageEntel.setMaxWidth(152);
                //layoutParamsEntel.width = 152;
                //layoutParamsEntel.height = 152;
                //layoutParamsEntel = new RelativeLayout.LayoutParams(152,152);
                //imageEntel.setLayoutParams(layoutParamsEntel);
                //imageEntel.getLayoutParams().height = 152;
                //imageEntel.getLayoutParams().width = 152;
                //imageEntel.setImageResource(R.drawable.entelgris);

                break;
            case R.id.imageEntelgris:
                mOperador = "ENTEL";
                //imageEntel.setMaxHeight(192);
                //imageEntel.setMaxWidth(192);
                //layoutParamsEntel = imageEntel.getLayoutParams();
                //layoutParamsEntel.height = 192;
                //layoutParamsEntel.width = 192;
                //layoutParamsEntel = new RelativeLayout.LayoutParams(192,192);
                //imageEntel.setLayoutParams(layoutParamsEntel);
                //imageEntel.getLayoutParams().width = 192;
                //imageEntel.getLayoutParams().height = 192;
                //imageEntel.setImageResource(R.drawable.entel);
                imageEntelgris.setVisibility(View.INVISIBLE);
                imageEntel.setVisibility(View.VISIBLE);
                imageTigo.setVisibility(View.INVISIBLE);
                imageTigogris.setVisibility(View.VISIBLE);
                imageViva.setVisibility(View.INVISIBLE);
                imageVivagris.setVisibility(View.VISIBLE);

                //layoutParamsTigo = imageTigo.getLayoutParams();
                //imageTigo.setMaxHeight(152);
                //imageTigo.setMaxWidth(152);
                //layoutParamsTigo.height = 152;
                //layoutParamsTigo.width = 152;
                //layoutParamsTigo = new RelativeLayout.LayoutParams(152,152);
                //imageTigo.setLayoutParams(layoutParamsTigo);
                //imageTigo.getLayoutParams().height = 152;
                //imageTigo.getLayoutParams().width = 152;
                //imageTigo.setImageResource(R.drawable.tigogris);

                //layoutParamsViva = imageViva.getLayoutParams();
                //imageViva.setMaxWidth(152);
                //imageViva.setMaxHeight(152);
                //layoutParamsViva.height= 152;
                //layoutParamsViva.width = 152;
                //layoutParamsViva = new RelativeLayout.LayoutParams(152,152);
                //imageViva.setLayoutParams(layoutParamsViva);
                //imageViva.getLayoutParams().height = 152;
                //imageViva.getLayoutParams().width = 152;
                //imageViva.setImageResource(R.drawable.vivagris);

                break;
            case R.id.textViewReporte:
                ReporteTask reporteTask = new ReporteTask(Util.TOKEN,getFragmentManager());
                reporteTask.execute();
                break;
        }
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
        }
        if (TextUtils.isEmpty(monto)) {
            montoEditText.setError(getString(R.string.error_required));
            focusView = montoEditText;
            sw = true;
        }else if(!TextUtils.isDigitsOnly(monto)){
            montoEditText.setError(getString(R.string.invalid_phone2));
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
            simpleDialog.setMessage("¿Desea enviar " + mMonto + "Bs. al " + mTelefono + "?");
            simpleDialog.setFragmentManager(fragmentManager);
            simpleDialog.show(fragmentManager,"SimpleDialog");
            telefonoEditText.setText("");
            montoEditText.setText("");
        }

    }

}
