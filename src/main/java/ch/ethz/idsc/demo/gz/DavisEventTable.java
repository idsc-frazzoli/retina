// code by gz
package ch.ethz.idsc.demo.gz;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class DavisEventTable implements OfflineTableSupplier, DavisDvsListener {
  /** the several events are packed into a single message.
   * the DavisDvsDatagramDecoder helps to step through all events in a single message */
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Scalar delta;
  // ---
  private Scalar time_next; // = Quantity.of(0, SI.SECOND);
  private RimoGetEvent rge;
  private RimoPutEvent rpe;
  /** counters with long precision (using int results in overflow) */
  long[] events = new long[2]; //
  private GokartStatusEvent gse;

  public DavisEventTable(Scalar delta) {
    this.delta = delta;
    time_next = delta;
    // the decoder listens to "this" because this
    // class DavisEventTable implements DavisDvsListener
    davisDvsDatagramDecoder.addDvsListener(this);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    ++events[davisDvsEvent.i];
    // System.out.println(davisDvsEvent);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (Scalars.lessThan(time_next, time) && //
        Objects.nonNull(gse) && Objects.nonNull(rge) && Objects.nonNull(rpe)) {
      time_next = time_next.add(delta);
      Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rge);
      Scalar rate = ChassisGeometry.GLOBAL.odometryTurningRate(rge);
      tableBuilder.appendRow( //
          time_next.map(Magnitude.SECOND), //
          Tensors.vectorLong(events), //
          speed.map(Magnitude.VELOCITY).map(Round._2), //
          rate.map(Magnitude.ANGULAR_RATE).map(Round._4) //
      // TODO davis imu
      );
      events[0] = 0;
      events[1] = 0;
    }
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      rge = new RimoGetEvent(byteBuffer);
    } else //
    if (channel.equals(RimoLcmServer.CHANNEL_PUT)) {
      rpe = RimoPutHelper.from(byteBuffer);
    } else //
    if (channel.equals("davis240c.overview.dvs")) { // TODO get channel name from code
      // this is where the decoder obtains a single message and
      // invokes the function davisDvs as often as necessary
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
    if (channel.equals(GokartLcmChannel.STATUS)) {
      gse = new GokartStatusEvent(byteBuffer);
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
