// code by mh, jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.calib.steer.RimoTwdOdometry;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class DirectTorqueVectoringModuleTest extends TestCase {
  public void testSimple() throws Exception {
    TorqueVectoringModule torqueVectoringModule = new DirectTorqueVectoringModule();
    torqueVectoringModule.first();
    torqueVectoringModule.last();
  }

  public void testControl() throws Exception {
    TorqueVectoringModule torqueVectoringModule = new DirectTorqueVectoringModule();
    torqueVectoringModule.first();
    {
      RimoGetEvent rimoGetEvent = RimoGetEvents.create(100, 200);
      Scalar vx = RimoTwdOdometry.tangentSpeed(rimoGetEvent);
      SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0, "SCE"));
      RimoPutEvent rimoPutEvent = torqueVectoringModule.derive( //
          steerColumnAdapter, RealScalar.ZERO, Tensors.of(vx, vx.zero(), Quantity.of(0.0, SI.PER_SECOND)));
      assertEquals(rimoPutEvent.putTireL.getTorque(), Quantity.of(0, NonSI.ARMS));
      assertEquals(rimoPutEvent.putTireR.getTorque(), Quantity.of(0, NonSI.ARMS));
    }
    // full forward
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(200, 200);
    Scalar vx = RimoTwdOdometry.tangentSpeed(rimoGetEvent);
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0, "SCE"));
    RimoPutEvent rimoPutEvent = torqueVectoringModule.derive( //
        steerColumnAdapter, RealScalar.ONE, Tensors.of(vx, vx.zero(), Quantity.of(0.0, SI.PER_SECOND)));
    assertEquals(rimoPutEvent.putTireL.getTorque(), ManualConfig.GLOBAL.torqueLimit.negate());
    assertEquals(rimoPutEvent.putTireR.getTorque(), ManualConfig.GLOBAL.torqueLimit);
    // half forward slip right
    torqueVectoringModule.last();
  }
}
