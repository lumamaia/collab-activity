package utils;

import java.util.List;

import com.parse.ParseObject;

public class PullParse {
	
	public static List<ParseObject> projetos;
	
	public static void setListProjeto(List<ParseObject> listProjetos) {

		projetos = listProjetos;
		
	}
	public static List<ParseObject> getListProjeto() {

		return projetos;
		
	}
		
}
