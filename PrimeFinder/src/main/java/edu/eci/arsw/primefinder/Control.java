package edu.eci.arsw.primefinder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Control extends Thread {
    
    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 30000000;
    private final static int TMILISECONDS = 5000;

    private final int NDATA = MAXVALUE / NTHREADS;
    private final Object monitor = new Object();
    private PrimeFinderThread pft[];
    
    private Control() {
        super();
        this.pft = new  PrimeFinderThread[NTHREADS];

        int i;
        for(i = 0;i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i*NDATA, (i+1)*NDATA, monitor);
            pft[i] = elem;
        }
        pft[i] = new PrimeFinderThread(i*NDATA, MAXVALUE + 1, monitor);
    }
    
    public static Control newControl() {
        return new Control();
    }

    @Override
    public void run() {
        for(int i = 0;i < NTHREADS;i++ ) {
            pft[i].start();
        }
        
        while(true) {
            try {
                Thread.sleep(TMILISECONDS);
                
                for(int i = 0; i < NTHREADS; i++) {
                    pft[i].pauseThread();
                }
                
                int total = 0;
                for(int i = 0; i < NTHREADS; i++) {
                    total += pft[i].getPrimes().size();
                }
                System.out.println("Primos encontrados: " + total);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Presione ENTER para continuar...");
                reader.readLine();
                
                synchronized(monitor) {
                    for(int i = 0; i < NTHREADS; i++) {
                        pft[i].resumeThread();
                    }
                    monitor.notifyAll();
                }
                
                boolean allDone = true;
                for(int i = 0; i < NTHREADS; i++) {
                    if(pft[i].isAlive()) {
                        allDone = false;
                        break;
                    }
                }
                if(allDone) break;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
