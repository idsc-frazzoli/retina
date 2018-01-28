// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.demo.DubendorfHangarLog;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetTire;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import lcm.logging.Log.Event;

class RimoTracker implements OfflineLogListener {
  public static final ScalarUnaryOperator MAGNITUDE_S = QuantityMagnitude.singleton("s");
  private final Scalar DELTA;
  // ---
  private Scalar time_next = Quantity.of(0, "s");
  private RimoGetEvent rge;
  private RimoPutEvent rpe;
  private GokartStatusEvent gse;
  TensorBuilder tensorBuilder = new TensorBuilder();

  public RimoTracker(Scalar DELTA) {
    this.DELTA = DELTA;
  }

  @Override
  public void event(Scalar time, Event event, ByteBuffer byteBuffer) {
    String channel = event.channel;
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      rge = new RimoGetEvent(byteBuffer);
    } else //
    if (channel.equals(RimoLcmServer.CHANNEL_PUT)) {
      rpe = RimoPutHelper.from(byteBuffer);
    } else //
    if (channel.equals(GokartLcmChannel.STATUS)) {
      gse = new GokartStatusEvent(byteBuffer);
    }
    if (Scalars.lessThan(time_next, time)) {
      if (Objects.nonNull(rge) && Objects.nonNull(rpe) && Objects.nonNull(gse) && gse.isSteerColumnCalibrated()) {
        // System.out.println("export " + time.number().doubleValue());
        time_next = time.add(DELTA);
        tensorBuilder.flatten( //
            time.map(MAGNITUDE_S), //
            rge.getAngularRate_Y_pair().map(RimoGetTire.MAGNITUDE_RATE), // rad/s
            rpe.getTorque_Y_pair().map(RimoPutTire.MAGNITUDE_ARMS), // ARMS
            gse.getSteerColumnEncoderCentered().map(SteerPutEvent.ENCODER));
      }
    }
  }
}

enum RimoAnalysis {
  ;
  public static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void main(String[] args) throws IOException {
    for (DubendorfHangarLog dhl : DubendorfHangarLog.values()) {
      File file = dhl.file(LOG_ROOT);
      if (file.isFile()) {
        System.out.println(dhl);
        RimoTracker rimoTracker = new RimoTracker(Quantity.of(0.1, "s"));
        OfflineLogPlayer.process(file, rimoTracker);
        Tensor table = rimoTracker.tensorBuilder.getTensor();
        Export.of(UserHome.file(dhl.title() + ".csv"), table.map(N.DOUBLE));
      } else
        System.err.println(dhl);
      // break;
    }
  }
}
