package com.app.colaborativa.atividade;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.app.colaborativa.adapter.ListaProjetoAdapter;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.app.colaborativa.R;

public class ListaProjetoOLD extends ListActivity{
	
	private List<ParseObject> projetos;
	public List<String> lista_projeto = new ArrayList<String>();
	private ListaProjetoAdapter projetoAdapter;
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_projeto);
		
		new RemoteDataTask().execute();
	
	}

	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {

			ParseQuery<ParseObject> query = ParseQuery.getQuery("projeto");
			query.orderByDescending("prazo");

			try {
				projetos = query.find();
			} catch (ParseException e) {

			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
				           
			if (projetos != null) {
			
				projetoAdapter = new ListaProjetoAdapter(ListaProjetoOLD.this, projetos);
				setListAdapter(projetoAdapter);
				
				getListView().setOnItemClickListener(new OnItemClickListener() {
					 
					public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
		              ParseObject proj = projetoAdapter.getItem(position);
		              Intent intent = new Intent(ListaProjetoOLD.this,Atividade.class);
		              intent.putExtra("nome", proj.getString("nome").toString());
		              intent.putExtra("position", position);
		              startActivity(intent);
		              
		              //Toast.makeText(ListaProjeto.this,proj.getInt("nome"), Toast.LENGTH_SHORT).show();    
		            }
			        });
				}
			}
						
		}
	
	
}
		


