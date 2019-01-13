// code by jph
package ch.ethz.idsc.retina.lidar.hdl32e;

public enum Hdl32eDevice {
  INSTANCE;
  // ---
  public final int LASERS = 32;
  final float[] IR = new float[LASERS];
  final float[] IZ = new float[LASERS];
  /** quote from the user's manual, p.12: "the interleaving firing pattern is
   * designed to avoid potential ghosting caused primarily by retro-reflection" */
  private final int[] ORDERING = new int[] { //
      -23, -7, // 0
      -22, -6, // 2
      -21, -5, // 4
      -20, -4, // 6
      -19, -3, // 8
      -18, -2, // 10
      -17, -1, // 12
      -16, +0, // 14, 15
      -15, +1, //
      -14, +2, //
      -13, +3, //
      -12, +4, //
      -11, +5, //
      -10, +6, //
      -9, +7, //
      -8, +8 };

  private Hdl32eDevice() {
    /** angular spacing between the lasers */
    final double inclination = 4.0 / 3.0;
    for (int laser = 0; laser < LASERS; ++laser) {
      double theta = ORDERING[laser] * inclination * Math.PI / 180;
      IR[laser] = (float) Math.cos(theta);
      IZ[laser] = (float) Math.sin(theta);
    }
  }
}
