package utils;

import java.util.Currency;
import java.util.Date;

import com.app.colaborativa.R;
import com.app.colaborativa.atividade.Feed;
import com.app.colaborativa.atividade.GerenciarAtividade;
import com.app.colaborativa.atividade.Projeto;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

public class MenuAction {
	
	private Context contexto;
	private Button projeto;
	private Button feed;
	public ParseUser currentUser;

	public MenuAction() {
		currentUser = ParseUser.getCurrentUser();
	}

	 

	    public void MapearProjeto(Context cxt, Button _projeto){
	        this.projeto = _projeto;
	        this.contexto = cxt;
	    
	            this.projeto.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View v) {
	                     Intent i = new Intent(contexto, Projeto.class);
	                     contexto.startActivity(i);
	                     ((Activity) contexto).finish();
	                }
	            }
	         ); 
	    }
	    
	    public void MapearFeed(Context cxt, Button _feed){
	        this.feed = _feed;
	        this.contexto = cxt;
	        
	        if(PullParse.hasAtualizacao()){
	        	this.feed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alerta, 0, 0, 0);
	        }
	    
	            this.feed.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View v) {
	                	PullParse.setUltimaVisita(new Date());
	                	currentUser.put("ultimoAcessoFeed", new Date());	                	
	                	currentUser.saveInBackground();
	                	feed.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
	                     Intent i = new Intent(contexto, Feed.class);
	                     contexto.startActivity(i);
//	                     ((Activity) contexto).finish();
	                }
	            }
	         ); 
	    }
}
