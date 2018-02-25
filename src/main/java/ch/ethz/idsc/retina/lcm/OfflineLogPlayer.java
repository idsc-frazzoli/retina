// code by jph
package ch.ethz.idsc.retina.lcm;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Round;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;

public enum OfflineLogPlayer {
  ;
  private static final String END_OF_FILE = "EOF";
  private static final Unit UNIT_US = Unit.of("us");

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
        if (binaryBlob != null)
          for (OfflineLogListener offlineLogListener : offlineLogListeners) {
            Scalar time = UnitSystem.SI().apply(Quantity.of(event.utime - tic, UNIT_US)).map(Round._6).Get();
            ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data).order(ByteOrder.LITTLE_ENDIAN);
            offlineLogListener.event(time, event.channel, byteBuffer);
          }
      }
    } catch (Exception exception) {
      if (!END_OF_FILE.equals(exception.getMessage()))
        throw exception;
    }
  }
}
