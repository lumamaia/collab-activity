package com.app.colaborativa.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import utils.PullParse;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.colaborativa.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ListaResponsavelAdapter extends BaseAdapter {

	private Map<ParseUser, String> membros = new LinkedHashMap<ParseUser, String>();
	private ParseUser[] mKeys;
	private ParseObject atividade;
	private Context context;

	public ListaResponsavelAdapter(Context context,
			Map<ParseUser, String> membros, ParseObject atividade) {
		// super();
		this.membros = membros;
		this.atividade = atividade;
		mKeys = membros.keySet().toArray(new ParseUser[membros.size()]);
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ParseUser membro = (ParseUser) mKeys[position];
		final String status = (String) getItem(position);

		if (view == null)
			view = LayoutInflater.from(context).inflate(
					R.layout.lista_responsavel, null);

		TextView textViewNome = (TextView) view.findViewById(R.id.tv_membro);
		textViewNome.setText(membro.getString("nome"));

		final ImageView selecionado = (ImageView) view
				.findViewById(R.id.ic_convite);
		if (status.equals("Convidado"))
			selecionado.setBackgroundResource(R.drawable.ic_convidado);
		else if (status.equals("semconvite"))
			selecionado.setBackgroundResource(R.drawable.ic_semconvite);
		else if (status.equals("Responsável"))
			selecionado.setBackgroundResource(R.drawable.ic_responsavel);

		final TextView textViewStatus = (TextView) view
				.findViewById(R.id.tv_status);
		textViewStatus.setText(status);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				TextView tvstatus = (TextView) v.findViewById(R.id.tv_status);

				if (tvstatus.getText().equals("semconvite")
						&& membro.hasSameId(ParseUser.getCurrentUser())){
					
					textViewStatus.setText("Responsável");
					selecionado
							.setBackgroundResource(R.drawable.ic_responsavel);

					ParseObject convite = new ParseObject("convite_responsavel");
					convite.put("atividade", atividade);
					convite.put("responsavel", membro);
					convite.put("usuario", ParseUser.getCurrentUser());
					convite.put("status", "Responsável");
					convite.saveInBackground();

					List<Object> responsaveis = new ArrayList<Object>();
					responsaveis.add(ParseUser.getCurrentUser().getObjectId());
					atividade.put("responsavel", responsaveis);
					atividade.saveInBackground();


					PullParse.saveFeed(atividade, atividade.getParseObject("projeto"), "NovoResponsavel", "like", ParseUser.getCurrentUser());

				}
				else if (tvstatus.getText().equals("semconvite")) {
					// enviar convite

					textViewStatus.setText("Convidado");
					selecionado.setBackgroundResource(R.drawable.ic_convidado);

					ParseObject convite = new ParseObject("convite_responsavel");
					convite.put("atividade", atividade);
					convite.put("responsavel", membro);
					convite.put("usuario", ParseUser.getCurrentUser());
					convite.put("status", "Convidado");
					convite.saveInBackground();

					PullParse.saveFeed(atividade, atividade.getParseObject("projeto"), "InformativoSugestaoResponsavel", "like", membro);
				}

				else if (tvstatus.getText().equals("Convidado")
						&& membro.hasSameId(ParseUser.getCurrentUser())) {

					textViewStatus.setText("Responsável");
					selecionado
							.setBackgroundResource(R.drawable.ic_responsavel);

					ParseObject convite = getConvite();

					convite.put("status", "Responsável");
					convite.saveInBackground();

					List<Object> responsaveis = new ArrayList<Object>();
					responsaveis.add(ParseUser.getCurrentUser().getObjectId());
					atividade.put("responsavel", responsaveis);
					atividade.saveInBackground();
					
					PullParse.saveFeed(atividade, atividade.getParseObject("projeto"), "NovoResponsavel", "like", ParseUser.getCurrentUser());

				}

				else if (tvstatus.getText().equals("Responsável")
						&& membro.hasSameId(ParseUser.getCurrentUser())) {

					textViewStatus.setText("semconvite");
					selecionado.setBackgroundResource(R.drawable.ic_semconvite);

					ParseObject convite = getConvite();

					convite.deleteInBackground();

				}

			}
		});
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

	public ParseObject getConvite() {
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery("convite_responsavel");
		query.whereEqualTo("atividade", atividade);
		query.whereEqualTo("responsavel", ParseUser.getCurrentUser());
		try {
			return query.getFirst();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}