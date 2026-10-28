public class Main {
    public static void main(String[] args) {

        Pistas pistas = new Pistas();

        Vehiculo v1 = new Vehiculo("V1", pistas);
        Vehiculo v2 = new Vehiculo("V2", pistas);
        Vehiculo v3 = new Vehiculo("V3", pistas);
        Vehiculo v4 = new Vehiculo("V4", pistas);

        v1.start();
        v2.start();
        v3.start();
        v4.start();
    }
}