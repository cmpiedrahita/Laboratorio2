package edu.eci.arsw.primefinder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Controlador que coordina los hilos de busqueda de primos.
 * Pausa los hilos cada cierto tiempo para mostrar progreso.
 */
public class Control extends Thread {
    
    // Numero de hilos trabajadores
    private final static int NTHREADS = 3;
    // Valor maximo hasta donde buscar primos
    private final static int MAXVALUE = 30000000;
    // Tiempo en milisegundos entre pausas
    private final static int TMILISECONDS = 5000;

    // Tamaño del rango que procesa cada hilo
    private final int NDATA = MAXVALUE / NTHREADS;
    // Monitor compartido para sincronizacion
    private final Object monitor = new Object();
    // Array de hilos trabajadores
    private PrimeFinderThread pft[];
    
    /**
     * Constructor privado.
     * Crea y configura los hilos trabajadores.
     */
    private Control() {
        super();
        this.pft = new  PrimeFinderThread[NTHREADS];

        int i;
        // Crear hilos para rangos intermedios
        for(i = 0;i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i*NDATA, (i+1)*NDATA, monitor);
            pft[i] = elem;
        }
        // Crear ultimo hilo con el rango restante
        pft[i] = new PrimeFinderThread(i*NDATA, MAXVALUE + 1, monitor);
    }
    
    /**
     * Metodo factory para crear una instancia de Control.
     * @return Nueva instancia de Control
     */
    public static Control newControl() {
        return new Control();
    }

    /**
     * Metodo principal del controlador.
     * Inicia los hilos y los pausa periodicamente.
     */
    @Override
    public void run() {
        // Iniciar todos los hilos trabajadores
        for(int i = 0;i < NTHREADS;i++ ) {
            pft[i].start();
        }
        
        // Ciclo de pausa y reanudacion
        while(true) {
            try {
                // Esperar el tiempo configurado
                Thread.sleep(TMILISECONDS);
                
                // Pausar todos los hilos
                for(int i = 0; i < NTHREADS; i++) {
                    pft[i].pauseThread();
                }
                
                // Contar total de primos encontrados
                int total = 0;
                for(int i = 0; i < NTHREADS; i++) {
                    total += pft[i].getPrimes().size();
                }
                System.out.println("Primos encontrados: " + total);
                
                // Esperar que el usuario presione ENTER
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Presione ENTER para continuar...");
                reader.readLine();
                
                // Reanudar todos los hilos
                synchronized(monitor) {
                    for(int i = 0; i < NTHREADS; i++) {
                        pft[i].resumeThread();
                    }
                    // Despertar a todos los hilos que estan esperando
                    monitor.notifyAll();
                }
                
                // Verificar si todos los hilos terminaron
                boolean allDone = true;
                for(int i = 0; i < NTHREADS; i++) {
                    if(pft[i].isAlive()) {
                        allDone = false;
                        break;
                    }
                }
                // Si todos terminaron, salir del ciclo
                if(allDone) break;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
