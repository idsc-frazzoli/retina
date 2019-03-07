// code by mg
package ch.ethz.idsc.demo;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Round;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;

/** plays log file until */
public enum BoundedOfflineLogPlayer {
  ;
  /** @param file
   * @param duration_us
   * @param offlineLogListeners
   * @throws IOException */
  public static void process(File file, long duration_us, OfflineLogListener... offlineLogListeners) throws IOException {
    process(file, duration_us, Arrays.asList(offlineLogListeners));
  }

  /** @param file
   * @param duration_us number of micro-seconds to replay log file until
   * @param offlineLogListeners
   * @throws IOException */
  public static void process(File file, long duration_us, Collection<? extends OfflineLogListener> offlineLogListeners) throws IOException {
    Set<String> set = new HashSet<>();
    Log log = new Log(file.toString(), "r");
    Long tic = null;
    try {
      while (true) {
        Event event = log.readNext();
        if (Objects.isNull(tic))
          tic = event.utime;
        if (event.utime - tic <= duration_us) {
          BinaryBlob binaryBlob = null;
          try {
            binaryBlob = new BinaryBlob(event.data);
          } catch (Exception exception) {
            if (set.add(event.channel))
              System.err.println("not a binary blob: " + event.channel);
          }
          if (Objects.nonNull(binaryBlob))
            for (OfflineLogListener offlineLogListener : offlineLogListeners) {
              Scalar time = UnitSystem.SI().apply(Quantity.of(event.utime - tic, NonSI.MICRO_SECOND)).map(Round._6).Get();
              ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data).order(ByteOrder.LITTLE_ENDIAN);
              offlineLogListener.event(time, event.channel, byteBuffer);
            }
        }
      }
    } catch (Exception exception) {
      if (!OfflineLogPlayer.END_OF_FILE.equals(exception.getMessage()))
        throw exception;
    }
  }
}
