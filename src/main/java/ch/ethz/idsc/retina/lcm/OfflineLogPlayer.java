// code by jph
package ch.ethz.idsc.retina.lcm;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;

public enum OfflineLogPlayer {
  ;
  private static final String END_OF_FILE = "EOF";
  private static final Unit UNIT_US = Unit.of("us");

  public static void process(File file, OfflineLogListener offlineLogListener) throws IOException {
    Log log = new Log(file.toString(), "r");
    Long tic = null;
    try {
      while (true) {
        Event event = log.readNext();
        if (Objects.isNull(tic))
          tic = event.utime;
        BinaryBlob binaryBlob = new BinaryBlob(event.data);
        offlineLogListener.event( //
            UnitSystem.SI().apply(Quantity.of(event.utime - tic, UNIT_US)), //
            event.channel, //
            ByteBuffer.wrap(binaryBlob.data).order(ByteOrder.LITTLE_ENDIAN));
      }
    } catch (Exception exception) {
      if (!END_OF_FILE.equals(exception.getMessage()))
        throw exception;
    }
  }
}
