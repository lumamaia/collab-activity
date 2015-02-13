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

public class ListaConviteAdapter extends ArrayAdapter<ParseUser> {

	private List<ParseUser> membros = null;
	private Context context;
	
	public ListaConviteAdapter(Context context, List<ParseUser> membros) {
		super(context, 0, membros);
		this.membros = membros;
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ParseObject membro = membros.get(position);

		if (view == null)
			view = LayoutInflater.from(context).inflate(R.layout.lista_convite,
					null);

		TextView textViewNome = (TextView) view.findViewById(R.id.tv_membro);
		textViewNome.setText(membro.getString("nome"));
		
		ImageView conviteIcone = (ImageView) view.findViewById(R.id.ic_convite);
		conviteIcone.setBackgroundResource(R.drawable.ic_membro_uncheck);
		
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

}