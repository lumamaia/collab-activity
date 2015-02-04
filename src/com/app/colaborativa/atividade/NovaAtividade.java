package com.app.colaborativa.atividade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utils.Mask;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.app.colaborativa.adapter.ListaProjetoAdapter;
import com.app.colaborativa.adapter.ListaConviteAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.app.colaborativa.R;

public class NovaAtividade extends ListActivity {
	
	private EditText nome;
	private TextView convite;
	private EditText prazo;
	private EditText descricao;
	private Date data_prazo;
	private Button salvar;
	private String proj_id, proj_nome;
	ParseObject projeto;
	List<ParseUser> convidados = new ArrayList<ParseUser>();
	private Button bt_projeto, bt_feed, bt_convidar;
	ListaConviteAdapter responsavelAdapter;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nova_atividade);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			proj_id = extras.getString("projeto_id");
			proj_nome = extras.getString("projeto_nome");
			findProjeto();
			
		}
		
		prazo = (EditText) findViewById(R.id.atividade_prazo);
		prazo.addTextChangedListener(Mask.insert("##/##/####", prazo));
		
		salvar = (Button) findViewById(R.id.bt_salvar);
		salvar.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					nome = (EditText) findViewById(R.id.atividade_nome);
					prazo = (EditText) findViewById(R.id.atividade_prazo);
					descricao = (EditText) findViewById(R.id.atividade_descricao);
					
					SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
					try {
						data_prazo = sdf.parse(prazo.getText().toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ParseObject atividade = new ParseObject("atividade");
					atividade.put("nome", nome.getText().toString());
					atividade.put("prazo", data_prazo);
					atividade.put("status", "Em Aberto");
					atividade.put("descricao", descricao.getText().toString());
					atividade.put("projeto_id", proj_id);
					atividade.put("criador", ParseUser.getCurrentUser());
					if(projeto != null)
						atividade.put("projeto", projeto);
					atividade.saveInBackground();
					
					 ParseObject feed = new ParseObject("feed");
					 feed.put("atividade", atividade);
					 feed.put("modelo", "NovaAtividade");
					 feed.put("icone", "like");
					 feed.put("contador", 0);
					 feed.put("data", new Date());
					 feed.saveInBackground();
					 
					 for (ParseUser membro : convidados) {
						 
						 ParseObject convite = new ParseObject("convite_responsavel");
						 convite.put("atividade", atividade);
						 convite.put("responsavel", membro);
						 convite.put("usuario", ParseUser.getCurrentUser());
						 convite.put("status", "pendente");
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
					
					Intent VoltarParaAtividade = new Intent(NovaAtividade.this, Atividade.class);
					VoltarParaAtividade.putExtra("projeto_id", proj_id);
					VoltarParaAtividade.putExtra("projeto_nome", proj_id);
					NovaAtividade.this.startActivity(VoltarParaAtividade);
					NovaAtividade.this.finish();
					
				}
			});
		
		bt_projeto = (Button) findViewById(R.id.button_projeto);		    
	    bt_projeto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent IrParaProjeto = new Intent(NovaAtividade.this, Projeto.class);
				NovaAtividade.this.startActivity(IrParaProjeto);
				NovaAtividade.this.finish();
				
			}
		});
	    bt_feed = (Button) findViewById(R.id.button_feeds);		    
	    bt_feed.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent IrParaFeed = new Intent(NovaAtividade.this, Feed.class);
				NovaAtividade.this.startActivity(IrParaFeed);
				NovaAtividade.this.finish();
				
			}
		});
		
	}
	
	public void findProjeto() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("projeto");
		query.include("membros");
		query.getInBackground(proj_id, new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, com.parse.ParseException e) {
				if (e == null) {
					projeto = object;
					List<Object> membro = projeto.getList("membros");
					 if(membro!=null){
					
						ParseUser.getQuery().whereContainedIn("objectId", projeto.getList("membros")).findInBackground(new FindCallback<ParseUser>() {
							
							@Override
							public void done(List<ParseUser> objects, com.parse.ParseException e) {
								responsavelAdapter = new ListaConviteAdapter(NovaAtividade.this, objects);
								setListAdapter(responsavelAdapter);
								
								getListView().setOnItemClickListener(new OnItemClickListener() {

									public void onItemClick(AdapterView<?> adapter, View v,
											int position, long l) {
										
										ParseUser user = responsavelAdapter.getItem(position);
										if(!convidados.contains(user))
											convidados.add(responsavelAdapter.getItem(position));
										
										ImageButton conviteIcone = (ImageButton) v.findViewById(R.id.ic_convite);
										conviteIcone.setBackgroundResource(R.drawable.ic_status_finalizada);
										
									}
								});
								
				
								
							}
						});
					 }
				}
			}
		});
	}
}


