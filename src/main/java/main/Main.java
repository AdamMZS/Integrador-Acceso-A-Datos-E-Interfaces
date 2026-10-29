package main;

import cli.ControladorCLI;

/**
 * Punto de entrada de la aplicación.
 */
public class Main {
    /**
     * Crea el punto de entrada de la aplicación.
     */
    public Main() {
    }

    /**
     * Ejecuta la aplicación con los argumentos recibidos.
     *
     * @param args comando y opciones de la CLI
     */
    public static void main(String[] args) {
        ControladorCLI controlador = new ControladorCLI();
        int resultado = controlador.ejecutar(args);
        if (resultado != 0) {
            System.exit(resultado);
        }
    }
}