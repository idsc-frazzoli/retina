// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.demo.DubendorfHangarLog;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NSingle;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;
import lcm.logging.Log.Event;

class PowerRimoTracker implements OfflineLogListener {
  private final Scalar delta;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private RimoGetEvent rge;
  private RimoPutEvent rpe;
  TensorBuilder tensorBuilder = new TensorBuilder();

  public PowerRimoTracker(Scalar delta) {
    this.delta = delta;
  }

  @Override
  public void event(Scalar time, Event event, ByteBuffer byteBuffer) {
    String channel = event.channel;
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      rge = new RimoGetEvent(byteBuffer);
    } else //
    if (channel.equals(RimoLcmServer.CHANNEL_PUT)) {
      rpe = RimoPutHelper.from(byteBuffer);
    }
    if (Scalars.lessThan(time_next, time)) {
      if (Objects.nonNull(rge) && Objects.nonNull(rpe)) {
        // System.out.println("export " + time.number().doubleValue());
        time_next = time.add(delta);
        tensorBuilder.flatten( //
            time.map(Magnitude.SECOND), //
            rge.getTireL.vector_raw(), //
            rge.getTireR.vector_raw(), //
            rpe.getTorque_Y_pair().map(RimoPutTire.MAGNITUDE_ARMS) // ARMS
        );
      }
    }
  }
}

enum PowerRimoAnalysis {
  ;
  public static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void main(String[] args) throws IOException {
    for (DubendorfHangarLog dhl : DubendorfHangarLog.values()) {
      File file = dhl.file(LOG_ROOT);
      if (file.isFile()) {
        System.out.println(dhl);
        PowerRimoTracker rimoTracker = new PowerRimoTracker(Quantity.of(0.1, SI.SECOND));
        OfflineLogPlayer.process(file, rimoTracker);
        Tensor table = rimoTracker.tensorBuilder.getTensor();
        Export.of(UserHome.file(dhl.title() + ".csv"), table.map(NSingle.FUNCTION));
      } else
        System.err.println(dhl);
      // break;
    }
  }
}
