// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.gokart.core.pure.SlamCurvePurePursuitModule;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** runs the SLAM algorithm and a pure pursuit module which gets a lookAhead point in the go kart frame
 * from the SLAM algorithm */
public class DvsSlamBaseModule extends AbstractClockedModule {
  private final SlamCurvePurePursuitModule slamCurvePurePursuitModule;
  private final OnlineSlamWrap onlineSlamWrap;

  DvsSlamBaseModule(SlamAlgoConfig slamAlgoConfig, String cameraType) {
    SlamDvsConfig.cameraType = cameraType;
    SlamCoreConfig.GLOBAL.slamAlgoConfig = slamAlgoConfig;
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
    Optional<Tensor> curve = onlineSlamWrap.getSlamPrcContainer().getCurve();
    slamCurvePurePursuitModule.setCurve(curve);
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return SlamCoreConfig.GLOBAL.purePursuitUpdateRate;
  }

  public static void standalone() throws Exception {
    DvsSlamBaseModule davisSlamBaseModule = new DvsSlamBaseModule(SlamCoreConfig.GLOBAL.slamAlgoConfig, SlamDvsConfig.cameraType);
    davisSlamBaseModule.launch();
    TimeUnit.SECONDS.sleep(SlamCoreConfig.GLOBAL.dvsConfig.logFileDuration.number().longValue());
    davisSlamBaseModule.terminate();
  }
}
