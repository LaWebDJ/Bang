package com.qallta.bang.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.qallta.bang.MainActivity;
import com.qallta.bang.R;
import com.qallta.bang.SimpleDialog;


public class RecargaFragment extends Fragment implements
        OnItemSelectedListener,
        View.OnClickListener{


    private EditText telefonoEditText;
    private EditText montoEditText;
    private Spinner  comboSpinner;
    private Button enviarButton;

    private String mOperador;
    private String mTelefono;
    private String mMonto;
    private Context context;
    private FragmentManager fragmentManager;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_recarga, container, false);
        context = view.getContext();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Recarga");
        telefonoEditText = (EditText) view.findViewById(R.id.telefono1);
        montoEditText  = (EditText) view.findViewById(R.id.monto);
        enviarButton = (Button)view.findViewById(R.id.btn_eviar);
        enviarButton.setOnClickListener(this);
        comboSpinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),R.array.Games,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboSpinner.setAdapter(adapter);
        comboSpinner.setOnItemSelectedListener(this);
        return view;//inflater.inflate(R.layout.fragment_recarga, container, false);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mOperador = parent.getItemAtPosition(position).toString();
        //Toast.makeText(getContext(),mOperador,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getContext(),"No selecciono el Operador.",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        fragmentManager = getFragmentManager();
        switch (v.getId()){
            case R.id.btn_eviar:
                mTelefono = telefonoEditText.getText().toString();
                mMonto = montoEditText.getText().toString();
                if(!mOperador.contains("Seleccione")){
                    attemptRecarga();
                        //new SimpleDialog(mTelefono,mMonto,mOperador,context).show(fragmentManager, "SimpleDialog");
                        //telefonoEditText.setText("");
                        //montoEditText.setText("");

                }else{
                    Toast.makeText(getContext(),"Seleccione un Operador.",Toast.LENGTH_LONG).show();
                }
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
            telefonoEditText.setError(getString(R.string.error_miniCharacters));
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
            new SimpleDialog(mTelefono,mMonto,mOperador,context).show(fragmentManager, "SimpleDialog");
            telefonoEditText.setText("");
            montoEditText.setText("");
        }

    }

}
