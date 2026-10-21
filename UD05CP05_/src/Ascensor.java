import java.util.ArrayList;
import java.util.Queue;
public class Ascensor extends Thread {
    private int pisoActual;
    public Ascensor(int pisoActual){
        this.pisoActual=pisoActual;
    }
    public void addUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }
    @Override
    public void run() {
        try {
            recorrerPisos();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    ArrayList<Usuario>usuarios=new ArrayList<>();
   public void recorrerPisos() throws InterruptedException {
       for (int i = 0; i < usuarios.size(); i++){
           Usuario u=usuarios.get(i);
           int usuarioid=i+1;
           u.start();
           System.out.println("Usuario "+usuarioid+" espera en piso:"+u.getPisoOrigen());

           moverAs(u.getPisoOrigen());

           System.out.println("Usuario "+usuarioid+" sube al ascensor.");

           u.avisarUsuario();

           moverAs(u.getPisoDestino());

           System.out.println("Usuario "+usuarioid+" baja en piso:"+u.getPisoDestino());

           u.avisarUsuario();

           u.join();
       }
       moverAs(-1);
       System.out.println("Ascensor llego a -1; Fin del progama");
   }
   public void moverAs(int destino){
       if (destino>pisoActual) {
           for (int i = pisoActual+1; i <= destino; i++) {
               pisoActual=i;
               System.out.println("Ascensor en piso:" + pisoActual);
           }
       } else if (pisoActual>destino) {
           for (int i=pisoActual-1;i>=destino;i--){
               pisoActual=i;
               System.out.println("Ascensor en piso:"+pisoActual);
           }
       }
   }

}
