// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

public enum Hdl32eStatics {
  ;
  /** default port on which hdl32e publishes firing data */
  public static final int RAY_DEFAULT_PORT = 2368;
  public static final int POS_DEFAULT_PORT = 8308;
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
