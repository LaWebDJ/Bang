package com.qallta.bang.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qallta.bang.R;
import com.qallta.bang.model.Transfer;

import java.util.ArrayList;



public class AdaptadorTitulares extends RecyclerView.Adapter<AdaptadorTitulares.TitularesViewHolder> {

    private View.OnClickListener listener;
    private ArrayList<Transferencia> datos;
    private Context context;

    public static class TitularesViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTitulo;
        private TextView txtSubtitulo;
        private TextView txtFecha;
        private Context contextTitular;

        public TitularesViewHolder(View itemView,Context context) {
            super(itemView);
            this.contextTitular = context;
            //itemView.setOnClickListener(this);
            txtTitulo = (TextView)itemView.findViewById(R.id.Titulo);
            txtSubtitulo = (TextView)itemView.findViewById(R.id.SubTitulo);
            txtFecha = (TextView) itemView.findViewById(R.id.fecha);
        }

        public void bindTitular(Transferencia t) {
            txtTitulo.setText(t.getTelefono());
            txtSubtitulo.setText(t.getMonto()+" Bs.");
            txtFecha.setText(t.getFechaRegistro());
        }

    }

    public AdaptadorTitulares(ArrayList<Transferencia> datos,Context context) {
        this.datos = datos;
        this.context = context;
    }

    @Override
    public TitularesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_titular, viewGroup, false);


        //itemView.setOnClickListener(this);
        //android:background="?android:attr/selectableItemBackground"

        TitularesViewHolder tvh = new TitularesViewHolder(itemView,context);
        return tvh;
    }

    @Override
    public void onBindViewHolder(TitularesViewHolder viewHolder, int pos) {
        Transferencia item = datos.get(pos);
        viewHolder.bindTitular(item);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }


}
