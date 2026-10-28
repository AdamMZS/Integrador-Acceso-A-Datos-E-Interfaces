package UD02CP06;

public class HebrasFibonacci extends Thread{
    private int id;
    private int []fib;
    public HebrasFibonacci(int id, int[]fib){
        this.fib=fib;
        this.id=id;
    }
    public void run(){
        synchronized (fib){
    if (id==0){
        fib[0]=0;
    }
    else if(id==1){
        fib[1]=1;
    }
    else {
        while (fib[id - 1] == -1 || fib[id - 2] == -1) {
            try {
                fib.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        fib[id]=fib[id-1]+fib[id-2];
    }
            fib.notifyAll();
        }
    }
}
