package com.app.colaborativa.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.app.colaborativa.R;

public class ListaIntegranteGrupoAdapter extends ArrayAdapter<ParseUser> {

	private List<ParseUser> membros = null;
	private List<String> integrante = new ArrayList<String>();
	private Context context;
	
	public ListaIntegranteGrupoAdapter(Context context, List<ParseUser> membros, List<String> integrante) {
		super(context, 0, membros);
		this.integrante =  integrante;
		this.membros = membros;
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ParseObject membro = membros.get(position);
		
		if (view == null)
			view = LayoutInflater.from(context).inflate(R.layout.lista_convite,
					null);
		
		TextView tvNome = (TextView) view.findViewById(R.id.tv_membro);
		tvNome.setText(membro.getString("nome"));
		
		ImageButton selecionado = (ImageButton) view.findViewById(R.id.ic_convite);
		
		if(integrante.contains(membro.getObjectId())){
			selecionado.setBackgroundResource(R.drawable.ic_status_finalizada);
		}
		else
		{
			selecionado.setBackgroundResource(R.drawable.ic_uncheck);
		}
		return view;
	}

}