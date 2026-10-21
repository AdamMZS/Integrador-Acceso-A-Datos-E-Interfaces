public class Main {
    public static void main(String[] args) {
        Ascensor ascensor = new Ascensor(0);

        ascensor.addUsuario(new Usuario(0, 3));
        ascensor.addUsuario(new Usuario(2, -1));
        ascensor.addUsuario(new Usuario(1, 5));

        ascensor.start();
    }
}