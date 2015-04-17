package utils;

import java.util.Date;
import java.util.List;

import android.R.bool;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class PullParse {
	
	public static List<ParseObject> projetos;
	public static Date ultimaAtualizacaoFeed;
	public static Date ultimaVisitaFeed;
	public static Date ultimaVisualizacaoFeed;
	public static ParseObject ultimo_feed;
	public static boolean isNotificacao = false;
	
	public PullParse() {
	}
	public static void setListProjeto(List<ParseObject> listProjetos) {

		projetos = listProjetos;
		
	}
	public static List<ParseObject> getListProjeto() {

		return projetos;
		
	}
	

	public static void setUltimaAtualizacaoFeed(Date _ultimaAtualizacaoFeed) {

		ultimaAtualizacaoFeed = _ultimaAtualizacaoFeed;
		ultimo_feed.put("data", _ultimaAtualizacaoFeed);
		ultimo_feed.saveInBackground();
		
		
	}
	
	public static void setUltimaVisita(Date _ultimaVisitaFeed) {
		if(ultimaVisitaFeed == null)
			ultimaVisualizacaoFeed = _ultimaVisitaFeed;
		else
			ultimaVisualizacaoFeed = ultimaVisitaFeed;
		ultimaVisitaFeed = _ultimaVisitaFeed;
		
	}
	
	public static Date getUltimaVisita() {
		return ultimaVisualizacaoFeed;
		
	}
	
	
	public static Boolean hasAtualizacao() {
		
		try{
			ultimo_feed = ParseQuery.getQuery("ultimo_feed").getFirst();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		
		if(ultimaAtualizacaoFeed != null && ultimaVisitaFeed != null){
			if(ultimaVisitaFeed.before(ultimo_feed.getDate("data"))){
				return true;
			}
		}
		return false;
		
	}
	
	public static void setUltimoFeed(ParseObject ultimoFeed){
		ultimo_feed = ultimoFeed;
	}
	
	public static void saveFeed(ParseObject atividade, ParseObject projeto, String modelo, String hasIcone, ParseUser membro){
		
		ParseObject feed = new ParseObject("feed");
		if(atividade!=null)
			feed.put("atividade", atividade);
		feed.put("projeto", projeto);
		feed.put("modelo", modelo);
		feed.put("icone", hasIcone);
		if(membro!=null)
			feed.put("membro", membro);
		feed.put("contador", 0);
		feed.put("data", new Date());
		setUltimaAtualizacaoFeed(new Date());
		feed.saveInBackground();
		
		if(!modelo.equals("NovoProjeto")){
			

			ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
			pushQuery.whereEqualTo("channels", "Proj_"+projeto.getObjectId().toString());
			pushQuery.whereNotEqualTo("objectId", ParseInstallation.getCurrentInstallation().getObjectId());
			ParsePush push = new ParsePush();
			push.setQuery(pushQuery);
		
			
			//ParsePush push = new ParsePush();
			//push.setChannel("Proj_"+projeto.getObjectId().toString());
			push.setMessage("Notificação: Projeto "+projeto.getString("nome"));
			push.sendInBackground();
		}
	}
	
	public static void setIsNotificacao(Boolean is){
		isNotificacao = is;
	}
		
	public static boolean isNotificacao(){
		return isNotificacao;
	}
	
	
}
