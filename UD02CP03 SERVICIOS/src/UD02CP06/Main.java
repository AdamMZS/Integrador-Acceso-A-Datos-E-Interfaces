package UD02CP06;

import java.util.Scanner;

public class Main {
    static void main() throws InterruptedException {

        Scanner sc=new Scanner(System.in);
        System.out.println("Numero fibonacci a calcular:");
        int n= sc.nextInt();
        int[]fib=new int[n+1];
        HebrasFibonacci []hebras =new HebrasFibonacci[n+1];

        for (int i=0;i<=n;i++){
            fib[i]=-1;
            hebras[i]=new HebrasFibonacci(i,fib);
            hebras[i].start();
        }
        for (int i=2;i<=n;i++){
            hebras[i].join();
            System.out.println("Fibonacci "+i+": "+fib[i]);
            if (i==n){
                System.out.println("****************");
                System.out.println("Fibonacci "+i+": "+fib[i]);
            }
        }

    }
}
