package com.app.colaborativa.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.colaborativa.R;
import com.parse.ParseObject;

public class ListaAtividadeAdapter extends ArrayAdapter<ParseObject> {
 
    private Context context;
    private List<ParseObject> atividade = null;
 
    public ListaAtividadeAdapter(Context context,  List<ParseObject> atividades) {
        super(context,0, atividades);
        this.atividade = atividades;
        this.context = context;
    }
 
    @Override
    public View getView(int position, View view, ViewGroup parent) {
    	ParseObject ativ = atividade.get(position);
         
        if(view == null)
            view = LayoutInflater.from(context).inflate(R.layout.lista_atividade, null);
 
       
        TextView textViewNome = (TextView) view.findViewById(R.id.tv_ativ_nome);
        textViewNome.setText(ativ.getString("nome"));
        

       Long diferenca = (ativ.getDate("prazo").getTime()- new Date().getTime());
        TextView textViewPrazo = (TextView)view.findViewById(R.id.tv_ativ_prazo);
        textViewPrazo.setText(DateFormat.format("dd-MM-yyyy", ativ.getDate("prazo").getTime()));
        
        ImageView icon_status = (ImageView)view.findViewById(R.id.ic_status);
        if(ativ.getString("status").equals("Finalizada"))
        {
        	icon_status.setImageResource(R.drawable.ic_status_finalizada);
        }
        else
        {
        	if(diferenca < 0){
        		icon_status.setImageResource(R.drawable.ic_status_atrasada);
        	}else{
        	icon_status.setImageResource(R.drawable.ic_status_aberta);
        	}
        	
        }
        
 
        return view;
    }
}