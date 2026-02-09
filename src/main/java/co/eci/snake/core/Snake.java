package co.eci.snake.core;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Representa una serpiente en el juego.
 * Maneja su cuerpo, direccion y movimiento de forma thread-safe.
 */
public final class Snake {
  // Cuerpo de la serpiente (cola de posiciones)
  private final Deque<Position> body = new ArrayDeque<>();
  // Direccion actual de movimiento
  private volatile Direction direction;
  // Longitud maxima permitida
  private int maxLength = 5;
  // Lock para sincronizar acceso al cuerpo
  private final Object lock = new Object();

  /**
   * Constructor privado.
   * @param start Posicion inicial
   * @param dir Direccion inicial
   */
  private Snake(Position start, Direction dir) {
    body.addFirst(start);
    this.direction = dir;
  }

  /**
   * Metodo factory para crear una serpiente.
   * @param x Coordenada X inicial
   * @param y Coordenada Y inicial
   * @param dir Direccion inicial
   * @return Nueva instancia de Snake
   */
  public static Snake of(int x, int y, Direction dir) {
    return new Snake(new Position(x, y), dir);
  }

  /**
   * Obtiene la direccion actual.
   * @return Direccion de movimiento
   */
  public Direction direction() { return direction; }

  /**
   * Cambia la direccion de la serpiente.
   * No permite giros de 180 grados.
   * @param dir Nueva direccion
   */
  public void turn(Direction dir) {
    // Evitar que la serpiente se devuelva sobre si misma
    if ((direction == Direction.UP && dir == Direction.DOWN) ||
        (direction == Direction.DOWN && dir == Direction.UP) ||
        (direction == Direction.LEFT && dir == Direction.RIGHT) ||
        (direction == Direction.RIGHT && dir == Direction.LEFT)) {
      return;
    }
    this.direction = dir;
  }

  /**
   * Obtiene la posicion de la cabeza.
   * @return Posicion de la cabeza
   */
  public Position head() {
    synchronized(lock) {
      return body.peekFirst();
    }
  }

  /**
   * Crea una copia del cuerpo de la serpiente.
   * Usado para renderizado sin bloquear el movimiento.
   * @return Copia del cuerpo
   */
  public Deque<Position> snapshot() {
    synchronized(lock) {
      return new ArrayDeque<>(body);
    }
  }

  /**
   * Avanza la serpiente una posicion.
   * @param newHead Nueva posicion de la cabeza
   * @param grow Si debe crecer (cuando come)
   */
  public void advance(Position newHead, boolean grow) {
    synchronized(lock) {
      // Agregar nueva cabeza
      body.addFirst(newHead);
      // Si come, aumentar longitud maxima
      if (grow) maxLength++;
      // Eliminar cola si excede la longitud
      while (body.size() > maxLength) body.removeLast();
    }
  }

  /**
   * Obtiene la longitud actual de la serpiente.
   * @return Numero de segmentos
   */
  public int length() {
    synchronized(lock) {
      return body.size();
    }
  }
}
