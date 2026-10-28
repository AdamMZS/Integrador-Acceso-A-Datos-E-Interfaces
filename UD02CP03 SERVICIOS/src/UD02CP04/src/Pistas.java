public class Pistas {
boolean [] pistas;
public Pistas(){
    pistas =new boolean[]{
            false,false
    };
}
    public synchronized int ocuparPista()  {
        try {
            while (pistas[0] && pistas[1] ) {
                wait();
            }
            if (!pistas[0]) {
                pistas[0]=true;
                return 0;
            }if (!pistas[1]){
                pistas[1]=true;
                return 1;
            }

        }catch (InterruptedException e){
            System.out.println("ERROR");
        }finally {
            System.out.println("----------------");
        }
        return -1;
    }
    public synchronized void liberarPista(int pista){
    pistas[pista]=false;
    notify();
    }

}

