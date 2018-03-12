// code by jph,gz
package ch.ethz.idsc.demo.gz;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
//import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
//import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent; // not useful now
//import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;// dev.davis.data?
//import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
//import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.app.DavisTallyEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;

//import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;

public class DavisEventTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Scalar delta;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  //private RimoGetEvent rge;
  //private RimoPutEvent rpe;
  
  // keeping the same style 
  //private DavisTallyEvent dte;  
  DavisTallyEvent dte;
  
  private GokartStatusEvent gse;

  public DavisEventTable(Scalar delta) {
    this.delta = delta;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
   // if (channel.equals(DavisLcmServer.CHANNEL_GET)) {
   //   dte = new DavisTallyEvent(byteBuffer);
	  
   // } else //
   // if (channel.equals(DavisLcmServer.CHANNEL_PUT)) {
   //   rpe = RimoPutHelper.from(byteBuffer);
  //  } else //
    if (channel.equals(GokartLcmChannel.STATUS)) {
      gse = new GokartStatusEvent(byteBuffer);
    }
    if (Scalars.lessThan(time_next, time)) {
      if (Objects.nonNull(dte)) {
        time_next = time.add(delta);
        for (int index = 0; index < dte.binLast; ++index) {
        	int posevents = dte.bin[index][0];
        	int negevents = dte.bin[index][1];
        	int totevents = posevents + negevents;// Is this the correct way of doing this?
        }
      //Scalar speed = Mean.of(rates).multiply(ChassisGeometry.GLOBAL.tireRadiusRear).Get();
      // rad/s * m == (m / s) / m
      // Scalar rate = Differences.of(rates).Get(0) //
      //     .multiply(RationalScalar.HALF) //
      //     .multiply(ChassisGeometry.GLOBAL.tireRadiusRear) //
      //     .divide(ChassisGeometry.GLOBAL.yTireRear);
        tableBuilder.appendRow( //
            time.map(Magnitude.SECOND), //
            posevents.map(Magnitude.ONE),//
            negevents.map(Magnitude.ONE),//
            totevents.map(Magnitude.ONE)
            //rpe.getTorque_Y_pair().map(RimoPutTire.MAGNITUDE_ARMS), //
            //SteerConfig.GLOBAL.getAngleFromSCE(gse), //
            //speed.map(Magnitude.VELOCITY), //
            //rate.map(Magnitude.ANGULAR_RATE) //
        );
      }
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
