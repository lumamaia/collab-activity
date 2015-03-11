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

import com.app.colaborativa.adapter.ListaFeedAdapter;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.app.colaborativa.R;

public class Feed extends ListActivity {

	private ListaFeedAdapter feedAdapter;
	private List<ParseObject> feeds;
	private Button bt_projeto;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed);

		TextView contexto = (TextView) findViewById(R.id.tv_contexto);
		contexto.setText("Notificações");
		
		new RemoteDataTask().execute();
		

		bt_projeto = (Button) findViewById(R.id.button_projeto);
		bt_projeto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent VoltarParaProjeto = new Intent(Feed.this, Projeto.class);
				Feed.this.startActivity(VoltarParaProjeto);
				Feed.this.finish();

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

			ParseQuery<ParseObject> query = ParseQuery.getQuery("feed");
			query.include("atividade");
			query.include("projeto");
			query.include("membro");
			query.orderByDescending("data");

			try {
				feeds = query.find();
			} catch (ParseException e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (feeds != null) {

				feedAdapter = new ListaFeedAdapter(Feed.this, feeds);
				setListAdapter(feedAdapter);

				getListView().setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> adapter, View v,
							int position, long l) {
						ParseObject feed = feedAdapter.getItem(position);
						Intent intent;
						if(feed.getString("modelo").equals("NovoProjeto")){
							intent = new Intent(Feed.this, Atividade.class);
						}else{
							intent = new Intent(Feed.this, GerenciarAtividade.class);
							intent.putExtra("atividade_id", feed.getParseObject("atividade").getObjectId());
						}
						
						intent.putExtra("projeto_nome",feed.getParseObject("projeto").getString("nome"));
						intent.putExtra("projeto_membros", feed.getParseObject("projeto").getList("membros").toString());
						startActivity(intent);
					}
				});
			}
		}

	}
}
