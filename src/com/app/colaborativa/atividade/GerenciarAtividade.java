package com.app.colaborativa.atividade;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import utils.DeleteAll;
import utils.Mask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Files;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.colaborativa.R;
import com.app.colaborativa.R.drawable;
import com.app.colaborativa.adapter.ListaComentarioAdapter;
import com.parse.GetCallback;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class GerenciarAtividade extends Activity {

	private EditText nome, prazo, descricao;
	private Date data_prazo;
	private Button salvar, bt_projeto, bt_feed, bt_contexto;
	private ImageView ic_add_comentario, ic_add_responsavel, ic_finalizar,
			ic_finalizada, ic_editar, ic_salvar, ic_excluir, ic_cancelar;
	public ImageView visualizar_img, anexo, remove_anexo;
	private LinearLayout view_editar, view_gerenciar;
	public ParseObject atividade1, comentario;
	public List<ParseObject> comentarios = null;
	List<ParseObject> responsaveis;
	public ListaComentarioAdapter comentarioAdapter;
	public String atividade_id, projeto_id, projeto_nome, projeto_membros;
	static public Long projeto_prazo;
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
			projeto_prazo = extras.getLong("projeto_prazo");
			this.findAtividade();
		}

		new RemoteDataTask().execute();

		//

		prazo = (EditText) findViewById(R.id.atividade_prazo);
		prazo.addTextChangedListener(Mask.insert("##/##/####", prazo));

		// add_comentario = (Button) findViewById(R.id.bt_add_comentario);
		ic_add_comentario = (ImageView) findViewById(R.id.ic_add_comentario);
		ic_add_comentario.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				comentario = new ParseObject("comentario");

				final AlertDialog.Builder builder = new AlertDialog.Builder(
						GerenciarAtividade.this, AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle("Comentario");
				builder.setIcon(R.drawable.ic_comentario);

				final AlertDialog alertDialog = builder.create();
				LayoutInflater inflater = alertDialog.getLayoutInflater();
				final View dialoglayout = inflater.inflate(
						R.layout.add_comentario, null);
				builder.setView(dialoglayout);

				final EditText input = (EditText) dialoglayout
						.findViewById(R.id.input_comentario);
				anexo = (ImageButton) dialoglayout.findViewById(R.id.ic_anexo);
				remove_anexo = (ImageButton) dialoglayout
						.findViewById(R.id.ic_remove_anexo);
				visualizar_img = (ImageView) dialoglayout
						.findViewById(R.id.imageview);
				anexo.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);//
						startActivityForResult(Intent.createChooser(intent,
								"Selecione a imagem"), 1234);

					}
				});

				remove_anexo.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						comentario.remove("anexo");
						anexo.setVisibility(View.VISIBLE);
						remove_anexo.setVisibility(View.GONE);
						visualizar_img.setVisibility(View.GONE);

					}
				});

				builder.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

				builder.setPositiveButton("Enviar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								comentario.put("comentario", input.getText()
										.toString());

								comentario.put("atividade_id", atividade_id);
								comentario.put("atividade", atividade1);
								comentario.put("data", new Date());
								comentario.put("contador", 0);
								comentario.put("usuario",
										ParseUser.getCurrentUser());
								comentario.saveInBackground();

								ParseObject feed = new ParseObject("feed");
								feed.put("atividade", atividade1);
								feed.put("projeto",
										atividade1.getParseObject("projeto"));
								feed.put("modelo", "NovoComentario");
								feed.put("icone", "none");
								feed.put("contador", 0);
								feed.put("data", new Date());
								feed.saveInBackground();

								new RemoteDataTask().execute();

								// viewFlipper.showPrevious();
							}
						});

				builder.show();

			}
		});

		// finalizar_atividade = (Button) findViewById(R.id.bt_finalizar);
		ic_finalizar = (ImageView) findViewById(R.id.ic_finalizar);
		ic_finalizar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						GerenciarAtividade.this, AlertDialog.THEME_HOLO_LIGHT);

				alertDialog.setTitle("Resolução");

				// Setting Dialog Message
				final EditText input = new EditText(GerenciarAtividade.this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				input.setLayoutParams(lp);
				alertDialog.setView(input);
				alertDialog.setIcon(R.drawable.ic_finalizada);

				alertDialog.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

				alertDialog.setPositiveButton("Finalizar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								ParseObject atividade = new ParseObject(
										"atividade");
								atividade = atividade1;
								atividade.put("status", "Finalizada");
								atividade.saveInBackground();

								ParseObject comentario = new ParseObject(
										"comentario");
								comentario.put("comentario", "Resolução: "
										+ input.getText().toString());
								comentario.put("atividade_id", atividade_id);
								comentario.put("atividade", atividade1);
								comentario.put("data", new Date());
								comentario.put("contador", 0);
								comentario.put("usuario",
										ParseUser.getCurrentUser());
								comentario.saveInBackground();

								ParseObject feed3 = new ParseObject("feed");
								feed3.put("atividade", atividade1);
								feed3.put("projeto",
										atividade1.getParseObject("projeto"));
								feed3.put("modelo", "AtividadeFinalizada");
								feed3.put("icone", "like");
								feed3.put("contador", 0);
								feed3.put("data", new Date());
								feed3.saveInBackground();

								Intent VoltarParaAtividade = new Intent(
										GerenciarAtividade.this,
										Atividade.class);
								VoltarParaAtividade.putExtra("projeto_id",
										projeto_id);
								VoltarParaAtividade.putExtra("projeto_nome",
										projeto_nome);
								VoltarParaAtividade.putExtra("projeto_prazo",
										projeto_prazo);
								VoltarParaAtividade.putExtra("atividade_id",
										atividade_id);
								VoltarParaAtividade.putExtra("projeto_membros",
										projeto_membros);
								GerenciarAtividade.this
										.startActivity(VoltarParaAtividade);
								GerenciarAtividade.this.finish();

							}

						});
				alertDialog.show();
			}
		});

		ic_finalizada = (ImageView) findViewById(R.id.ic_finalizada);
		ic_finalizada.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						GerenciarAtividade.this, AlertDialog.THEME_HOLO_LIGHT);

				alertDialog.setTitle("Motivo");

				// Setting Dialog Message
				final EditText input = new EditText(GerenciarAtividade.this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				input.setLayoutParams(lp);
				alertDialog.setView(input);
				alertDialog.setIcon(R.drawable.ic_finalizar);

				alertDialog.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

				alertDialog.setPositiveButton("Reabrir",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								ParseObject atividade = new ParseObject(
										"atividade");
								atividade = atividade1;
								atividade.put("status", "Em Aberto");
								atividade.saveInBackground();

								ic_finalizada.setVisibility(View.GONE);
								ic_finalizar.setVisibility(View.VISIBLE);

								ParseObject comentario = new ParseObject(
										"comentario");
								comentario.put("comentario", "Reaberta: "
										+ input.getText().toString());
								comentario.put("atividade_id", atividade_id);
								comentario.put("atividade", atividade1);
								comentario.put("data", new Date());
								comentario.put("contador", 0);
								comentario.put("usuario",
										ParseUser.getCurrentUser());
								comentario.saveInBackground();

								ParseObject feed3 = new ParseObject("feed");
								feed3.put("atividade", atividade1);
								feed3.put("projeto",
										atividade1.getParseObject("projeto"));
								feed3.put("modelo", "AtividadeReaberta");
								feed3.put("icone", "like");
								feed3.put("contador", 0);
								feed3.put("data", new Date());
								feed3.saveInBackground();

								new RemoteDataTask().execute();
								dialog.cancel();

							}

						});
				alertDialog.show();
				comentarioAdapter.notifyDataSetChanged();
			}
		});

		ic_salvar = (ImageView) findViewById(R.id.ic_salvar);
		ic_salvar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				nome = (EditText) findViewById(R.id.atividade_nome);
				prazo = (EditText) findViewById(R.id.atividade_prazo);
				descricao = (EditText) findViewById(R.id.atividade_descricao);

				if (nome.getText().toString().trim().equals(""))
					nome.setError("Nome é obrigatorio!");
				else if (prazo.getText().toString().trim().equals(""))
					prazo.setError("Prazo é obrigatorio!");
				else {

					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					try {
						data_prazo = sdf.parse(prazo.getText().toString());
						Calendar cal = Calendar.getInstance();
						cal.setTime(data_prazo);
						if (cal.get(Calendar.YEAR) < 1000) {
							cal.add(Calendar.YEAR, 2000);
						}
						data_prazo = cal.getTime();

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (new Date().after(data_prazo)) {
						prazo.setError("Esse prazo já passou!");
					} else if (new Date(projeto_prazo).before(data_prazo)) {
						prazo.setError("Esse prazo é maior que o prazo do Projeto!");
					} else {
						ParseObject atividade = new ParseObject("atividade");
						atividade = atividade1;
						atividade.put("nome", nome.getText().toString());
						atividade.put("prazo", data_prazo);
						// atividade.put("projeto_id", projeto_id);
						atividade.put("descricao", descricao.getText()
								.toString());
						atividade.saveInBackground();
						// atividade.saveInBackground(new SaveCallback() {
						//
						// @Override
						// public void done(com.parse.ParseException e) {
						//
						// Intent VoltarParaAtividade = new Intent(
						// GerenciarAtividade.this, Atividade.class);
						// VoltarParaAtividade.putExtra("projeto_id",
						// projeto_id);
						// VoltarParaAtividade.putExtra("projeto_nome",
						// projeto_nome);
						// VoltarParaAtividade.putExtra("projeto_prazo",
						// projeto_prazo);
						// VoltarParaAtividade.putExtra("atividade_id",
						// atividade_id);
						// VoltarParaAtividade.putExtra("projeto_membros",
						// projeto_membros);
						// GerenciarAtividade.this
						// .startActivity(VoltarParaAtividade);
						// GerenciarAtividade.this.finish();
						//
						// }
						// });

						view_gerenciar.setVisibility(View.VISIBLE);
						view_editar.setVisibility(View.GONE);
						nome.setEnabled(false);
						prazo.setEnabled(false);
						descricao.setEnabled(false);

					}
				}
			}
		});

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
				IrParaResponsavel.putExtra("projeto_prazo", projeto_prazo);
				IrParaResponsavel.putExtra("projeto_membros", projeto_membros);
				startActivity(IrParaResponsavel);
