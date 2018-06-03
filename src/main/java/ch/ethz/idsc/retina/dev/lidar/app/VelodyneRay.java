// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

public class VelodyneRay {
  private final float dx;
  private final float dy;
  public final int index;

  public VelodyneRay(float dx, float dy, int index) {
    this.dx = dx;
    this.dy = dy;
    this.index = index;
  }

  public float[] getCoord(float distance) {
    return new float[] { //
        dx * distance, //
        dy * distance //
    };
  }
}
