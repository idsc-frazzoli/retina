// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import ch.ethz.idsc.owly.demo.rice.Rice1StateSpaceModel;
import ch.ethz.idsc.owly.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owly.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class HmiSimpleDriveJoystickTest extends TestCase {
  public void testSimple() {
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        Rice1StateSpaceModel.of(RealScalar.ZERO), //
        MidpointIntegrator.INSTANCE, //
        new StateTime(Tensors.fromString("{0}"), Quantity.of(0, "s")));
    System.out.println(episodeIntegrator.tail().toInfoString());
    Scalar push = Quantity.of(1, "s^-1");
    // if (hasJoystick())
    // push = getSpeedLimit().multiply(RealScalar.of(_joystick.getLeftKnobDirectionUp()));
    // FIXME use increments
    episodeIntegrator.move(Tensors.of(push), Quantity.of(0.2, "s"));
    StateTime stateTime = episodeIntegrator.tail();
//    System.out.println(stateTime.toInfoString());
    // episodeIntegrator.move(u, now);
  }

  public void testFixed() {
    // FixedStateIntegrator.create(MidpointIntegrator.INSTANCE, Scalars.fromString("3[s]"), 1) //
    // .trajectory(new StateTime(), flow);
  }
}
