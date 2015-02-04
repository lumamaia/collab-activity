package com.app.colaborativa.adapter;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.colaborativa.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ListaResponsavelAdapter extends BaseAdapter {

	private Map<ParseUser, String> membros = new LinkedHashMap<ParseUser, String>();
	private ParseUser[] mKeys;
	private ParseObject atividade;
	private Context context;
	private String status;
	public ListaResponsavelAdapter(Context context, Map<ParseUser, String> membros, ParseObject atividade) {
		//super();
		this.membros= membros;
		this.atividade = atividade;
		mKeys = membros.keySet().toArray(new ParseUser[membros.size()]);
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ParseUser membro = (ParseUser) mKeys[position];
		status = (String) getItem(position);

		if (view == null)
			view = LayoutInflater.from(context).inflate(R.layout.lista_responsavel,
					null);

		TextView textViewNome = (TextView) view.findViewById(R.id.tv_membro);
		textViewNome.setText(membro.getString("nome"));
		
		final TextView textViewStatus = (TextView) view.findViewById(R.id.tv_status);
		textViewStatus.setText(status);
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(status == ""){
					//enviar convite
					
					 textViewStatus.setText("Convidado");
					
					 ParseObject convite = new ParseObject("convite_responsavel");
					 convite.put("atividade", atividade);
					 convite.put("responsavel", membro);
					 convite.put("usuario", ParseUser.getCurrentUser());
					 convite.put("status", "Convidado");
					 convite.saveInBackground();
					 
					 ParseObject feed2 = new ParseObject("feed");
					 feed2.put("atividade", atividade);
					 feed2.put("modelo", "InformativoSugestaoResponsavel");
					 feed2.put("icone", "like");
					 feed2.put("membro", membro);
					 feed2.put("contador", 0);
					 feed2.put("data", new Date());
					 feed2.saveInBackground();
				}
				else if(status == "semconvite"){
					
				}
				
				
			}
		});
		
		
		
//		ImageButton conviteIcone = (ImageButton) view.findViewById(R.id.ic_convite);
//		conviteIcone.setBackgroundResource(R.drawable.ic_uncheck);
		
//		conviteIcone.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//				conviteIcone.setBackgroundResource(R.drawable.ic_status_finalizada);
//				conviteIcone.setEnabled(false);
//
//			}
//	
//		});
		
		return view;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		 return membros.size();
	}

	@Override
	public Object getItem(int position) {
		return membros.get(mKeys[position]);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

}