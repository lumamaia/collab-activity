package com.app.colaborativa.atividade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.Mask;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ViewFlipper;

import com.app.colaborativa.adapter.ListaComentarioAdapter;
import com.app.colaborativa.adapter.ListaResponsavelAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.app.colaborativa.R;

public class GerenciarResponsavel extends ListActivity{

	private EditText txt_nome, txt_comentario, txt_prazo, txt_descricao;
	private Button salvar, bt_projeto, bt_feed, bt_recusar, bt_ser_responsavel;
	public ParseObject atividade1;
	public List<ParseObject> convite = null;
	public ListaResponsavelAdapter responsavelAdapter;
	public String atividade_id, nome, prazo, descricao, projeto_id;
	public ViewFlipper viewFlipper;
	public static List<ParseUser> todos_membros;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gerenciar_responsavel);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			atividade_id = extras.getString("atividade_id");
			findAtividade();
		}

		

		bt_projeto = (Button) findViewById(R.id.button_projeto);
		bt_projeto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent VoltarParaProjeto = new Intent(
						GerenciarResponsavel.this, Projeto.class);
				GerenciarResponsavel.this.startActivity(VoltarParaProjeto);
				GerenciarResponsavel.this.finish();

			}
		});
		bt_feed = (Button) findViewById(R.id.button_feeds);
		bt_feed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent IrParaFeed = new Intent(GerenciarResponsavel.this,
						Feed.class);
				GerenciarResponsavel.this.startActivity(IrParaFeed);
				GerenciarResponsavel.this.finish();

			}
		});
		bt_ser_responsavel = (Button) findViewById(R.id.bt_aceitar_convite);
		bt_ser_responsavel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// ParseObject convite = new ParseObject("convite_responsavel");
				convite.get(0).put("status", "Aceito");
				convite.get(0).saveInBackground();

				ParseObject feed = new ParseObject("feed");
				feed.put("atividade", atividade1);
				feed.put("membro", ParseUser.getCurrentUser());
				feed.put("modelo", "NovoResponsavel");
				feed.put("icone", "like");
				feed.put("contador", 0);
				feed.put("data", new Date());
				feed.saveInBackground();

//				Intent IrParaResponsavel = new Intent(
//						GerenciarResponsavel.this, GerenciarResponsavel.class);
//				GerenciarResponsavel.this.startActivity(IrParaResponsavel);
//				GerenciarResponsavel.this.finish();

			}
		});
	}

	public void findAtividade() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("atividade");

		query.getInBackground(atividade_id, new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, com.parse.ParseException e) {
				if (e == null) {
					atividade1 = object;
					new RemoteDataTask().execute();
					
//					ParseQuery<ParseObject> query = ParseQuery.getQuery("convite_responsavel");
//					query.whereEqualTo("atividade", atividade1);
//					query.whereEqualTo("responsavel", ParseUser.getCurrentUser());
//
//					query.findInBackground(new FindCallback<ParseObject>() {
//						
//						@Override
//						public void done(List<ParseObject> objects, com.parse.ParseException e) {
//							if (e == null) {
//								if(objects.size() != 0){
//									
//									convite = objects;
//									bt_aceitar = (Button) findViewById(R.id.bt_aceitar_convite);
//									bt_aceitar.setEnabled(true);
//								}
//							
//							}
//						}
//					
//					});
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
			query.orderByAscending("responsavel");
			//query.whereEqualTo("responsavel", ParseUser.getCurrentUser());

			try {
				convite = query.find();
				todos_membros = ParseUser.getQuery().orderByAscending("objectId").find();

			} catch (com.parse.ParseException e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			bt_ser_responsavel = (Button) findViewById(R.id.bt_aceitar_convite);
			Map<ParseUser, String> lista_responsavel = new HashMap<ParseUser, String>();
			
			String status;
			for (ParseUser membro : todos_membros) {
				status = "semconvite";
				for (ParseObject conv  : convite){
					if(membro.hasSameId(conv.getParseUser("responsavel"))){
						status = conv.getString("status");
						break;
					}
				}
				if(membro.hasSameId(ParseUser.getCurrentUser())){
					if(status.equals("aceito")){
						bt_ser_responsavel.setVisibility(View.GONE);
					}
					else{
						bt_ser_responsavel.setText("Ser Responsável");
					}
				}
				lista_responsavel.put(membro, status);
			}
				
			responsavelAdapter = new ListaResponsavelAdapter(GerenciarResponsavel.this, lista_responsavel);
			setListAdapter(responsavelAdapter);

			//se o membro já não for responsavel
//			if(!lista_responsavel.get(ParseUser.getCurrentUser()).equals("responsavel")){
//				
//				bt_aceitar.setEnabled(true);
//			}
		}
	}
}
