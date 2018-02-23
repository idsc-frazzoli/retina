// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.owl.bot.rice.Duncan1StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class HmiSimpleDriveJoystickTest extends TestCase {
  public void testSimple() {
    Tensor speed = Tensors.fromString("{10[m*s^-1]}");
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        new Duncan1StateSpaceModel(Quantity.of(0.2, "s^-1")), //
        MidpointIntegrator.INSTANCE, //
        new StateTime(speed, Quantity.of(0, "s")));
    Tensor accel = Tensors.of(Quantity.of(3, "m*s^-2"));
    episodeIntegrator.move(accel, Quantity.of(1, "s"));
    StateTime stateTime = episodeIntegrator.tail();
    // System.out.println(stateTime.toInfoString());
    assertTrue(Scalars.lessThan(speed.Get(0), stateTime.state().Get(0)));
  }
}
