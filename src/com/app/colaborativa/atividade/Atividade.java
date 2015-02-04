package com.app.colaborativa.atividade;

import java.util.List;

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
	private String proj_nome, proj_id;
	private ListaAtividadeAdapter atividadeAdapter;
	private List<ParseObject> atividades;
	private Button bt_projeto, bt_feed;
	
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
				IrParaAddAtividade.putExtra("projeto_id", proj_id);
				IrParaAddAtividade.putExtra("projeto_nome", proj_nome);
				Atividade.this.startActivity(IrParaAddAtividade);
				Atividade.this.finish();
				
			}
	
		});
		
		 	bt_projeto = (Button) findViewById(R.id.button_projeto);		    
		    bt_projeto.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Intent IrParaProjeto = new Intent(Atividade.this, Projeto.class);
					Atividade.this.startActivity(IrParaProjeto);
					Atividade.this.finish();
					
				}
			});
		    bt_feed = (Button) findViewById(R.id.button_feeds);		    
		    bt_feed.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent IrParaFeed = new Intent(Atividade.this, Feed.class);
					Atividade.this.startActivity(IrParaFeed);
					Atividade.this.finish();
					
				}
			});
	}

	@Override
	public void onContentChanged() {
		// TODO Auto-generated method stub
		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			proj_id = extras.getString("projeto_id");
			proj_nome = extras.getString("projeto_nome");
		}
		
		TextView contexto = (TextView) findViewById(R.id.tv_contexto);
		contexto.setText(proj_nome+"> Atividades");
		new RemoteDataTask().execute();
		
		super.onContentChanged();
	}
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {

			ParseQuery<ParseObject> query = ParseQuery.getQuery("atividade");
//			query.whereEqualTo("projeto", proj_id);
			query.whereEqualTo("projeto_id", proj_id);

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
		              intent.putExtra("nome", ativ.getString("nome"));
		              intent.putExtra("prazo", ativ.getDate("prazo").getTime());
		              intent.putExtra("descricao", ativ.getString("descricao"));
		              intent.putExtra("status", ativ.getString("status"));
		              intent.putExtra("projeto_id", ativ.getString("projeto_id"));
		              intent.putExtra("atividade_id", ativ.getObjectId());
		              intent.putExtra("position", position);
		              startActivity(intent);
		              
		              //Toast.makeText(ListaProjeto.this,proj.getInt("nome"), Toast.LENGTH_SHORT).show();    
		            }
			        });
				}
			}
						
		}
}
