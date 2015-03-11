package com.app.colaborativa.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utils.Mensagens;
import android.R.drawable;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.colaborativa.atividade.Atividade;
import com.app.colaborativa.atividade.NovaAtividade;
import com.parse.ParseFacebookUtils.Permissions.User;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.app.colaborativa.R;

public class ListaFeedAdapter extends ArrayAdapter<ParseObject> {

	private Context context;
	private List<ParseObject> feeds = null;
	String modelo, mensagem;
	Method metodo = null;
	String userId = ParseUser.getCurrentUser().getObjectId();
	//Mensagens mensagem = new Mensagens();

	public ListaFeedAdapter(Context context, List<ParseObject> feeds) {
		super(context, 0, feeds);
		this.feeds = feeds;
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ParseObject feed = feeds.get(position);

		if (view == null)
			view = LayoutInflater.from(context).inflate(R.layout.lista_feed,
					null);

		ParseObject atividade = feed.getParseObject("atividade");
		ParseUser membro = (ParseUser) feed.getParseObject("membro");
		modelo = feed.getString("modelo");
		mensagem = null;
		
		

		Object arglist[] = new Object[3];
		arglist[0] = feed.getParseObject("projeto").getString("nome");
		
		if(atividade != null)
			arglist[1] = atividade.getString("nome");
		
		if(membro != null)
			arglist[2] = membro.getString("nome");

		Class classe = Mensagens.class;
		Object object = null;
		try {
			object = classe.newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		try {
			metodo = classe.getMethod(modelo, String.class, String.class,
					String.class);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mensagem = (String) metodo.invoke(object, arglist);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TextView textViewMensagem = (TextView) view.findViewById(R.id.tv_msgm);
		textViewMensagem.setText(mensagem);
		
		final TextView textViewContador = (TextView) view.findViewById(R.id.tv_contador);
		textViewContador.setText(feed.getNumber("contador").toString());
		
		TextView textViewTexto = (TextView) view.findViewById(R.id.tv_texto);
		
		
		
		final ImageButton imageIcone = (ImageButton) view.findViewById(R.id.ic_feed);
		if(feed.getString("icone").equals("like")){
			imageIcone.setBackgroundResource(R.drawable.ic_like);
			textViewTexto.setText("Curtidas:");
		}
		else if(feed.getString("icone").equals("none")){
			imageIcone.setVisibility(View.GONE);
			textViewTexto.setVisibility(View.GONE);
			textViewContador.setVisibility(View.GONE);
		}
//		else{
//			imageIcone.setBackgroundResource(R.drawable.ic_applouse);
//			textViewTexto.setText("Agradecimentos:");
//		}
		
		List usuarios = feed.getList("curtidas");
		if(usuarios != null){
			if(usuarios.contains(userId)){
				imageIcone.setEnabled(false);
				imageIcone.setBackgroundResource(R.drawable.ic_like_azul);
			}
		}

		imageIcone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				feed.increment("contador");
				feed.addUnique("curtidas",userId);
				feed.saveInBackground();
				textViewContador.setText(feed.getNumber("contador").toString());
				imageIcone.setBackgroundResource(R.drawable.ic_like_azul);
				imageIcone.setEnabled(false);
			}
	
		});
		return view;
	}
}