package com.app.colaborativa.atividade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import utils.DeleteAll;
import utils.Mask;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.colaborativa.R;
import com.app.colaborativa.adapter.ListaIntegranteGrupoAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class NovoProjeto extends ListActivity {

	private EditText txt_nome, txt_prazo;
	private TextView tv_nome, tv_prazo;
	private String nome, prazo, projeto_id;
	private boolean is_edicao;
	private Date data_prazo;
	private Button bt_projeto, bt_feed;
	private ImageView salvar, excluir;
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
		
		is_edicao = false;
		
		txt_nome = (EditText) findViewById(R.id.proj_nome);		
		txt_prazo = (EditText) findViewById(R.id.proj_prazo);
		txt_prazo.addTextChangedListener(Mask.insert("##/##/####", txt_prazo));
		tv_nome = (TextView) findViewById(R.id.tv_nome);		
		tv_prazo = (TextView) findViewById(R.id.tv_prazo);
		excluir = (ImageView) findViewById(R.id.ic_excluir);
		TextView contexto = (TextView) findViewById(R.id.tv_contexto);	

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			is_edicao = true;
			projeto_id = extras.getString("projeto_id");
			projeto = buscarProjeto(projeto_id);
			integrantes.addAll(Arrays.asList(extras.getString("projeto_membros").split("\\s*,\\s*")));
			txt_nome.setText(extras.getString("projeto_nome"));
			txt_prazo.setText(DateFormat.format("dd/MM/yyyy",
					extras.getLong("projeto_prazo")));
			
			tv_nome.setVisibility(View.VISIBLE);
			tv_prazo.setVisibility(View.VISIBLE);
			excluir.setVisibility(View.VISIBLE);
			contexto.setText(extras.getString("projeto_nome"));
		}
		integrantes.add(ParseUser.getCurrentUser().getObjectId().toString());
		buscarMembros();
		
		salvar = (ImageView) findViewById(R.id.ic_salvar);
		salvar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				txt_prazo = (EditText) findViewById(R.id.proj_prazo);
								
				if(projeto==null)
					projeto = new ParseObject("projeto");
				
				if(txt_nome.getText().toString().trim().equals(""))
				     txt_nome.setError( "Nome é obrigatorio!" );
				else if(txt_prazo.getText().toString().trim().equals(""))
					txt_prazo.setError( "Prazo é obrigatorio!" );
				else if(txt_prazo.getText().length() < 6)
					txt_prazo.setError( "Esse Prazo é inválido!" );
				else{
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					try {
						data_prazo = sdf.parse(txt_prazo.getText().toString());
						Calendar cal = Calendar.getInstance();
				        cal.setTime(data_prazo);
						if(cal.get(Calendar.YEAR)<1000){
							cal.add(Calendar.YEAR, 2000);
						}
						data_prazo = cal.getTime();
							
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					if(new Date().after(data_prazo)){
						txt_prazo.setError( "Esse prazo já passou!" );
					}
					else{
					
						projeto.put("nome", txt_nome.getText().toString());
						projeto.put("prazo", data_prazo);	
						projeto.put("membros", integrantes);
						projeto.put("status", "Em Aberto");	
						projeto.put("criador", ParseUser.getCurrentUser());
						projeto.saveInBackground();
						
						if(!is_edicao){
						 ParseObject feed = new ParseObject("feed");
						 feed.put("projeto", projeto);
						 feed.put("modelo", "NovoProjeto");
						 feed.put("membro", ParseUser.getCurrentUser());
						 feed.put("icone", "like");
						 feed.put("contador", 0);
						 feed.put("data", new Date());
						 feed.saveInBackground();
						}
						 
		
						Intent VoltarParaProjeto = new Intent(NovoProjeto.this,
								Projeto.class);
						NovoProjeto.this.startActivity(VoltarParaProjeto);
						NovoProjeto.this.finish();
					}
				}
			}
		});

		excluir.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				DeleteAll delete = new DeleteAll();
				delete.deleteProjeto(projeto);

				Intent IrParaProjeto = new Intent(NovoProjeto.this,
						Projeto.class);
				NovoProjeto.this.startActivity(IrParaProjeto);
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
		ParseUser.getQuery().orderByAscending("nome").findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> objects, com.parse.ParseException e) {
				

				integrantesAdapter = new ListaIntegranteGrupoAdapter(NovoProjeto.this, objects, integrantes);
				setListAdapter(integrantesAdapter);
				
				getListView().setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> adapter, View v,
							int position, long l) {
						
						ImageView selecionado = (ImageView) v.findViewById(R.id.ic_convite);
						ParseUser user = integrantesAdapter.getItem(position);
						
						if(!integrantes.contains(user.getObjectId())){
							selecionado.setBackgroundResource(R.drawable.ic_membro_check);
							integrantes.add(user.getObjectId());
						}else{
							selecionado.setBackgroundResource(R.drawable.ic_membro_uncheck);
							integrantes.remove(user.getObjectId());
						}
					
					}
				});
			}
			});
		
	}

}
