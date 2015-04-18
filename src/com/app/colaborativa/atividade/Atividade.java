package com.app.colaborativa.atividade;

import java.util.List;

import utils.MenuAction;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.colaborativa.CollabActivityProjectActivity;
import com.app.colaborativa.adapter.ListaAtividadeAdapter;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.app.colaborativa.R;

public class Atividade extends ListActivity {
	
	private ImageButton add_atividade;
	private String projeto_nome, projeto_id, proj_membros;
	private long projeto_prazo;
	private ListaAtividadeAdapter atividadeAdapter;
	private List<ParseObject> atividades;
	private Button bt_projeto, bt_feed, bt_contexto;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_atividade);

		// + Atividade
		add_atividade = (ImageButton) findViewById(R.id.add_atividade);
		add_atividade.setOnClickListener(new View.OnClickListener() {
						
			@Override
			public void onClick(View v) {
				
				Intent IrParaAddAtividade = new Intent(Atividade.this, NovaAtividade.class);
				IrParaAddAtividade.putExtra("projeto_id", projeto_id);
				IrParaAddAtividade.putExtra("projeto_nome", projeto_nome);
				IrParaAddAtividade.putExtra("projeto_prazo", projeto_prazo);
				Atividade.this.startActivityForResult(IrParaAddAtividade, 1);
//				Atividade.this.finish();
				
			}
	
		});
		
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

	@Override
	public void onContentChanged() {
		// TODO Auto-generated method stub
		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projeto_id = extras.getString("projeto_id");
			projeto_nome = extras.getString("projeto_nome");
			proj_membros = extras.getString("projeto_membros");
			projeto_prazo = extras.getLong("projeto_prazo");
			
		}
		
		bt_contexto = (Button) findViewById(R.id.button_contexto);
		bt_contexto.setText("Projeto "+projeto_nome);
		new RemoteDataTask().execute();
		
		super.onContentChanged();
	}
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {

			ParseQuery<ParseObject> query = ParseQuery.getQuery("atividade");
			query.orderByDescending("prazo");
			query.whereEqualTo("projeto_id", projeto_id);
			query.include("criador");

			try {
				atividades = query.find();
			} catch (ParseException e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
				           
			if (atividades != null) {
			
				atividadeAdapter = new ListaAtividadeAdapter(Atividade.this, atividades);
				setListAdapter(atividadeAdapter);
				
				getListView().setOnItemClickListener(new OnItemClickListener() {
					 
					public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
		              ParseObject ativ = atividadeAdapter.getItem(position);
		              Intent intent = new Intent(Atividade.this,GerenciarAtividade.class);
		              intent.putExtra("projeto_id", ativ.getString("projeto_id"));
		              intent.putExtra("projeto_nome", projeto_nome);
		              intent.putExtra("projeto_membros", proj_membros);
//		              intent.putExtra("projeto_prazo", projeto_prazo);
		              intent.putExtra("atividade_id", ativ.getObjectId());
		              startActivity(intent);
		              
		              //Toast.makeText(ListaProjeto.this,proj.getInt("nome"), Toast.LENGTH_SHORT).show();    
		            }
			        });
				}
			}
						
		}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (requestCode == 1) {
	        if(resultCode == -1){
	        	onContentChanged();
	        }
	    }
	}
}
