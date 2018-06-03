// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;
import ch.ethz.idsc.tensor.sca.ArcTan;

public class VelodyneRayLookup {
  private final double tiltY_deg;
  private final double emulation_deg;
  private final VelodyneRay[] velodyneRays;

  public VelodyneRayLookup(int length, boolean flip, double angle_offset, double tiltY, double emulation_deg) {
    AngleVectorLookupFloat angleVectorLookupFloat = //
        new AngleVectorLookupFloat(length, flip, angle_offset);
    tiltY_deg = Math.toDegrees(tiltY);
    this.emulation_deg = emulation_deg;
    velodyneRays = new VelodyneRay[length];
    for (int rotational = 0; rotational < length; ++rotational) {
      // TODO pre-multiply dx, dy with VelodyneStatics.TO_METER_FLOAT
      float dx = angleVectorLookupFloat.dx(rotational);
      float dy = angleVectorLookupFloat.dy(rotational);
      double angle = ArcTan.of(dx, dy).number().doubleValue();
      double tilt = tiltY_deg * Math.cos(angle); // Math.toDegrees(0.04) * cos(angle)
      int index = degreeToLidarID(closestRay(tilt)) * 3;
      velodyneRays[rotational] = new VelodyneRay(dx, dy, index);
    }
  }

  public VelodyneRay velodyneRay(int rotational) {
    return velodyneRays[rotational];
  }

  /** @param tilt_deg
   * @return */
  private int closestRay(double tilt_deg) {
    return (int) (2 * Math.round((tilt_deg + emulation_deg - 1) / 2) + 1);
  }

  /** @param degree in {-15, -13, -11, -1, +1, +3, ..., +15}
   * @return lidar ID */
  static int degreeToLidarID(int degree) {
    return (degree + 15) % 15;
  }
}
