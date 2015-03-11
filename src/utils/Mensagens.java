package utils;

public class Mensagens {

	public Mensagens() {
		// TODO Auto-generated constructor stub
	}
	
	public String NovoProjeto(String projeto, String atividade, String membro){
		return "Um novo projeto \""+projeto+"\" foi criado pelo membro \""+membro+"\".";
	}
	public String NovaAtividade(String projeto, String atividade, String membro){
		return projeto+": Uma nova atividade \""+atividade+"\" foi criada. Deseja visualizá-la?";
	}
	public String SugestaoResponsavel(String projeto, String atividade, String membro){
		return projeto+": Sugeriram você como responsável pela Atividade \""+atividade+"\". Aceita esse desafio??";
	}
	
	public String InformativoSugestaoResponsavel(String projeto, String atividade, String membro){
		return projeto+": \""+membro+"\" foi convidado a ser um responsável pela Atividade \""+atividade+"\"";
	}
	
	public String AtividadeEditada(String projeto, String atividade, String membro){
		return projeto+": A Atividade \""+atividade+"\" foi alterada! Deseja visualizá-la?";
	}

	public String NovoComentario(String projeto, String atividade, String membro){
		return projeto+": Tem um novo comentário na  Atividade \""+atividade+"\"! Deseja visualizá-lo?";
	}

	public String AtividadeFinalizada(String projeto, String atividade, String membro){
		return projeto+": A Atividade \""+atividade+"\" foi conclúida! Parabéns ao grupo!";
	}
	public String AtividadeReaberta(String projeto, String atividade, String membro){
		return projeto+": A Atividade \""+atividade+"\" foi reaberta! Deseja visualizá-la?";
	}
	public String NovoResponsavel(String projeto, String atividade, String membro){
		return projeto+": "+membro+" agora tambem é responsável pela atividade \""+atividade+"\". Dê os parabéns!";
	}

}
