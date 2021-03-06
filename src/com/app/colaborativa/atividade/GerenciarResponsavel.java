package com.app.colaborativa.atividade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import utils.MenuAction;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.colaborativa.R;
import com.app.colaborativa.adapter.ListaResponsavelAdapter;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class GerenciarResponsavel extends ListActivity {

	private Button bt_projeto, bt_feed, bt_contexto;
	public ParseObject atividade1;
	public List<ParseObject> convite = null;
	public ListaResponsavelAdapter responsavelAdapter;
	public String atividade_id, nome, prazo, descricao, projeto_id,
			projeto_nome;
	public List<String> projeto_membros = new ArrayList<String>();
	public ViewFlipper viewFlipper;
	public static List<ParseUser> todos_membros;
	public static ParseObject convite_atual;
	Map<ParseUser, String> lista_responsavel;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gerenciar_responsavel);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			atividade_id = extras.getString("atividade_id");
			projeto_id = extras.getString("projeto_id");
			projeto_nome = extras.getString("projeto_nome");
			projeto_membros.addAll(Arrays.asList(extras
					.getString("projeto_membros").replace("[", "")
					.replace("]", "").split("\\s*,\\s*")));
			findAtividade();
		}

		bt_projeto = (Button) findViewById(R.id.button_projeto);
		bt_feed = (Button) findViewById(R.id.button_feeds);
		MenuAction menu = new MenuAction();
		menu.MapearProjeto(this, bt_projeto);		
		menu.MapearFeed(this, bt_feed);

		bt_contexto = (Button) findViewById(R.id.button_contexto);
		bt_contexto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void findAtividade() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("atividade");
		query.include("projeto");

		query.getInBackground(atividade_id, new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, com.parse.ParseException e) {
				if (e == null) {
					atividade1 = object;
					bt_contexto = (Button) findViewById(R.id.button_contexto);
					bt_contexto.setText("Projeto " + projeto_nome + " > Atividade "
							+ atividade1.getString("nome"));

					new RemoteDataTask().execute();
				}
			}

		});
	}

	public class RemoteDataTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {

			ParseQuery<ParseObject> query = ParseQuery
					.getQuery("convite_responsavel");
			query.whereEqualTo("atividade", atividade1);
			query.include("responsavel");
			query.orderByAscending("prazo");
			// query.whereEqualTo("responsavel", ParseUser.getCurrentUser());

			try {
				convite = query.find();
				// todos_membros =
				// ParseUser.getQuery().orderByAscending("objectId").find();
				todos_membros = ParseUser.getQuery()
						.whereContainedIn("objectId", projeto_membros)
						.orderByAscending("nome").find();

			} catch (com.parse.ParseException e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			lista_responsavel = new LinkedHashMap<ParseUser, String>();
			String status;
			for (ParseUser membro : todos_membros) {
				status = "semconvite";
				for (ParseObject conv : convite) {
					if (membro.hasSameId(conv.getParseUser("responsavel"))) {
						status = conv.getString("status");
						break;
					}
				}
				lista_responsavel.put(membro, status);
			}

			responsavelAdapter = new ListaResponsavelAdapter(
					GerenciarResponsavel.this, lista_responsavel, atividade1);
			setListAdapter(responsavelAdapter);

		}
	}
}
