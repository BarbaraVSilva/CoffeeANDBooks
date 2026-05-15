package exception;

public class UsuarioNaoAutorizadoException extends RuntimeException {
    public UsuarioNaoAutorizadoException() {
        super("Acesso negado: Seu perfil não possui permissão para esta operação.");
    }
}
