// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import java.util.Optional;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.gokart.core.pure.WaypointPurePursuitModule;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

/** runs the SLAM algorithm and a pure pursuit module which gets a lookAhead point in the go kart frame
 * from the SLAM algorithm */
public class DavisSlamModule extends AbstractClockedModule {
  private final OnlineSlamWrap onlineSlamWrap = new OnlineSlamWrap(new SlamConfig());
  private final WaypointPurePursuitModule waypointPurePursuitModule = new WaypointPurePursuitModule();

  @Override // from AbstractModule
  protected void first() throws Exception {
    onlineSlamWrap.start();
    // ---
    waypointPurePursuitModule.launch();
  }

  @Override // from AbstractModule
  protected void last() {
    onlineSlamWrap.stop();
    // ---
    waypointPurePursuitModule.terminate();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    Optional<Tensor> lookAhead = onlineSlamWrap.getSlamContainer().getLookAhead();
    waypointPurePursuitModule.setLookAhead(lookAhead);
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return Quantity.of(0.1, SI.SECOND);
  }

  /***************************************************/
  public static void standalone() throws Exception {
    DavisSlamModule davisSlamModule = new DavisSlamModule();
    davisSlamModule.launch();
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
