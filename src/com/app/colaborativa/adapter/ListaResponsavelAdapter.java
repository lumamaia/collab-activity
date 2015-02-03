package com.app.colaborativa.adapter;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.colaborativa.R;
import com.parse.ParseUser;

public class ListaResponsavelAdapter extends BaseAdapter {

	private Map<ParseUser, String> membros = new HashMap<ParseUser, String>();
	private ParseUser[] mKeys;
	private Context context;
	private String status;
	public ListaResponsavelAdapter(Context context, Map<ParseUser, String> membros) {
		//super();
		this.membros= membros;
		mKeys = membros.keySet().toArray(new ParseUser[membros.size()]);
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ParseUser membro = (ParseUser) mKeys[position];
		status = (String) getItem(position);

		if (view == null)
			view = LayoutInflater.from(context).inflate(R.layout.lista_responsavel,
					null);

		TextView textViewNome = (TextView) view.findViewById(R.id.tv_membro);
		textViewNome.setText(membro.getString("nome"));
		
		TextView textViewStatus = (TextView) view.findViewById(R.id.tv_status);
		textViewStatus.setText(status);
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(status == "semconvite"){
					//enviar convite
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