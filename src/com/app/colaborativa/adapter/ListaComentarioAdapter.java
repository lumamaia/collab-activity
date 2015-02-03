package com.app.colaborativa.adapter;

import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.colaborativa.R;
import com.parse.ParseObject;

public class ListaComentarioAdapter extends ArrayAdapter<ParseObject> {
 
    private Context context;
    private List<ParseObject> comentarios = null;
    public ParseObject membro;
 
    public ListaComentarioAdapter(Context context,  List<ParseObject> comentarios) {
        super(context,0, comentarios);
        this.comentarios = comentarios;
        this.context = context;
    }
 
    @Override
    public View getView(int position, View view, ViewGroup parent) {
    	ParseObject comentario = comentarios.get(position);
         
        if(view == null)
            view = LayoutInflater.from(context).inflate(R.layout.lista_comentario, null);
 
       
        TextView textViewNome = (TextView) view.findViewById(R.id.tv_comentario_membro);
        textViewNome.setText(comentario.getParseObject("usuario").getString("nome"));
         
        TextView textViewComentario = (TextView) view.findViewById(R.id.tv_comentario);
        textViewComentario.setText(comentario.getString("comentario"));
        
        TextView textViewPrazo = (TextView)view.findViewById(R.id.tv_comentario_data);
        textViewPrazo.setText(DateFormat.format("dd/MM/yyyy hh:mm:ss", comentario.getDate("data").getTime()));
 
        return view;
    }
}