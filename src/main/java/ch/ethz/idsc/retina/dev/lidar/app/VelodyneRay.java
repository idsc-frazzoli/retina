// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;

public class VelodyneRay {
  public final float dx;
  public final float dy;
  public final int index;

  public VelodyneRay(float dx, float dy, int index) {
    this.dx = dx;
    this.dy = dy;
    this.index = index;
  }

  public float[] getCoord(float distance) {
    return new float[] { //
        dx * distance * VelodyneStatics.TO_METER_FLOAT, //
        dy * distance * VelodyneStatics.TO_METER_FLOAT //
    };
  }

  public void insert(float distance, float[] array) {
    array[0] = dx * distance * VelodyneStatics.TO_METER_FLOAT; //
    array[1] = dy * distance * VelodyneStatics.TO_METER_FLOAT; //
  }
}
