// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;

public class GokartLogFileIndexer implements OfflineLogListener {
  public static GokartLogFileIndexer create(File file) throws IOException {
    GokartLogFileIndexer lcmLogFileIndexer = new GokartLogFileIndexer(file);
    System.out.print("building index... ");
    OfflineLogPlayer.process(file, lcmLogFileIndexer);
    System.out.println("done.");
    return lcmLogFileIndexer;
  }

  // ---
  private static final Scalar resolution = Quantity.of(0.25, SI.SECOND);
  private final File file;
  private final List<Integer> raster2event = new ArrayList<>();
  private final TableBuilder raster2speed = new TableBuilder();
  // ---
  private int event_count;
  private Tensor rates = Array.zeros(2);

  private GokartLogFileIndexer(File file) {
    this.file = file;
    append(0, rates);
  }

  private void append(int count, Tensor rates) {
    raster2event.add(count);
    raster2speed.appendRow(rates);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    int index = time.divide(resolution).number().intValue();
    if (raster2event.size() <= index)
      append(event_count, rates);
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
      rates = rimoGetEvent.getAngularRate_Y_pair().map(Magnitude.PER_SECOND).map(Scalar::abs);
      // Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent);
      // Scalar raw = Magnitude.VELOCITY.apply(speed.abs()); // abs !
      // raster2speed.set(index, Max.of(raw, raster2speed.get(index)));
    }
    ++event_count;
  }

  public File file() {
    return file;
  }

  public Stream<Tensor> raster2speed() {
    return raster2speed.stream();
  }

  public int getEventIndex(int x0) {
    return raster2event.get(x0);
  }
}
