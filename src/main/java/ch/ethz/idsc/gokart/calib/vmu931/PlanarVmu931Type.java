// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

public enum PlanarVmu931Type {
  NATIVE(NativePlanarVmu931Imu.INSTANCE), //
  FLIPPED(FlippedPlanarVmu931Imu.INSTANCE), //
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
