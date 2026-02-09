package co.eci.snake.concurrency;

import co.eci.snake.core.Board;
import co.eci.snake.core.Direction;
import co.eci.snake.core.GameState;
import co.eci.snake.core.Snake;
import co.eci.snake.core.SnakeStats;
import co.eci.snake.core.engine.GameClock;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Hilo que controla el movimiento autonomo de una serpiente.
 * Respeta el estado del juego (pausado/corriendo).
 */
public final class SnakeRunner implements Runnable {
  private final Snake snake;
  private final Board board;
  private final GameClock clock;
  private final SnakeStats stats;
  // Tiempo de espera normal entre movimientos
  private final int baseSleepMs = 80;
  // Tiempo de espera en modo turbo
  private final int turboSleepMs = 40;
  // Ticks restantes de turbo
  private int turboTicks = 0;

  /**
   * Constructor.
   * @param snake Serpiente a controlar
   * @param board Tablero de juego
   * @param clock Reloj del juego
   * @param stats Estadisticas de la serpiente
   */
  public SnakeRunner(Snake snake, Board board, GameClock clock, SnakeStats stats) {
    this.snake = snake;
    this.board = board;
    this.clock = clock;
    this.stats = stats;
  }

  /**
   * Metodo principal del hilo.
   * Mueve la serpiente continuamente hasta que muera.
   */
  @Override
  public void run() {
    try {
      int consecutiveHits = 0;
      while (!Thread.currentThread().isInterrupted()) {
        // Esperar mientras el juego no este corriendo
        while (clock.getState() != GameState.RUNNING) {
          Thread.sleep(50);
        }
        
        // Ocasionalmente cambiar de direccion
        maybeTurn();
        
        // Intentar mover la serpiente
        var res = board.step(snake);
        
        if (res == Board.MoveResult.HIT_OBSTACLE) {
          // Contar choques consecutivos
          consecutiveHits++;
          // Si choca muchas veces, la serpiente muere
          if (consecutiveHits > 10) {
            stats.markDead();
            break;
          }
          // Cambiar de direccion al chocar
          randomTurn();
        } else {
          // Reiniciar contador si no choco
          consecutiveHits = 0;
          // Activar turbo si lo comio
          if (res == Board.MoveResult.ATE_TURBO) {
            turboTicks = 100;
          }
        }
        
        // Determinar velocidad segun modo turbo
        int sleep = (turboTicks > 0) ? turboSleepMs : baseSleepMs;
        if (turboTicks > 0) turboTicks--;
        Thread.sleep(sleep);
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Decide aleatoriamente si cambiar de direccion.
   */
  private void maybeTurn() {
    // Menor probabilidad de girar en turbo
    double p = (turboTicks > 0) ? 0.05 : 0.10;
    if (ThreadLocalRandom.current().nextDouble() < p) randomTurn();
  }

  /**
   * Cambia la serpiente a una direccion aleatoria.
   */
  private void randomTurn() {
    var dirs = Direction.values();
    snake.turn(dirs[ThreadLocalRandom.current().nextInt(dirs.length)]);
  }
}
