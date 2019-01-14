// code by gz
package ch.ethz.idsc.demo.gz;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class DavisEventTable implements OfflineTableSupplier, DavisDvsListener {
  /** the several events are packed into a single message.
   * the DavisDvsDatagramDecoder helps to step through all events in a single message */
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  // private static final Scalar DVS_PERIOD = Quantity.of(RationalScalar.of(1, 50), SI.SECOND);
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Scalar delta;
  // ---
  private Scalar time_next;
  private RimoGetEvent rge;
  // private RimoPutEvent rpe;
  /** counters with long precision (using int results in overflow) */
  long[] events = new long[2]; //
  // private GokartStatusEvent gse;

  public DavisEventTable(Scalar delta) {
    this.delta = delta;
    time_next = delta;
    // the decoder listens to "this" because this
    // class DavisEventTable implements DavisDvsListener
    davisDvsDatagramDecoder.addDvsListener(this);
  }

  Integer reference = null;

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (Objects.isNull(reference))
      reference = davisDvsEvent.time;
    ++events[davisDvsEvent.i];
    Scalar now = Quantity.of((davisDvsEvent.time - reference) * 1e-6, SI.SECOND);
    if (Scalars.lessEquals(time_next, now)) { // GZ not as precise as could be
      Scalar speed = Objects.isNull(rge) ? Quantity.of(0, SI.VELOCITY) : ChassisGeometry.GLOBAL.odometryTangentSpeed(rge);
      Scalar rate = Objects.isNull(rge) ? Quantity.of(0, SI.PER_SECOND) : ChassisGeometry.GLOBAL.odometryTurningRate(rge);
      tableBuilder.appendRow( //
          Magnitude.SECOND.apply(time_next.subtract(delta)), //
          Tensors.vectorLong(events), speed.map(Magnitude.VELOCITY).map(Round._2), //
          rate.map(Magnitude.PER_SECOND).map(Round._4) //
      );
      time_next = time_next.add(delta);
      events[0] = 0;
      events[1] = 0;
    }
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      rge = new RimoGetEvent(byteBuffer);
    } else //
    if (channel.equals(RimoLcmServer.CHANNEL_PUT)) {
      // rpe = RimoPutHelper.from(byteBuffer);
    } else //
    if (channel.equals("davis240c.overview.dvs")) { // GZ get channel name from code
      // this is where the decoder obtains a single message and
      // invokes the function davisDvs as often as necessary
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
    if (channel.equals(GokartLcmChannel.STATUS)) {
      // gse = new GokartStatusEvent(byteBuffer);
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
