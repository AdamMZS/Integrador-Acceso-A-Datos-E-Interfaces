public class Main {
    void main() {
    VariableCompartida variable=new VariableCompartida();
    Thread hebra=new Thread(() ->{
    for (int i=0;i<100;i++){
        variable.setValor(i);
    }
        });
    Thread leer=new Thread(()->{
       for (int i=0;i<100;i++){
           System.out.println(variable.getValor());
       }
    });
    hebra.start();
    leer.start();
        //Se puede ver que el resultado me da todo 99
        // porque no van a la vez, te coge el resultado cuando ya el primer hilo termina
        //Un posible resultado seria en los metodos de variableCompartida seria poner synchronized en los metodos get y set usando notifu

        VariableCompartidaSolucionado var=new VariableCompartidaSolucionado();
    Thread hebraSol=new Thread(()->{
       for (int i=0;i<100;i++){
           var.setValor(i);
    }
    });
    Thread LeerSol=new Thread(()->{
    for (int i=0;i<100;i++){
        System.out.println(var.getValor());
    }
    });
    try{//se introducen los join para que se espere a que termine y salgan las soluciones separadas y ordenadas
     hebra.join();
     leer.join();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
        System.out.println("-----------------------------------");
    hebraSol.start();
    LeerSol.start();

    }
}