package com.app.colaborativa.atividade;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;

import com.app.colaborativa.adapter.ListaMembroAdapter;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.app.colaborativa.R;

public class BuscarMembro extends Activity{

	private Button salvar;
	public String contato_selecionado;
	public static List<ParseUser> todos_membros;
	private SearchView sv_buscar;
	public EditText busca_contato;
	public ListaMembroAdapter adapterContato;
	public ListView listTelefone;
	ArrayList<String> contato = new ArrayList<String>();

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buscar_contato);
		new RemoteDataTask().execute();
		
		listTelefone = (ListView) findViewById(R.id.lista_contato);
//		sv_buscar=(SearchView) findViewById(R.id.sv_buscar);
		busca_contato = (EditText) findViewById(R.id.pesquisar_contato);
		
		busca_contato.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				adapterContato.getFilter().filter(s);

				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		} );

		
			

		salvar = (Button) findViewById(R.id.bt_cancelar);
		salvar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent VoltarParaNovoProjeto = new Intent(BuscarMembro.this,
						NovoProjeto.class);
				BuscarMembro.this.startActivity(VoltarParaNovoProjeto);
				BuscarMembro.this.finish();

			}
		});

	}

	
//	public void findProjeto() {
//		ParseQuery<ParseObject> query = ParseQuery.getQuery("projeto");
//
//		query.getInBackground(proj_id, new GetCallback<ParseObject>() {
//
//			@Override
//			public void done(ParseObject object, com.parse.ParseException e) {
//				if (e == null) {
//					projeto = object;
//				}
//			}
//		});
//	}

	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {

				try {
					todos_membros = ParseUser.getQuery().find();
				} catch (com.parse.ParseException e) {

					e.printStackTrace();
				}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (todos_membros != null) {
//			for (ParseObject membro : membros) {
//				contato.add(membro.getString("nome")+" - "+membro.getString("celular"));
//			}
//			adapterContato = new ArrayAdapter<String>(BuscarMembro.this,
//					android.R.layout.simple_list_item_1, contato);
//			listTelefone.setAdapter(adapterContato);
//						
			listTelefone = (ListView) findViewById(R.id.lista_telefone);			
			adapterContato = new ListaMembroAdapter(BuscarMembro.this, todos_membros);
			listTelefone.setAdapter(adapterContato);
			listTelefone.setTextFilterEnabled(true);


			listTelefone.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> adapter, View v,
						int position, long l) {
					ParseObject membro = adapterContato.getItem(position);
					Intent intent = new Intent(BuscarMembro.this,
							NovoProjeto.class);
					intent.putExtra("nome", membro.getString("nome"));
					intent.putExtra("user_id", membro.getObjectId());
					intent.putExtra("celular", membro.getString("celular"));
					startActivity(intent);

					// Toast.makeText(ListaProjeto.this,proj.getInt("nome"),
					// Toast.LENGTH_SHORT).show();
				}
			});
		}
}
	}
	
	
}

