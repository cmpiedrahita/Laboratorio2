package co.eci.snake.core.engine;

import co.eci.snake.core.GameState;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Reloj del juego que controla el repintado periodico.
 * Maneja los estados: STOPPED, RUNNING, PAUSED.
 */
public final class GameClock implements AutoCloseable {
  // Ejecutor para tareas periodicas
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  // Periodo entre ticks en milisegundos
  private final long periodMillis;
  // Accion a ejecutar en cada tick
  private final Runnable tick;
  // Estado actual del reloj
  private final java.util.concurrent.atomic.AtomicReference<GameState> state = new AtomicReference<>(GameState.STOPPED);

  /**
   * Constructor.
   * @param periodMillis Periodo entre ticks
   * @param tick Accion a ejecutar periodicamente
   */
  public GameClock(long periodMillis, Runnable tick) {
    if (periodMillis <= 0) throw new IllegalArgumentException("periodMillis must be > 0");
    this.periodMillis = periodMillis;
    this.tick = java.util.Objects.requireNonNull(tick, "tick");
  }

  /**
   * Inicia el reloj.
   * Comienza a ejecutar el tick periodicamente.
   */
  public void start() {
    // Solo iniciar si esta detenido
    if (state.compareAndSet(GameState.STOPPED, GameState.RUNNING)) {
      scheduler.scheduleAtFixedRate(() -> {
        // Solo ejecutar tick si esta corriendo
        if (state.get() == GameState.RUNNING) tick.run();
      }, 0, periodMillis, TimeUnit.MILLISECONDS);
    }
  }

  /**
   * Pausa el reloj.
   * Deja de ejecutar el tick.
   */
  public void pause()  { state.set(GameState.PAUSED); }
  
  /**
   * Reanuda el reloj.
   * Vuelve a ejecutar el tick.
   */
  public void resume() { state.set(GameState.RUNNING); }
  
  /**
   * Detiene el reloj completamente.
   */
  public void stop()   { state.set(GameState.STOPPED); }
  
  /**
   * Obtiene el estado actual del reloj.
   * @return Estado actual
   */
  public GameState getState() { return state.get(); }
  
  /**
   * Cierra el reloj y libera recursos.
   */
  @Override public void close() { scheduler.shutdownNow(); }
}
