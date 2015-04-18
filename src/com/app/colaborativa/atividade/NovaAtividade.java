package com.app.colaborativa.atividade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import utils.Mask;
import utils.MenuAction;
import utils.PullParse;
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
	private ImageView salvar;
	private String proj_id, proj_nome;
	private Long proj_prazo;
	ParseObject projeto;
	List<ParseUser> convidados = new ArrayList<ParseUser>();
	private Button bt_projeto, bt_feed, bt_contexto;
	ListaConviteAdapter responsavelAdapter;
	private Exception exp;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nova_atividade);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			proj_id = extras.getString("projeto_id");
			proj_nome = extras.getString("projeto_nome");
			proj_prazo = extras.getLong("projeto_prazo");
			findProjeto();

		}

		bt_projeto = (Button) findViewById(R.id.button_projeto);
		bt_feed = (Button) findViewById(R.id.button_feeds);
		MenuAction menu = new MenuAction();
		menu.MapearProjeto(this, bt_projeto);
		menu.MapearFeed(this, bt_feed);

		bt_contexto = (Button) findViewById(R.id.button_contexto);
		bt_contexto.setText("Projeto " + proj_nome + " > Atividades");
		bt_contexto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED, returnIntent);
				finish();
			}
		});

		prazo = (EditText) findViewById(R.id.atividade_prazo);
		prazo.addTextChangedListener(Mask.insert("##/##/####", prazo));

		salvar = (ImageView) findViewById(R.id.bt_salvar);
		salvar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				exp = null;
				boolean com_prazo = true;
				nome = (EditText) findViewById(R.id.atividade_nome);
				prazo = (EditText) findViewById(R.id.atividade_prazo);
				descricao = (EditText) findViewById(R.id.atividade_descricao);

				if (prazo.getText().toString().trim().equals("")) {
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(proj_prazo);
					data_prazo = cal.getTime();
					com_prazo = false;
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					sdf.setLenient(false);
					try {
						data_prazo = sdf.parse(prazo.getText().toString());
						Calendar cal = Calendar.getInstance();
						cal.setTime(data_prazo);
						if (cal.get(Calendar.YEAR) < 1000) {
							cal.add(Calendar.YEAR, 2000);
						}
						data_prazo = cal.getTime();

					} catch (ParseException e) {
						exp = e;
					}
				}

				if (nome.getText().toString().trim().equals("")) {
					nome.setError("Nome é obrigatorio!");
				} else if (prazo.getText().length() < 8 && com_prazo) {
					prazo.setError("Esse Prazo é inválido!");
				} else if (exp != null) {
					prazo.setError("Esse Prazo é inválido!");
				} else if (new Date().after(data_prazo)) {
					prazo.setError("Esse prazo já passou!");
				} else {

					ParseObject atividade = new ParseObject("atividade");
					atividade.put("nome", nome.getText().toString());
					atividade.put("prazo", data_prazo);
					atividade.put("status", "Em Aberto");
					atividade.put("descricao", descricao.getText().toString());
					atividade.put("projeto_id", proj_id);
					atividade.put("criador", ParseUser.getCurrentUser());
					if (projeto != null)
						atividade.put("projeto", projeto);
					atividade.saveInBackground();

					PullParse.saveFeed(atividade, projeto, "NovaAtividade",
							"like", null);

					for (ParseUser membro : convidados) {

						ParseObject convite = new ParseObject(
								"convite_responsavel");
						convite.put("atividade", atividade);
						convite.put("responsavel", membro);
						convite.put("usuario", ParseUser.getCurrentUser());
						convite.put("status", "Convidado");
						convite.saveInBackground();

						PullParse.saveFeed(atividade, projeto,
								"InformativoSugestaoResponsavel", "like",
								membro);

					}
					Intent returnIntent = new Intent();
					setResult(RESULT_OK, returnIntent);
					finish();
				}

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
					if (membro != null) {

						ParseUser
								.getQuery()
								.whereContainedIn("objectId",
										projeto.getList("membros"))
								.findInBackground(
										new FindCallback<ParseUser>() {

											@Override
											public void done(
													List<ParseUser> objects,
													com.parse.ParseException e) {
												responsavelAdapter = new ListaConviteAdapter(
														NovaAtividade.this,
														objects);
												setListAdapter(responsavelAdapter);

												getListView()
														.setOnItemClickListener(
																new OnItemClickListener() {

																	public void onItemClick(
																			AdapterView<?> adapter,
																			View v,
																			int position,
																			long l) {

																		ImageView conviteIcone = (ImageView) v
																				.findViewById(R.id.ic_convite);
																		ParseUser user = responsavelAdapter
																				.getItem(position);
																		if (!convidados
																				.contains(user)) {
																			convidados
																					.add(user);
																			conviteIcone
																					.setBackgroundResource(R.drawable.ic_membro_check);
																		} else {
																			convidados
																					.remove(user);
																			conviteIcone
																					.setBackgroundResource(R.drawable.ic_membro_uncheck);
																		}
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
