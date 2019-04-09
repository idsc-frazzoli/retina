// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

public enum EventCamera {
  DAVIS(DavisSlamPrcConfig.GLOBAL, DavisSlamCoreConfig.GLOBAL, new DavisDvsConfig()), //
  SEYE(SEyeSlamPrcConfig.GLOBAL, SEyeSlamCoreConfig.GLOBAL, new SEyeDvsConfig()), //
  ;
  public final SlamPrcConfig slamPrcConfig;
  public final SlamCoreConfig slamCoreConfig;
  // TOOD check if still necesssary or refactor
  private final DvsConfig dvsConfig;

  private EventCamera(SlamPrcConfig slamPrcConfig, SlamCoreConfig slamCoreConfig, DvsConfig dvsConfig) {
    this.slamPrcConfig = slamPrcConfig;
    this.slamCoreConfig = slamCoreConfig;
    this.dvsConfig = dvsConfig;
  }
}
