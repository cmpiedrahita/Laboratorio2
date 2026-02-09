package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread{

	
	int a,b;
	private final Object monitor;
	private volatile boolean paused = false;
	private List<Integer> primes;
	
	public PrimeFinderThread(int a, int b, Object monitor) {
		super();
                this.primes = new LinkedList<>();
		this.a = a;
		this.b = b;
		this.monitor = monitor;
	}

        @Override
	public void run(){
            for (int i= a;i < b;i++){
                synchronized(monitor) {
                    while(paused) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
                if (isPrime(i)){
                    primes.add(i);
                }
            }
	}
	
	public void pauseThread() {
		paused = true;
	}
	
	public void resumeThread() {
		paused = false;
	}
	
	boolean isPrime(int n) {
	    boolean ans;
            if (n > 2) { 
                ans = n%2 != 0;
                for(int i = 3;ans && i*i <= n; i+=2 ) {
                    ans = n % i != 0;
                }
            } else {
                ans = n == 2;
            }
	    return ans;
	}

	public List<Integer> getPrimes() {
		return primes;
	}
	
}
