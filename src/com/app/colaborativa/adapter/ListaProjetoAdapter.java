package com.app.colaborativa.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseObject;
import com.app.colaborativa.R;
import com.app.colaborativa.atividade.Atividade;
import com.app.colaborativa.atividade.NovoProjeto;
import com.app.colaborativa.atividade.Projeto;

public class ListaProjetoAdapter extends ArrayAdapter<ParseObject> {
 
    private Context context;
    private List<ParseObject> projeto = null;
 
    public ListaProjetoAdapter(Context context,  List<ParseObject> projetos) {
        super(context,0, projetos);
        this.projeto = projetos;
        this.context = context;
    }
 
    @Override
    public View getView(int position, View view, ViewGroup parent) {
    	final ParseObject proj = projeto.get(position);
         
        if(view == null)
            view = LayoutInflater.from(context).inflate(R.layout.lista_projeto, null);
 
       
        TextView textViewNome = (TextView) view.findViewById(R.id.tv_proj_nome);
        textViewNome.setText(proj.getString("nome"));
         
        TextView textViewPrazo = (TextView)view.findViewById(R.id.tv_proj_prazo);
        textViewPrazo.setText(DateFormat.format("dd-MM-yyyy", proj.getDate("prazo").getTime()));
        
        ImageButton imageIcone = (ImageButton) view.findViewById(R.id.ic_projeto);
        imageIcone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(context,
						NovoProjeto.class);
				intent.putExtra("projeto_nome", proj.getString("nome"));
				intent.putExtra("projeto_prazo", proj.getDate("prazo").getTime());
				intent.putExtra("projeto_membros", proj.getList("membros").toString().replace("[", "").replace("]", ""));
				intent.putExtra("projeto_id", proj.getObjectId());
				context.startActivity(intent);
			}
	
		});
 
        return view;
    }
}