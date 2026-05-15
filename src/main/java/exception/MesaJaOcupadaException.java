package exception;

public class MesaJaOcupadaException extends Exception {
    public MesaJaOcupadaException(int mesa) {
        super("A Mesa " + mesa + " já possui uma comanda ativa.");
    }
}
