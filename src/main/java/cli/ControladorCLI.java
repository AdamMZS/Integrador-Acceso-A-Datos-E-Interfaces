package cli;

import dao.GestorArchivos;
import model.Candidatura;
import java.util.List;

public class ControladorCLI {

    private final GestorArchivos gestor;

    public ControladorCLI() {
        this.gestor = new GestorArchivos();
    }

    public void ejecutar(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: Debes indicar una operación (-listar, -añadir, -ver, -borrar, -importar).");
            return;
        }

        String comando = args[0];

        try {
            switch (comando) {
                case "-listar":
                    List<Candidatura> todas = gestor.leerTodas();
                    if (todas.isEmpty()) {
                        System.out.println("No hay candidaturas registradas.");
                    } else {
                        todas.forEach(System.out::println);
                    }
                    break;

                case "-añadir":
                    System.out.println("Operación añadir en construcción...");
                    break;

                case "-ver":
                    if (args.length < 2) {
                        System.err.println("Error: Debes proporcionar un ID. Ejemplo: -ver 13");
                        return;
                    }
                    long idVer = Long.parseLong(args[1]);
                    System.out.println("Operación ver en construcción para el ID: " + idVer);
                    break;

                case "-borrar":
                    if (args.length < 2) {
                        System.err.println("Error: Debes proporcionar un ID. Ejemplo: -borrar 13");
                        return;
                    }
                    long idBorrar = Long.parseLong(args[1]);
                    System.out.println("Operación borrar en construcción para el ID: " + idBorrar);
                    break;

                case "-importar":
                    System.out.println("Operación importar en construcción...");
                    break;

                default:
                    System.err.println("Error: Comando desconocido '" + comando + "'.");
            }
        } catch (Exception e) {
            System.err.println("Ha ocurrido un error: " + e.getMessage());
        }
    }
}