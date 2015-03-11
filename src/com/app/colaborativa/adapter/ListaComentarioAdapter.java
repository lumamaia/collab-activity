package com.app.colaborativa.adapter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.StaticLayout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.colaborativa.R;
import com.app.colaborativa.atividade.GerenciarAtividade;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ListaComentarioAdapter extends ArrayAdapter<ParseObject> {

	private Context context;
	private List<ParseObject> comentarios = null;
	public ParseObject membro;
	String userId = ParseUser.getCurrentUser().getObjectId();

	public ListaComentarioAdapter(Context context, List<ParseObject> comentarios) {
		super(context, 0, comentarios);
		this.comentarios = comentarios;
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ParseObject comentario = comentarios.get(position);

		if (view == null)
			view = LayoutInflater.from(context).inflate(
					R.layout.lista_comentario, null);

		TextView textViewNome = (TextView) view
				.findViewById(R.id.tv_comentario_membro);
		textViewNome.setText(comentario.getParseObject("usuario").getString(
				"nome"));

		TextView textViewComentario = (TextView) view
				.findViewById(R.id.tv_comentario);
		textViewComentario.setText(comentario.getString("comentario"));

		TextView textViewPrazo = (TextView) view
				.findViewById(R.id.tv_comentario_data);
		textViewPrazo.setText(DateFormat.format("dd/MM/yyyy hh:mm:ss",
				comentario.getDate("data").getTime()));
		
		final TextView textViewContador = (TextView) view.findViewById(R.id.tv_contador);
		textViewContador.setText(comentario.getNumber("contador").toString());
		
		ImageView anexo = (ImageView) view.findViewById(R.id.anexo);
		
		anexo.setImageDrawable(null);
		anexo.setVisibility(View.GONE);
		   
		ParseFile file = comentario.getParseFile("anexo");
		if(file!=null){
			anexo.setVisibility(View.VISIBLE);
			InputStream is = null;
			try {
				is = new ByteArrayInputStream(file.getData());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inSampleSize=2; //decrease decoded image ;
			
			
			Bitmap bmp = BitmapFactory.decodeStream(is, null, options);
			anexo.setImageBitmap(bmp);
		}

		final ImageButton imageIcone = (ImageButton) view
				.findViewById(R.id.ic_like);
		imageIcone.setBackgroundResource(R.drawable.ic_like);
		
		List usuarios = comentario.getList("curtidas");
		if(usuarios != null){
			if(usuarios.contains(userId)){
				imageIcone.setEnabled(false);
				imageIcone.setBackgroundResource(R.drawable.ic_like_azul);
			}
		}
		
		imageIcone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				comentario.increment("contador");
				comentario.addUnique("curtidas", userId);
				comentario.saveInBackground();
				textViewContador.setText(comentario.getNumber("contador").toString());
				imageIcone.setBackgroundResource(R.drawable.ic_like_azul);
				imageIcone.setEnabled(false);
			}

		});
		
//		anexo.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
//				builder.setTitle("Comentario");
//				builder.setIcon(R.drawable.ic_comentario);
//				
//
//				AlertDialog alertDialog = builder.create();
//				LayoutInflater inflater = alertDialog.getLayoutInflater();
//				View dialoglayout = inflater.inflate(R.layout.visualizar_anexo, null);
//				builder.setView(dialoglayout);
//				
//				ImageView imagem = (ImageView) dialoglayout.findViewById(R.id.anexo);
//				imagem.setBackgroundDrawable(anexo.getBackground());
//				
//				builder.setNegativeButton("Voltar",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int which) {
//								dialog.cancel();
//							}
//						});
//				
//				builder.show();
//			}
//
//		});
		
		
		
		return view;
	}
}