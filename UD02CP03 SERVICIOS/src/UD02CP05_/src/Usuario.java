public class Usuario extends Thread{
    private int pisoOrigen;
    private int pisoDestino;
    private boolean disponible = false;
    public Usuario(int pisoOrigen,int pisoDestino){
        this.pisoDestino=pisoDestino;
        this.pisoOrigen=pisoOrigen;
    }
    @Override
    public void run() {
        esperarPiso();
        esperarPiso();
    }

    public int getPisoOrigen() {
        return pisoOrigen;
    }

    public void setPisoOrigen(int pisoOrigen) {
        this.pisoOrigen = pisoOrigen;
    }

    public int getPisoDestino() {
        return pisoDestino;
    }

    public void setPisoDestino(int pisoDestino) {
        this.pisoDestino = pisoDestino;
    }
    public synchronized void esperarPiso(){
        try{
            while(!disponible){
                wait();
            }
            disponible=false;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized void avisarUsuario(){
            disponible=true;
            notifyAll();
    }

}
