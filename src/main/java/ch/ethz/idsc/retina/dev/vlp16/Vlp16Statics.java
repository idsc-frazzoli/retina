// code by jph
package ch.ethz.idsc.retina.dev.vlp16;

public enum Vlp16Statics {
  ;
  /** default port on which vlp16 publishes firing data */
  public static final int RAY_DEFAULT_PORT = 2368;
  public static final int RAY_PACKET_LENGTH = 1206;
  // ---
  /** default port on which vlp16 publishes positioning data */
  public static final int POS_DEFAULT_PORT = 8308;
  public static final int POS_PACKET_LENGTH = 512;
  /** quote from the user's manual, p.12:
   * "the interleaving firing pattern is designed to avoid
   * potential ghosting caused primarily by retro-reflection" */
  public static final int[] ORDERING = new int[] { //
      -23, -7, //
      -22, -6, //
      -21, -5, //
      -20, -4, //
      -19, -3, //
      -18, -2, //
      -17, -1, //
      -16, +0, //
      -15, +1, //
      -14, +2, //
      -13, +3, //
      -12, +4, //
      -11, +5, //
      -10, +6, //
      -9, +7, //
      -8, +8 };
}
