package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;

/**
 * Hilo trabajador que busca numeros primos en un rango especifico.
 * Puede ser pausado y reanudado usando wait/notify.
 */
public class PrimeFinderThread extends Thread{

	// Rango de busqueda: desde 'a' hasta 'b'
	int a,b;
	// Monitor compartido para sincronizacion
	private final Object monitor;
	// Indica si el hilo debe pausarse
	private volatile boolean paused = false;
	// Lista de numeros primos encontrados
	private List<Integer> primes;
	
	/**
	 * Constructor del hilo.
	 * @param a Inicio del rango de busqueda
	 * @param b Fin del rango de busqueda
	 * @param monitor Objeto compartido para sincronizacion
	 */
	public PrimeFinderThread(int a, int b, Object monitor) {
		super();
        this.primes = new LinkedList<>();
		this.a = a;
		this.b = b;
		this.monitor = monitor;
	}

	/**
	 * Metodo principal del hilo.
	 * Busca numeros primos en el rango asignado.
	 */
    @Override
	public void run(){
        for (int i= a;i < b;i++){
            // Verificar si debe pausarse
            synchronized(monitor) {
                while(paused) {
                    try {
                        // Esperar hasta que se reanude
                        monitor.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
            // Si es primo, agregarlo a la lista
            if (isPrime(i)){
                primes.add(i);
            }
        }
	}
	
	/**
	 * Marca el hilo para que se pause.
	 */
	public void pauseThread() {
		paused = true;
	}
	
	/**
	 * Marca el hilo para que se reanude.
	 */
	public void resumeThread() {
		paused = false;
	}
	
	/**
	 * Verifica si un numero es primo.
	 * @param n Numero a verificar
	 * @return true si es primo, false en caso contrario
	 */
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

	/**
	 * Obtiene la lista de primos encontrados.
	 * @return Lista de numeros primos
	 */
	public List<Integer> getPrimes() {
		return primes;
	}
	
}
