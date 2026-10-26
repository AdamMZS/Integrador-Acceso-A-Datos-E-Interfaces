package main;

import cli.ControladorCLI;

public class Main {
    public static void main(String[] args) {
        ControladorCLI controlador = new ControladorCLI();
        controlador.ejecutar(args);
    }
}