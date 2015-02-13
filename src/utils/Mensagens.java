package utils;

public class Mensagens {

	public Mensagens() {
		// TODO Auto-generated constructor stub
	}
	
	public String NovaAtividade(String projeto, String atividade, String membro){
		return projeto+": Uma nova atividade \""+atividade+"\" foi criada. Deseja visualiz�-la?";
	}
	public String SugestaoResponsavel(String projeto, String atividade, String membro){
		return projeto+": Sugeriram voc� como respons�vel pela Atividade \""+atividade+"\". Aceita esse desafio??";
	}
	
	public String InformativoSugestaoResponsavel(String projeto, String atividade, String membro){
		return projeto+": \""+membro+"\" foi convidado a ser um respons�vel pela Atividade \""+atividade+"\"";
	}
	
	public String AtividadeEditada(String projeto, String atividade, String membro){
		return projeto+": A Atividade \""+atividade+"\" foi alterada! Deseja visualiz�-la?";
	}

	public String NovoComentario(String projeto, String atividade, String membro){
		return projeto+": Tem um novo coment�rio na  Atividade \""+atividade+"\"! Deseja visualiz�-lo?";
	}

	public String AtividadeFinalizada(String projeto, String atividade, String membro){
		return projeto+": A Atividade \""+atividade+"\" foi concl�ida! Parab�ns ao grupo!";
	}
	public String NovoResponsavel(String projeto, String atividade, String membro){
		return projeto+": "+membro+" agora tambem � respons�vel pela atividade \""+atividade+"\". D� os parab�ns!";
	}

}
