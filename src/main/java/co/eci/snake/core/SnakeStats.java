package co.eci.snake.core;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Almacena estadisticas de una serpiente.
 * Rastrea si esta viva, cuando murio y su longitud.
 */
public final class SnakeStats {
  // Identificador de la serpiente
  private final int id;
  // Referencia a la serpiente
  private final Snake snake;
  // Indica si la serpiente esta viva
  private final AtomicBoolean alive = new AtomicBoolean(true);
  // Momento en que murio (en milisegundos)
  private final AtomicLong deathTime = new AtomicLong(-1);

  /**
   * Constructor.
   * @param id Identificador de la serpiente
   * @param snake Instancia de la serpiente
   */
  public SnakeStats(int id, Snake snake) {
    this.id = id;
    this.snake = snake;
  }

  /**
   * Obtiene el ID de la serpiente.
   * @return Identificador
   */
  public int getId() { return id; }
  
  /**
   * Obtiene la instancia de la serpiente.
   * @return Serpiente
   */
  public Snake getSnake() { return snake; }
  
  /**
   * Verifica si la serpiente esta viva.
   * @return true si esta viva
   */
  public boolean isAlive() { return alive.get(); }
  
  /**
   * Marca la serpiente como muerta.
   * Registra el momento de muerte.
   */
  public void markDead() {
    // Solo marcar la primera vez
    if (alive.compareAndSet(true, false)) {
      deathTime.set(System.currentTimeMillis());
    }
  }

  /**
   * Obtiene el momento de muerte.
   * @return Timestamp en milisegundos, -1 si no ha muerto
   */
  public long getDeathTime() { return deathTime.get(); }
  
  /**
   * Obtiene la longitud actual de la serpiente.
   * @return Numero de segmentos
   */
  public int getLength() { return snake.length(); }
}
