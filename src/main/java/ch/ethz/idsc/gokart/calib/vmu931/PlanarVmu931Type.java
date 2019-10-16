// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

public enum PlanarVmu931Type {
  /** ante 20190208 */
  NATIVE(NativePlanarVmu931Imu.INSTANCE), //
  /** post [20190208 */
  FLIPPED(FlippedPlanarVmu931Imu.INSTANCE), //
  /** post 20190521 since GokartLogFile _20190521T150634_d2699045 */
  ROT90(Rot90PlanarVmu931Imu.INSTANCE), //
  ;
  private final PlanarVmu931Imu planarVmu931Imu;

  private PlanarVmu931Type(PlanarVmu931Imu planarVmu931Imu) {
    this.planarVmu931Imu = planarVmu931Imu;
  }

  public PlanarVmu931Imu planarVmu931Imu() {
    return planarVmu931Imu;
  }
}
