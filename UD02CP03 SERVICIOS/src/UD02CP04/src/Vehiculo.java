import java.util.Random;

public class Vehiculo extends Thread{
    String nombre;
    Pistas pistas;
    public Vehiculo(String nombre,Pistas pistas){
        this.nombre=nombre;
        this.pistas=pistas;
    }

    public void run (){
    try{

        int pista = pistas.ocuparPista();
        System.out.println(nombre+" ocupando pista:" + pista);
        Random random = new Random();
        double numero = random.nextDouble(4) + 2;
        Thread.sleep((long) (numero * 1000));
        pistas.liberarPista(pista);
        System.out.printf("%s completó la prueba en %.2f segundos%n", nombre, numero);
        System.out.println(nombre+" Liberando pista:"+pista);
    }catch (InterruptedException e) {
        throw new RuntimeException(e);
    }

    }

}
