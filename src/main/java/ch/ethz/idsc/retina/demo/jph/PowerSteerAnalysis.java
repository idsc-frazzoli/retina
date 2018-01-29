// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.demo.DubendorfHangarLog;
import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.lcm.autobox.MiscLcmServer;
import ch.ethz.idsc.retina.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.N;
import lcm.logging.Log.Event;

class PowerSteerTracker implements OfflineLogListener {
  private final Scalar DELTA;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private SteerGetEvent sge;
  private SteerPutEvent spe;
  private MiscGetEvent mge;
  TensorBuilder tensorBuilder = new TensorBuilder();

  public PowerSteerTracker(Scalar DELTA) {
    this.DELTA = DELTA;
  }

  @Override
  public void event(Scalar time, Event event, ByteBuffer byteBuffer) {
    String channel = event.channel;
    if (channel.equals(SteerLcmServer.CHANNEL_GET)) {
      sge = new SteerGetEvent(byteBuffer);
    } else //
    if (channel.equals(SteerLcmServer.CHANNEL_PUT)) {
      spe = SteerPutEvent.from(byteBuffer);
    } else //
    if (channel.equals(MiscLcmServer.CHANNEL_GET)) {
      mge = new MiscGetEvent(byteBuffer);
    }
    if (Scalars.lessThan(time_next, time)) {
      if (Objects.nonNull(sge) && Objects.nonNull(spe) && Objects.nonNull(mge)) {
        time_next = time.add(DELTA);
        tensorBuilder.flatten( //
            time.map(Magnitude.SECOND), //
            DoubleScalar.of(sge.getGcpRelRckPos()), //
            spe.getTorque().map(SteerPutEvent.RTORQUE), //
            mge.getSteerBatteryVoltage().map(Magnitude.VOLT) //
        );
      }
    }
  }
}

enum PowerSteerAnalysis {
  ;
  public static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void main(String[] args) throws IOException {
    for (DubendorfHangarLog dhl : DubendorfHangarLog.values()) {
      File file = dhl.file(LOG_ROOT);
      if (file.isFile()) {
        System.out.println(dhl);
        PowerSteerTracker powerSteerTracker = new PowerSteerTracker(Quantity.of(0.1, SI.SECOND));
        OfflineLogPlayer.process(file, powerSteerTracker);
        Tensor table = powerSteerTracker.tensorBuilder.getTensor();
        Export.of(UserHome.file(dhl.title() + ".csv"), table.map(N.DOUBLE));
      } else
        System.err.println(dhl);
      // break;
    }
  }
}
