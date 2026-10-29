package main;

import cli.ControladorCLI;

public class Main {
    public static void main(String[] args) {
        ControladorCLI controlador = new ControladorCLI();
        int resultado = controlador.ejecutar(args);
        if (resultado != 0) {
            System.exit(resultado);
        }
    }
}