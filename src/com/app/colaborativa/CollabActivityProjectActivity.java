package com.app.colaborativa;

import java.util.List;

import utils.PullParse;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.colaborativa.atividade.Feed;
import com.app.colaborativa.atividade.Projeto;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class CollabActivityProjectActivity extends Activity {

	Button bt_projeto, bt_feed;
	EditText txt_main_apelido, txt_main_celular;
	public static String celular, celular_cadastrado, nome_cadastrado;
	private List<ParseObject> membro;
	public ParseUser currentUser;
	public ParseObject ultimoAcesso;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		currentUser = ParseUser.getCurrentUser();
		

//		celular_cadastrado = currentUser.getString("celular");
		nome_cadastrado = currentUser.getString("nome");
		txt_main_apelido = (EditText) findViewById(R.id.main_apelido);
//		txt_main_celular = (EditText) findViewById(R.id.main_celular);

		// txt_main_celular.addTextChangedListener(Mask.insert("### (##) #####-####",
		// txt_main_celular));
		if (nome_cadastrado != null) {
			txt_main_apelido = (EditText) findViewById(R.id.main_apelido);
			txt_main_apelido.setText(nome_cadastrado);
		}
//		if (celular_cadastrado != null) {
//			txt_main_celular = (EditText) findViewById(R.id.main_celular);
//			txt_main_celular.setText(celular_cadastrado);
//		} else {
//			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//			celular = tm.getLine1Number();
//			txt_main_celular.setText(celular);
//			currentUser.put("celular", celular);
//		}

		try {
			ultimoAcesso = ParseQuery.getQuery("ultimo_feed").getFirst();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		if(PullParse.isNotificacao())
		{
			PullParse.setUltimoFeed(ultimoAcesso);
			PullParse.setUltimaVisita(currentUser.getDate("ultimoAcessoFeed"));
			PullParse.setUltimaAtualizacaoFeed(ultimoAcesso.getDate("data"));
			currentUser.increment("acessos");
			currentUser.saveInBackground();

			Intent irFeed = new Intent(
					CollabActivityProjectActivity.this, Feed.class);
			CollabActivityProjectActivity.this
					.startActivity(irFeed);
		}
		bt_projeto = (Button) findViewById(R.id.bt_projeto);
		bt_projeto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (txt_main_apelido.getText().toString().trim().equals(""))
					txt_main_apelido.setError("Apelido é obrigatorio!");
				else {

					txt_main_apelido = (EditText) findViewById(R.id.main_apelido);
					nome_cadastrado = txt_main_apelido.getText().toString();
					PullParse.setUltimoFeed(ultimoAcesso);
					PullParse.setUltimaVisita(currentUser.getDate("ultimoAcessoFeed"));
					PullParse.setUltimaAtualizacaoFeed(ultimoAcesso.getDate("data"));
					currentUser.increment("acessos");
					currentUser.put("nome", nome_cadastrado);
					currentUser.put("instalacao", ParseInstallation.getCurrentInstallation());
					currentUser.saveInBackground();

					Intent VoltarParaProjeto = new Intent(
							CollabActivityProjectActivity.this, Projeto.class);
					CollabActivityProjectActivity.this
							.startActivity(VoltarParaProjeto);
				}
			}

		});

	}
}
