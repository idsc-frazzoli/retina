// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import java.util.function.Consumer;

/* package */ enum Vmu931Reply {
  SELFTEST("Test passed. Your device works fine."), //
  CALIBRATION("Calibration completed."), //
  ;
  private final String message_success;

  private Vmu931Reply(String message_success) {
    this.message_success = message_success;
  }

  /** @param message
   * @param consumer */
  public static void match(String message, Consumer<Vmu931Reply> consumer) {
    for (Vmu931Reply vmu931Reply : Vmu931Reply.values())
      if (vmu931Reply.message_success.equals(message))
        consumer.accept(vmu931Reply);
  }
}
