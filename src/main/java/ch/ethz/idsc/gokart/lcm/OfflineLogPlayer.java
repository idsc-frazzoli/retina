// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;

/** Remark:
 * the timestamps of the lcm event sequence as recorded are NOT guaranteed to be monotonous.
 * occasionally, the event that follows another may have a smaller timestamp.
 * Observed "step backs" are 1[us], 18[us], or 70[us]. */
public enum OfflineLogPlayer {
  ;
  public static final String END_OF_FILE = "EOF";

  public static void process(File file, OfflineLogListener... offlineLogListeners) throws IOException {
    process(file, Arrays.asList(offlineLogListeners));
  }

  public static void process(File file, Collection<? extends OfflineLogListener> offlineLogListeners) throws IOException {
    Set<String> set = new HashSet<>();
    Log log = new Log(file.toString(), "r");
    Long tic = null;
    try {
      while (true) {
        Event event = log.readNext();
        if (Objects.isNull(tic))
          tic = event.utime;
        BinaryBlob binaryBlob = null;
        try {
          binaryBlob = new BinaryBlob(event.data);
        } catch (Exception exception) {
          if (set.add(event.channel))
            System.err.println("not a binary blob: " + event.channel);
        }
        if (Objects.nonNull(binaryBlob)) {
          Scalar time = UnitSystem.SI().apply(Quantity.of(event.utime - tic, NonSI.MICRO_SECOND));
          for (OfflineLogListener offlineLogListener : offlineLogListeners)
            offlineLogListener.event(time, event.channel, ByteBuffer.wrap(binaryBlob.data).order(ByteOrder.LITTLE_ENDIAN));
        }
      }
    } catch (Exception exception) {
      if (!END_OF_FILE.equals(exception.getMessage()))
        throw exception;
    }
  }
}
