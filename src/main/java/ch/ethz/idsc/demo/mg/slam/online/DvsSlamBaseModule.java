// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;
import ch.ethz.idsc.demo.mg.slam.config.EventCamera;
import ch.ethz.idsc.gokart.core.pure.SlamCurvePurePursuitModule;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** runs the SLAM algorithm and a pure pursuit module which gets a lookAhead point in the go kart frame
 * from the SLAM algorithm */
public class DvsSlamBaseModule extends AbstractClockedModule {
  private final EventCamera eventCamera;
  private final SlamCurvePurePursuitModule slamCurvePurePursuitModule;
  private final OnlineSlamWrap onlineSlamWrap;

  // TODO first parameter may be obsolete
  DvsSlamBaseModule(EventCamera eventCamera, SlamAlgoConfig slamAlgoConfig) {
    this.eventCamera = eventCamera;
    eventCamera.slamCoreConfig.slamAlgoConfig = slamAlgoConfig; // TODO global solution not good
    onlineSlamWrap = new OnlineSlamWrap();
    slamCurvePurePursuitModule = new SlamCurvePurePursuitModule();
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    onlineSlamWrap.start();
    // ---
    slamCurvePurePursuitModule.launch();
  }

  @Override // from AbstractModule
  protected void last() {
    onlineSlamWrap.stop();
    // ---
    slamCurvePurePursuitModule.terminate();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    Optional<Tensor> optional = onlineSlamWrap.getSlamPrcContainer().getCurve();
    slamCurvePurePursuitModule.setCurve(optional);
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return eventCamera.slamCoreConfig.purePursuitUpdateRate;
  }

  public static void standalone() throws Exception {
    EventCamera eventCamera = EventCamera.DAVIS;
    // ---
    DvsSlamBaseModule davisSlamBaseModule = new DvsSlamBaseModule(eventCamera, eventCamera.slamCoreConfig.slamAlgoConfig);
    davisSlamBaseModule.launch();
    TimeUnit.SECONDS.sleep(eventCamera.slamCoreConfig.dvsConfig.logFileDuration.number().longValue());
    davisSlamBaseModule.terminate();
  }
}
