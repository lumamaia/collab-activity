package com.app.colaborativa.atividade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utils.Mask;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.colaborativa.adapter.ListaComentarioAdapter;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.app.colaborativa.R;

public class GerenciarAtividade extends ListActivity {

	private EditText txt_nome, txt_comentario, txt_prazo, txt_descricao;
	private Date data_prazo;
	private Button salvar, salvar_comentario, add_comentario, bt_projeto,
			bt_feed, finalizar_atividade, bt_responsavel, bt_ser_responsavel;
	private ImageView ic_add_comentario, ic_add_responsavel, ic_finalizar,
			ic_finalizada;
	public ParseObject atividade1;
	public List<ParseObject> comentarios = null;
	List<ParseObject> responsaveis;
	public ListaComentarioAdapter comentarioAdapter;
	public String atividade_id, nome, prazo, descricao, projeto_id,
			projeto_nome, projeto_membros;
	public ViewFlipper viewFlipper;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gerenciar_atividade);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			atividade_id = extras.getString("atividade_id");
			projeto_id = extras.getString("projeto_id");
			projeto_nome = extras.getString("projeto_nome");
			projeto_membros = extras.getString("projeto_membros");
			this.findAtividade();
		}

		new RemoteDataTask().execute();

		//

		txt_prazo = (EditText) findViewById(R.id.atividade_prazo);
		txt_prazo.addTextChangedListener(Mask.insert("##/##/####", txt_prazo));

		// add_comentario = (Button) findViewById(R.id.bt_add_comentario);
		ic_add_comentario = (ImageView) findViewById(R.id.ic_add_comentario);
		ic_add_comentario.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						GerenciarAtividade.this, AlertDialog.THEME_HOLO_LIGHT);

				alertDialog.setTitle("Comentario");

				// Setting Dialog Message
				final EditText input = new EditText(GerenciarAtividade.this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				input.setLayoutParams(lp);
				alertDialog.setView(input);
				alertDialog.setIcon(R.drawable.ic_comentario);

				alertDialog.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

				alertDialog.setPositiveButton("Enviar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								ParseObject comentario = new ParseObject(
										"comentario");
								comentario.put("comentario", input.getText()
										.toString());
								comentario.put("atividade_id", atividade_id);
								comentario.put("atividade", atividade1);
								comentario.put("data", new Date());
								comentario.put("usuario",
										ParseUser.getCurrentUser());
								comentario.saveInBackground();

								ParseObject feed = new ParseObject("feed");
								feed.put("atividade", atividade1);
								// feed.put("projeto", projeto_id);
								feed.put("modelo", "NovoComentario");
								feed.put("icone", "like");
								feed.put("contador", 0);
								feed.put("data", new Date());
								feed.saveInBackground();

								new RemoteDataTask().execute();

								// viewFlipper.showPrevious();
							}
						});

				alertDialog.show();

			}
		});

		// finalizar_atividade = (Button) findViewById(R.id.bt_finalizar);
		ic_finalizar = (ImageView) findViewById(R.id.ic_finalizar);
		ic_finalizar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				txt_nome = (EditText) findViewById(R.id.atividade_nome);
				txt_prazo = (EditText) findViewById(R.id.atividade_prazo);
				txt_descricao = (EditText) findViewById(R.id.atividade_descricao);

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				try {
					data_prazo = sdf.parse(txt_prazo.getText().toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ParseObject atividade = new ParseObject("atividade");
				atividade = atividade1;
				atividade.put("nome", txt_nome.getText().toString());
				atividade.put("prazo", data_prazo);
				atividade.put("status", "Finalizada");
				atividade.put("descricao", txt_descricao.getText().toString());
				atividade.saveInBackground(new SaveCallback() {

					@Override
					public void done(com.parse.ParseException e) {

						ParseObject feed3 = new ParseObject("feed");
						feed3.put("atividade", atividade1);
						feed3.put("modelo", "AtividadeFinalizada");
						feed3.put("icone", "like");
						feed3.put("contador", 0);
						feed3.put("data", new Date());
						feed3.saveInBackground();

						Intent VoltarParaAtividade = new Intent(
								GerenciarAtividade.this, Atividade.class);
						VoltarParaAtividade.putExtra("projeto_id", projeto_id);
						VoltarParaAtividade.putExtra("projeto_nome",
								projeto_nome);
						VoltarParaAtividade.putExtra("atividade_id",
								atividade_id);
						VoltarParaAtividade.putExtra("projeto_membros",
								projeto_membros);
						GerenciarAtividade.this
								.startActivity(VoltarParaAtividade);
						GerenciarAtividade.this.finish();

					}
				});

			}
		});

		salvar = (Button) findViewById(R.id.bt_salvar);
		salvar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				txt_nome = (EditText) findViewById(R.id.atividade_nome);
				txt_prazo = (EditText) findViewById(R.id.atividade_prazo);
				txt_descricao = (EditText) findViewById(R.id.atividade_descricao);

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				try {
					data_prazo = sdf.parse(txt_prazo.getText().toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ParseObject atividade = new ParseObject("atividade");
				atividade = atividade1;
				atividade.put("nome", txt_nome.getText().toString());
				atividade.put("prazo", data_prazo);
				// atividade.put("projeto_id", projeto_id);
				atividade.put("descricao", txt_descricao.getText().toString());
				atividade.saveInBackground(new SaveCallback() {

					@Override
					public void done(com.parse.ParseException e) {

						Intent VoltarParaAtividade = new Intent(
								GerenciarAtividade.this, Atividade.class);
						VoltarParaAtividade.putExtra("projeto_id", projeto_id);
						VoltarParaAtividade.putExtra("projeto_nome",
								projeto_nome);
						VoltarParaAtividade.putExtra("atividade_id",
								atividade_id);
						VoltarParaAtividade.putExtra("projeto_membros",
								projeto_membros);
						GerenciarAtividade.this
								.startActivity(VoltarParaAtividade);
						GerenciarAtividade.this.finish();

					}
				});

			}
		});
		// salvar_comentario = (Button) findViewById(R.id.bt_salvar_comentario);
		// salvar_comentario.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// txt_comentario = (EditText) findViewById(R.id.atividade_comentario);
		//
		// //data_prazo = sdf.getCalendar().getTime();
		// ParseObject comentario = new ParseObject("comentario");
		// comentario.put("comentario", txt_comentario.getText().toString());
		// comentario.put("atividade_id", atividade_id);
		// comentario.put("atividade", atividade1);
		// comentario.put("data", new Date());
		// comentario.put("usuario", ParseUser.getCurrentUser());
		// comentario.saveInBackground();
		//
		// ParseObject feed = new ParseObject("feed");
		// feed.put("atividade", atividade1);
		// // feed.put("projeto", projeto_id);
		// feed.put("modelo", "NovoComentario");
		// feed.put("icone", "like");
		// feed.put("contador", 0);
		// feed.put("data", new Date());
		// feed.saveInBackground();
		//
		// new RemoteDataTask().execute();
		// txt_comentario = null;
		// viewFlipper.showPrevious();
		//
		// }
		// });

		bt_projeto = (Button) findViewById(R.id.button_projeto);
		bt_projeto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent VoltarParaProjeto = new Intent(GerenciarAtividade.this,
						Projeto.class);
				GerenciarAtividade.this.startActivity(VoltarParaProjeto);
				GerenciarAtividade.this.finish();

			}
		});
		bt_feed = (Button) findViewById(R.id.button_feeds);
		bt_feed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent IrParaFeed = new Intent(GerenciarAtividade.this,
						Feed.class);
				GerenciarAtividade.this.startActivity(IrParaFeed);
				GerenciarAtividade.this.finish();

			}
		});

		// bt_responsavel = (Button) findViewById(R.id.bt_responsavel);
		ic_add_responsavel = (ImageView) findViewById(R.id.ic_add_responsavel);
		ic_add_responsavel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent IrParaResponsavel = new Intent(GerenciarAtividade.this,
						GerenciarResponsavel.class);
				IrParaResponsavel.putExtra("atividade_id", atividade_id);
				IrParaResponsavel.putExtra("projeto_id", projeto_id);
				IrParaResponsavel.putExtra("projeto_nome", projeto_nome);
				IrParaResponsavel.putExtra("projeto_membros", projeto_membros);
				GerenciarAtividade.this.startActivity(IrParaResponsavel);
				GerenciarAtividade.this.finish();

			}
		});

	}

	public void findAtividade() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("atividade");
		query.getInBackground(atividade_id, new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, com.parse.ParseException e) {
				if (e == null) {

					atividade1 = object;
					// if(atividade1.getString("status").equals("Finalizada"))
					// finalizar_atividade.setVisibility(View.GONE);

					TextView contexto = (TextView) findViewById(R.id.tv_contexto);
					contexto.setText(projeto_nome + " > "
							+ atividade1.getString("nome"));
					ic_finalizar = (ImageView) findViewById(R.id.ic_finalizar);
					ic_finalizada = (ImageView) findViewById(R.id.ic_finalizada);

					if (atividade1.getString("status").equals("Finalizada")) {
						ic_finalizar.setVisibility(View.GONE);
						ic_finalizada.setVisibility(View.VISIBLE);
					}

					txt_nome = (EditText) findViewById(R.id.atividade_nome);
					txt_nome.setText(atividade1.getString("nome"));
					txt_prazo = (EditText) findViewById(R.id.atividade_prazo);
					txt_prazo.setText(DateFormat.format("dd/MM/yyyy",
							atividade1.getDate("prazo")));

					txt_descricao = (EditText) findViewById(R.id.atividade_descricao);
					txt_descricao.setText(atividade1.getString("descricao"));

				}
			}
		});
	}

	public class RemoteDataTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {

			ParseQuery<ParseObject> query = ParseQuery.getQuery("comentario");
			query.whereEqualTo("atividade_id", atividade_id);
			query.addDescendingOrder("data");
			query.include("usuario");
			query.include("responsaveis");

			try {
				comentarios = query.find();

			} catch (com.parse.ParseException e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (comentarios != null) {

				comentarioAdapter = new ListaComentarioAdapter(
						GerenciarAtividade.this, comentarios);
				setListAdapter(comentarioAdapter);
			}
		}
	}
}
