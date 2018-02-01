// code by jph
package ch.ethz.idsc.retina.lcm;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;

public enum OfflineLogPlayer {
  ;
  private static final String END_OF_FILE = "EOF";

  public static void process(File file, OfflineLogListener offlineLogListener) throws IOException {
    Log log = new Log(file.toString(), "r");
    Long tic = null;
    try {
      while (true) {
        Event event = log.readNext();
        if (Objects.isNull(tic))
          tic = event.utime;
        BinaryBlob binaryBlob = new BinaryBlob(event.data);
        ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data); // length == 524
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        Scalar scalar = Quantity.of(event.utime - tic, "us");
        offlineLogListener.event(UnitSystem.SI().apply(scalar), event.channel, byteBuffer);
      }
    } catch (Exception exception) {
      if (!END_OF_FILE.equals(exception.getMessage()))
        throw exception;
    }
  }
}
