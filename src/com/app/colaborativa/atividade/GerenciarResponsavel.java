package com.app.colaborativa.atividade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import android.widget.TextView;
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
	public ParseObject atividade1, convite_atual;
	public List<ParseObject> convite = null;
	public ListaResponsavelAdapter responsavelAdapter;
	public String atividade_id, nome, prazo, descricao, projeto_id,projeto_nome;
	public List<String> projeto_membros = new ArrayList<String>();
	public ViewFlipper viewFlipper;
	public static List<ParseUser> todos_membros;
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
			projeto_membros.addAll(Arrays.asList(extras.getString("projeto_membros").replace("[", "").replace("]", "").split("\\s*,\\s*")));
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

				bt_ser_responsavel.setVisibility(View.GONE);
			
				if(convite_atual == null){
					convite_atual = new ParseObject("convite_responsavel");
					convite_atual.put("atividade", atividade1);
					convite_atual.put("responsavel", ParseUser.getCurrentUser());
					convite_atual.put("usuario", ParseUser.getCurrentUser());
				}
				
				convite_atual.put("status", "Responsável");
				convite_atual.saveInBackground();
				
				List<Object> responsaveis = new ArrayList<Object>();
				responsaveis.add(ParseUser.getCurrentUser().getObjectId());				
				atividade1.put("responsavel", responsaveis);
				atividade1.saveInBackground();

				ParseObject feed = new ParseObject("feed");
				feed.put("atividade", atividade1);
				feed.put("membro", ParseUser.getCurrentUser());
				feed.put("modelo", "NovoResponsavel");
				feed.put("icone", "like");
				feed.put("contador", 0);
				feed.put("data", new Date());
				feed.saveInBackground();

				Intent IrPara = new Intent(
						GerenciarResponsavel.this, GerenciarAtividade.class);
				IrPara.putExtra("atividade_id", atividade_id);
				IrPara.putExtra("projeto_id", projeto_id);
				IrPara.putExtra("projeto_nome", projeto_nome);
				IrPara.putExtra("projeto_membros", projeto_membros.toString());
				GerenciarResponsavel.this.startActivity(IrPara);
				GerenciarResponsavel.this.finish();

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
					TextView contexto = (TextView) findViewById(R.id.tv_contexto);
					contexto.setText(projeto_nome+" > "+atividade1.getString("nome"));					
					
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
			query.orderByAscending("responsavel");
			//query.whereEqualTo("responsavel", ParseUser.getCurrentUser());

			try {
				convite = query.find();
				//todos_membros = ParseUser.getQuery().orderByAscending("objectId").find();
				todos_membros = ParseUser.getQuery().whereContainedIn("objectId", projeto_membros).orderByAscending("nome").find();
					

			} catch (com.parse.ParseException e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			bt_ser_responsavel = (Button) findViewById(R.id.bt_aceitar_convite);
			lista_responsavel = new LinkedHashMap<ParseUser, String>();
			ParseObject convite_aux = null;
			String status;
			for (ParseUser membro : todos_membros) {
				status = "";
				for (ParseObject conv  : convite){
					if(membro.hasSameId(conv.getParseUser("responsavel"))){
						status = conv.getString("status");
						convite_aux = conv;
						break;
					}
				}
				if(membro.hasSameId(ParseUser.getCurrentUser())){
					convite_atual = convite_aux;
					if(!status.equals("Responsável")){
						bt_ser_responsavel.setVisibility(View.VISIBLE);
					}
				}
				lista_responsavel.put(membro, status);
			}
				
			responsavelAdapter = new ListaResponsavelAdapter(GerenciarResponsavel.this, lista_responsavel, atividade1);
			setListAdapter(responsavelAdapter);

			//se o membro já não for responsavel
//			if(!lista_responsavel.get(ParseUser.getCurrentUser()).equals("responsavel")){
//				
//				bt_aceitar.setEnabled(true);
//			}
		}
	}
}
