package exception;

public class ConexaoBancoException extends Exception {
    public ConexaoBancoException(String mensagem) {
        super(mensagem);
    }
    public ConexaoBancoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
