package utils;

public class DefaultMessages {

	private static final String MSG_ERRO_SALVAR = "Ocorreu um erro ao tentar salvar o registro no banco de dados";
	private static final String MSG_ERRO_ATUALIZAR = "Ocorreu um erro ao tentar atualizar o registro no banco de dados";
	private static final String MSG_ERRO_DELETAR = "Ocorreu um erro ao tentar deletar o registro no banco de dados";
	private static final String MSG_ERRO_FINDBY = "Ocorreu um erro ao tentar procurar o registro no banco de dados";
	private static final String MSG_ERRO_FINDALL = "Ocorreu um erro ao tentar listar todos os registros cadastrados no banco de dados";

	public static String getMsgErroSalvar() {
		return MSG_ERRO_SALVAR;
	}

	public static String getMsgErroAtualizar() {
		return MSG_ERRO_ATUALIZAR;
	}

	public static String getMsgErroDeletar() {
		return MSG_ERRO_DELETAR;
	}

	public static String getMsgErroFindby() {
		return MSG_ERRO_FINDBY;
	}

	public static String getMsgErroFindall() {
		return MSG_ERRO_FINDALL;
	}

}
