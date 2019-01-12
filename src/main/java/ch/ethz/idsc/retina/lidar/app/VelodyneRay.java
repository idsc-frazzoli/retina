// code by jph
package ch.ethz.idsc.retina.lidar.app;

public class VelodyneRay {
  private final float dx;
  private final float dy;
  /** position offset in sensing data array */
  public final int offset;

  public VelodyneRay(float dx, float dy, int offset) {
    this.dx = dx;
    this.dy = dy;
    this.offset = offset;
  }

  public float[] getCoord(float distance) {
    return new float[] { //
        dx * distance, //
        dy * distance //
    };
  }
}
