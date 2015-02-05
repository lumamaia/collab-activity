package com.app.colaborativa;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.colaborativa.atividade.Feed;
import com.app.colaborativa.atividade.Projeto;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class CollabActivityProjectActivity extends Activity {
	
	Button bt_projeto, bt_feed;
	EditText txt_main_apelido, txt_main_celular;
	public static String celular, celular_cadastrado, nome_cadastrado;
	private List<ParseObject> membro;
	public ParseUser currentUser;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ParseAnalytics.trackAppOpened(getIntent());
		
		currentUser = ParseUser.getCurrentUser();
		celular_cadastrado = currentUser.getString("celular");
		nome_cadastrado = currentUser.getString("nome");
		txt_main_apelido = (EditText) findViewById(R.id.main_apelido);
		txt_main_celular = (EditText) findViewById(R.id.main_celular);

		//txt_main_celular.addTextChangedListener(Mask.insert("### (##) #####-####", txt_main_celular));
		if(nome_cadastrado != null)
		{
			txt_main_apelido = (EditText) findViewById(R.id.main_apelido);
			txt_main_apelido.setText(nome_cadastrado);
		}
		if(celular_cadastrado != null){
			txt_main_celular = (EditText) findViewById(R.id.main_celular);
			txt_main_celular.setText(celular_cadastrado);
		}
		else{
			TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
			celular = tm.getLine1Number();
			txt_main_celular.setText(celular);
			currentUser.put("celular", celular);
		}
		
		
		
		bt_projeto = (Button) findViewById(R.id.bt_projeto);		    
	    bt_projeto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				exitThisPage();
				Intent VoltarParaProjeto = new Intent(CollabActivityProjectActivity.this, Projeto.class);
				CollabActivityProjectActivity.this.startActivity(VoltarParaProjeto);
				CollabActivityProjectActivity.this.finish();
				
			}
		});
	    
	    bt_feed = (Button) findViewById(R.id.bt_feeds);		    
	    bt_feed.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				exitThisPage();
				Intent IrParaFeed = new Intent(CollabActivityProjectActivity.this, Feed.class);
				CollabActivityProjectActivity.this.startActivity(IrParaFeed);
				CollabActivityProjectActivity.this.finish();
				
			}
		});
	}
	

	public void exitThisPage() {
		

			txt_main_apelido = (EditText) findViewById(R.id.main_apelido);
			nome_cadastrado = txt_main_apelido.getText().toString();
			currentUser.increment("acessos");	
			if(!nome_cadastrado.isEmpty()){
				currentUser.put("nome", nome_cadastrado);
				currentUser.saveInBackground();				
			}
	}
}
				           
