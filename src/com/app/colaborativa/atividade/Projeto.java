package com.app.colaborativa.atividade;

import java.util.ArrayList;
import java.util.List;

import utils.MenuAction;
import utils.PullParse;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.app.colaborativa.adapter.ListaProjetoAdapter;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.app.colaborativa.R;

public class Projeto extends ListActivity {

	private List<ParseObject> projetos;
	public List<String> lista_projeto = new ArrayList<String>();
	private ListaProjetoAdapter projetoAdapter;
	private ImageButton add_projeto;
	private Button bt_feed;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_projeto);

		bt_feed = (Button) findViewById(R.id.button_feeds);
		MenuAction menu = new MenuAction();	
		menu.MapearFeed(this, bt_feed);
		
		// + Projeto
		add_projeto = (ImageButton) findViewById(R.id.add_projeto);
		add_projeto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent IrParaAddProjeto = new Intent(Projeto.this,
						NovoProjeto.class);
				Projeto.this.startActivityForResult(IrParaAddProjeto, 1);
//				Projeto.this.finish();

			}

		});
	}

	@Override
	public void onContentChanged() {
		
		new RemoteDataTask().execute();
		
		super.onContentChanged();
	}

	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {

			ParseQuery<ParseObject> query = ParseQuery.getQuery("projeto");
			query.whereEqualTo("membros", ParseUser.getCurrentUser().getObjectId());
			query.include("criador");
			query.orderByDescending("prazo");

			try {
				projetos = query.find();
			} catch (ParseException e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (projetos != null) {
				
				PullParse.setListProjeto(projetos);
							
				projetoAdapter = new ListaProjetoAdapter(Projeto.this, projetos);
				setListAdapter(projetoAdapter);

				getListView().setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> adapter, View v,
							int position, long l) {
						ParseObject proj = projetoAdapter.getItem(position);
						
						ParsePush.subscribeInBackground(proj.getObjectId().toString());
						
						Intent intent = new Intent(Projeto.this,
								Atividade.class);
						intent.putExtra("projeto_nome", proj.getString("nome"));
						intent.putExtra("projeto_prazo", proj.getDate("prazo").getTime());
						intent.putExtra("projeto_id", proj.getObjectId());
						intent.putExtra("projeto_membros", proj.getList("membros").toString());
						intent.putExtra("position", position);
						startActivity(intent);

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