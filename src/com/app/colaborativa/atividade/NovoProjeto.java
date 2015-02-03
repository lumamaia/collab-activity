package com.app.colaborativa.atividade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import utils.Mask;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.app.colaborativa.R;
import com.app.colaborativa.adapter.ListaConviteAdapter;
import com.app.colaborativa.adapter.ListaIntegranteGrupoAdapter;

public class NovoProjeto extends ListActivity {

	private EditText txt_nome, txt_prazo;
	private String nome, prazo, projeto_id;
	private Date data_prazo;
	private Button salvar, add_membro, bt_projeto, bt_feed;
	private ListaIntegranteGrupoAdapter integrantesAdapter;

	public EditText busca_contato;
	public ArrayAdapter<String> adapter;
	public List<ParseObject> membros = new ArrayList<ParseObject>();
	public List<String> integrantes = new ArrayList<String>();
	public ParseObject projeto;

	String phoneNumber;
	public ListView listTelefone;
	public ArrayList<String> contatos = new ArrayList<String>();

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.novo_projeto);
		
		txt_nome = (EditText) findViewById(R.id.proj_nome);
		
		txt_prazo = (EditText) findViewById(R.id.proj_prazo);
		txt_prazo.addTextChangedListener(Mask.insert("##/##/####", txt_prazo));

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projeto_id = extras.getString("projeto_id");
			projeto = buscarProjeto(projeto_id);
			integrantes.addAll(Arrays.asList(extras.getString("projeto_membros").split("\\s*,\\s*")));
			txt_nome.setText(extras.getString("projeto_nome"));
			txt_prazo.setText(DateFormat.format("dd/MM/yyyy",
					extras.getLong("projeto_prazo")));
		}
		buscarMembros();
		
		salvar = (Button) findViewById(R.id.bt_salvar);
		salvar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				txt_prazo = (EditText) findViewById(R.id.proj_prazo);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				try {
					data_prazo = sdf.parse(txt_prazo.getText().toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(projeto==null)
					projeto = new ParseObject("projeto");
				
				projeto.put("nome", txt_nome.getText().toString());
				projeto.put("prazo", data_prazo);	
				projeto.put("membros", integrantes);	
				projeto.put("criador", ParseUser.getCurrentUser());
				projeto.saveInBackground();

				Intent VoltarParaProjeto = new Intent(NovoProjeto.this,
						Projeto.class);
				NovoProjeto.this.startActivity(VoltarParaProjeto);
				//NovoProjeto.this.finish();

			}
		});

		add_membro = (Button) findViewById(R.id.bt_add_membro);
		add_membro.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				nome = txt_nome.getText().toString();
				prazo = txt_prazo.getText().toString();

				Intent IrParaBuscarContato = new Intent(NovoProjeto.this,
						BuscarMembro.class);
				NovoProjeto.this.startActivity(IrParaBuscarContato);
				NovoProjeto.this.finish();

			}
		});

		bt_projeto = (Button) findViewById(R.id.button_projeto);
		bt_projeto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent IrParaProjeto = new Intent(NovoProjeto.this,
						Projeto.class);
				NovoProjeto.this.startActivity(IrParaProjeto);
				NovoProjeto.this.finish();

			}
		});
		bt_feed = (Button) findViewById(R.id.button_feeds);
		bt_feed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent IrParaFeed = new Intent(NovoProjeto.this, Feed.class);
				NovoProjeto.this.startActivity(IrParaFeed);
				NovoProjeto.this.finish();

			}
		});
	}

	private ParseObject buscarProjeto(String projeto_id) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("projeto");
		query.include("membros");
		query.getInBackground(projeto_id, new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, com.parse.ParseException e) {
				if (e == null) {
					projeto = object;
				}
			}
		});
		
		return projeto;		
	}
	
	private void buscarMembros() {
		ParseUser.getQuery().findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> objects, com.parse.ParseException e) {
				

				integrantesAdapter = new ListaIntegranteGrupoAdapter(NovoProjeto.this, objects, integrantes);
				setListAdapter(integrantesAdapter);
				
				getListView().setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> adapter, View v,
							int position, long l) {
						
						ImageButton selecionado = (ImageButton) v.findViewById(R.id.ic_convite);
						selecionado.setBackgroundResource(R.drawable.ic_status_finalizada);
						ParseUser user = integrantesAdapter.getItem(position);
						integrantes.add(integrantesAdapter.getItem(position).getObjectId());
					
					}
				});
			}
			});
		
	}

//	@Override
//	public void onContentChanged() {
//		// TODO Auto-generated method stub
//
//		listTelefone = (ListView) findViewById(R.id.listMembro);
//
//		Bundle extras = getIntent().getExtras();
//		if (extras != null) {
//
//			usuarios.add(extras.get("user_id"));
//			contatos.add(extras.get("nome").toString() + "  "
//					+ extras.get("celular").toString());
//		}
//		if (contatos.size() != 0) {
//			adapter = new ArrayAdapter<String>(this,
//					android.R.layout.simple_list_item_1, contatos);
//			listTelefone.setAdapter(adapter);
//
//		}
//		super.onContentChanged();
//	}

}
