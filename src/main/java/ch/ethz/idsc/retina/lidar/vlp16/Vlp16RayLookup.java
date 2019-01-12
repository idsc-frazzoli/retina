// code by jph
package ch.ethz.idsc.retina.lidar.vlp16;

import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.lidar.app.VelodyneRay;
import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;
import ch.ethz.idsc.tensor.sca.ArcTan;

/* package */ class Vlp16RayLookup {
  private final int bits;
  private final double emulation_deg;
  private final VelodyneRay[] velodyneRays;

  /** @param bits
   * @param flip
   * @param angle_offset
   * @param tiltY in radians
   * @param emulation_deg in degree */
  public Vlp16RayLookup(int bits, boolean flip, double angle_offset, double tiltY, double emulation_deg) {
    this.bits = bits;
    int length = VelodyneStatics.AZIMUTH_RESOLUTION / (1 << bits);
    AngleVectorLookupFloat angleVectorLookupFloat = //
        new AngleVectorLookupFloat(length, flip, angle_offset);
    double tiltY_deg = Math.toDegrees(tiltY);
    this.emulation_deg = emulation_deg;
    velodyneRays = new VelodyneRay[length];
    for (int rotational = 0; rotational < length; ++rotational) {
      float dx = angleVectorLookupFloat.dx(rotational);
      float dy = angleVectorLookupFloat.dy(rotational);
      double angle = ArcTan.of(dx, dy).number().doubleValue();
      double tilt = tiltY_deg * Math.cos(angle); // Math.toDegrees(0.04) * cos(angle)
      int index = degreeToLidarID(closestRay(tilt)) * 3;
      velodyneRays[rotational] = new VelodyneRay( //
          dx * VelodyneStatics.TO_METER_FLOAT, //
          dy * VelodyneStatics.TO_METER_FLOAT, //
          index);
    }
  }

  public VelodyneRay velodyneRay(int rotational) {
    return velodyneRays[rotational >> bits];
  }

  /** @param tilt_deg
   * @return */
  /* package */ int closestRay(double tilt_deg) {
    return (int) (2 * Math.round((tilt_deg + emulation_deg - 1) / 2) + 1);
  }

  /** @param degree in {-15, -13, -11, -1, +1, +3, ..., +15}
   * @return lidar ID */
  static int degreeToLidarID(int degree) {
    return (degree + 15) % 15;
  }
}
