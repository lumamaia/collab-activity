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
import utils.MenuAction;
import utils.PullParse;
import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.colaborativa.R;
import com.app.colaborativa.adapter.ListaIntegranteGrupoAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NovoProjeto extends ListActivity {

	private EditText txt_nome, txt_prazo;
	private TextView tv_nome, tv_prazo, tv_list;
	private String projeto_id;
	private boolean is_edicao;
	private Date data_prazo;
	private Button bt_projeto, bt_feed, bt_contexto;
	private ImageView salvar, excluir;
	private ListaIntegranteGrupoAdapter integrantesAdapter;

	public EditText busca_contato;
	public ArrayAdapter<String> adapter;
	public List<ParseObject> membros = new ArrayList<ParseObject>();
	public List<String> integrantes = new ArrayList<String>();
	public ParseObject projeto;
	private Exception exp;
	

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
		tv_list = (TextView) findViewById(R.id.tv_list);
		excluir = (ImageView) findViewById(R.id.ic_excluir);
		bt_contexto = (Button) findViewById(R.id.button_contexto);
		TextView contexto = (TextView) findViewById(R.id.tv_contexto);	
		contexto.setText("Novo Projeto");
		bt_contexto.setText("Projetos");
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
			bt_contexto.setText("Projeto " + extras.getString("projeto_nome"));
		}
		integrantes.add(ParseUser.getCurrentUser().getObjectId().toString());
		buscarMembros();
		
		bt_projeto = (Button) findViewById(R.id.button_projeto);
		bt_feed = (Button) findViewById(R.id.button_feeds);
		MenuAction menu = new MenuAction();
		menu.MapearProjeto(this, bt_projeto);		
		menu.MapearFeed(this, bt_feed);
		
		bt_contexto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED,returnIntent);
				finish();
			}
		});
		
		salvar = (ImageView) findViewById(R.id.ic_salvar);
		salvar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				exp = null;
				txt_prazo = (EditText) findViewById(R.id.proj_prazo);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				sdf.setLenient (false);
				try {
					data_prazo = sdf.parse(txt_prazo.getText().toString());
					Calendar cal = Calendar.getInstance();
			        cal.setTime(data_prazo);
					if(cal.get(Calendar.YEAR)<1000){
						cal.add(Calendar.YEAR, 2000);
					}
					data_prazo = cal.getTime();
						
				} catch (ParseException e) {
					exp = e;
				}
				
								
				if(projeto==null)
					projeto = new ParseObject("projeto");
				
				if(txt_nome.getText().toString().trim().equals(""))
				     txt_nome.setError( "Nome � obrigatorio!" );
				else if(integrantes.size() ==0){
					tv_list.setError("Membro � obrigatorio!");
				}
				else if(txt_prazo.getText().toString().trim().equals(""))
					txt_prazo.setError( "Prazo � obrigatorio!" );
				else if(txt_prazo.getText().length() < 8)
					txt_prazo.setError( "Esse Prazo � inv�lido!" );
				else if(exp != null)
					txt_prazo.setError( "Esse Prazo � inv�lido!" );
				else if(new Date().after(data_prazo))
						txt_prazo.setError( "Esse prazo j� passou!" );
				else{
					
						projeto.put("nome", txt_nome.getText().toString());
						projeto.put("prazo", data_prazo);	
						projeto.put("membros", integrantes);
						projeto.put("status", "Em Aberto");	
						projeto.put("criador", ParseUser.getCurrentUser());
						projeto.saveInBackground(new SaveCallback() {
							
							@Override
							public void done(com.parse.ParseException e) {
								
								if(!is_edicao){
									ParsePush.subscribeInBackground("Proj_"+projeto.getObjectId().toString());
									PullParse.saveFeed(null, projeto, "NovoProjeto", "like", ParseUser.getCurrentUser());
								}
								
							}
						});
		
						Intent returnIntent = new Intent();
						setResult(RESULT_OK,returnIntent);
						finish();
					}
				
			}
		});

		excluir.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {


			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					NovoProjeto.this, AlertDialog.THEME_HOLO_LIGHT);

			alertDialog.setTitle("Confirma��o de Exclus�o");

			// Setting Dialog Message
			final TextView input = new TextView(NovoProjeto.this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			input.setLayoutParams(lp);
			input.setText("Tem certeza que deseja EXCLUIR a atividade?");
			alertDialog.setView(input);

			alertDialog.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.cancel();
						}
					});

			alertDialog.setPositiveButton("Excluir",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {

							
							DeleteAll delete = new DeleteAll();
							delete.deleteProjeto(projeto);
						
							Intent returnIntent = new Intent();
							setResult(RESULT_OK,returnIntent);
							finish();
							
						}

					});
			alertDialog.show();
			
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
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.include("instalacao");
		query.orderByAscending("nome");
		query.findInBackground(new FindCallback<ParseUser>() {

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
