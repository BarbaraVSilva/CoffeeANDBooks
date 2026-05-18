package util;

import java.util.HashMap;
import java.util.Map;
import model.Comanda;

public class ComandaManager {
    private static Map<Integer, Comanda> comandasAtivas = new HashMap<>();

    public static Map<Integer, Comanda> getComandasAtivas() {
        return comandasAtivas;
    }

    public static void abrirComanda(int numeroMesa, String clienteNome) {
        if (!comandasAtivas.containsKey(numeroMesa)) {
            comandasAtivas.put(numeroMesa, new Comanda(numeroMesa, clienteNome));
        }
    }

    public static Comanda getComanda(int numeroMesa) {
        return comandasAtivas.get(numeroMesa);
    }

    public static void fecharComanda(int numeroMesa) {
        comandasAtivas.remove(numeroMesa);
    }
    
    public static boolean isMesaOcupada(int numeroMesa) {
        return comandasAtivas.containsKey(numeroMesa);
    }
}