//				GerenciarAtividade.this.finish();

			}
		});

		view_editar = (LinearLayout) findViewById(R.id.view_editar);
		view_gerenciar = (LinearLayout) findViewById(R.id.view_gerenciar);

		ic_excluir = (ImageView) findViewById(R.id.ic_excluir);
		ic_excluir.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DeleteAll delete = new DeleteAll();
				delete.execute(atividade1);

				Intent VoltarParaAtividade = new Intent(
						GerenciarAtividade.this, Atividade.class);
				VoltarParaAtividade.putExtra("projeto_id", projeto_id);
				VoltarParaAtividade.putExtra("projeto_nome", projeto_nome);
				VoltarParaAtividade.putExtra("atividade_id", atividade_id);
				VoltarParaAtividade
						.putExtra("projeto_membros", projeto_membros);
				GerenciarAtividade.this.startActivity(VoltarParaAtividade);
				GerenciarAtividade.this.finish();

			}
		});

		ic_editar = (ImageView) findViewById(R.id.ic_editar);
		ic_editar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				view_gerenciar.setVisibility(View.GONE);
				view_editar.setVisibility(View.VISIBLE);
				nome.setEnabled(true);
				prazo.setEnabled(true);
				descricao.setEnabled(true);

			}
		});

		ic_cancelar = (ImageView) findViewById(R.id.ic_cancelar);
		ic_cancelar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				view_gerenciar.setVisibility(View.VISIBLE);
				view_editar.setVisibility(View.GONE);
				nome.setEnabled(false);
				prazo.setEnabled(false);
				descricao.setEnabled(false);

			}
		});

		bt_contexto = (Button) findViewById(R.id.button_contexto);
		bt_contexto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
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

					bt_contexto = (Button) findViewById(R.id.button_contexto);
					bt_contexto.setText("Projeto " + projeto_nome
							+ " > Atividades");

					TextView contexto = (TextView) findViewById(R.id.tv_contexto);
					contexto.setText(atividade1.getString("nome"));

					ic_finalizar = (ImageView) findViewById(R.id.ic_finalizar);
					ic_finalizada = (ImageView) findViewById(R.id.ic_finalizada);

					if (atividade1.getString("status").equals("Finalizada")) {
						ic_finalizar.setVisibility(View.GONE);
						ic_finalizada.setVisibility(View.VISIBLE);
					}

					nome = (EditText) findViewById(R.id.atividade_nome);
					nome.setText(atividade1.getString("nome"));
					nome.setEnabled(false);
					prazo = (EditText) findViewById(R.id.atividade_prazo);
					prazo.setText(DateFormat.format("dd/MM/yyyy",
							atividade1.getDate("prazo")));
					prazo.setEnabled(false);

					descricao = (EditText) findViewById(R.id.atividade_descricao);
					descricao.setText(atividade1.getString("descricao"));
					descricao.setEnabled(false);

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
				// setListAdapter(comentarioAdapter);
				ListView myList = (ListView) findViewById(R.id.listview_comentarios);
				myList.setAdapter(comentarioAdapter);
			}
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 1234:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();

				Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
				visualizar_img.setImageBitmap(yourSelectedImage);
				visualizar_img.setVisibility(View.VISIBLE);
				anexo.setVisibility(View.GONE);
				remove_anexo.setVisibility(View.VISIBLE);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 30,
						stream);
				byte[] byteArray = stream.toByteArray();

				ParseFile file = new ParseFile("teste.jpg", byteArray);
				comentario.put("anexo", file);

				/*
				 * Now you have choosen image in Bitmap format in object
				 * "yourSelectedImage". You can use it in way you want!
				 */
			}
		}

	};
}
