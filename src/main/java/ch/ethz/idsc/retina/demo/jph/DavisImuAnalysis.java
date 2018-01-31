// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.demo.DubendorfHangarLog;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
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

class DavisImuTracker implements OfflineLogListener {
  private final Scalar delta;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private RimoGetEvent rge;
  private DavisImuFrame dif;
  TensorBuilder tensorBuilder = new TensorBuilder();

  public DavisImuTracker(Scalar delta) {
    this.delta = delta;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, Event event, ByteBuffer byteBuffer) {
    String channel = event.channel;
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      rge = new RimoGetEvent(byteBuffer);
    } else //
    if (channel.equals("davis240c.overview.atg")) {
      dif = new DavisImuFrame(byteBuffer);
    }
    if (Scalars.lessThan(time_next, time)) {
      if (Objects.nonNull(rge) && Objects.nonNull(dif)) {
        time_next = time.add(delta);
        tensorBuilder.flatten( //
            time.map(Magnitude.SECOND), //
            dif.accelImageFrame().map(Magnitude.ACCELERATION), //
            dif.temperature().map(Magnitude.DEGREE_CELSIUS), //
            dif.gyroImageFrame().map(Magnitude.ANGULAR_RATE), //
            rge.getAngularRate_Y_pair().map(Magnitude.ANGULAR_RATE) //
        );
      }
    }
  }
}

enum DavisImuAnalysis {
  ;
  public static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void main(String[] args) throws IOException {
    for (DubendorfHangarLog dubendorfHangarLog : DubendorfHangarLog.values()) {
      File file = dubendorfHangarLog.file(LOG_ROOT);
      if (file.isFile()) {
        System.out.println(dubendorfHangarLog);
        DavisImuTracker powerSteerTracker = new DavisImuTracker(Quantity.of(0.01, SI.SECOND));
        OfflineLogPlayer.process(file, powerSteerTracker);
        Tensor table = powerSteerTracker.tensorBuilder.getTensor();
        Export.of(UserHome.file(dubendorfHangarLog.title() + ".csv"), table.map(NSingle.FUNCTION));
      } else
        System.err.println(dubendorfHangarLog);
      // break;
    }
  }
}
