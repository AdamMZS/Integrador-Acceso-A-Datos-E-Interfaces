public class VariableCompartidaSolucionado {
        private int valor;
        private boolean disponible=false;
        public synchronized int getValor() {
            try {
                while (!disponible){
                    wait();
                }
                disponible=false;
                notifyAll();
                return valor;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        public synchronized void setValor(int valor) {
            try{
                while (disponible){
                    wait();
                }
                this.valor=valor;
                disponible=true;
                notifyAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
}
