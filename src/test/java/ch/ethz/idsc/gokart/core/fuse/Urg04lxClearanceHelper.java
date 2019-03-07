// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.car.math.CircleClearanceTracker;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Urg04lxClearanceHelper {
  ;
  static boolean isPathObstructed(SteerColumnInterface steerColumnInterface, FloatBuffer floatBuffer) {
    if (steerColumnInterface.isSteerColumnCalibrated()) {
      SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
      Scalar angle = steerMapping.getAngleFromSCE(steerColumnInterface); // <- calibration checked
      return isPathObstructed(angle, floatBuffer);
    }
    return true;
  }

  /** @param angle without unit but interpretation as radians
   * @param floatBuffer
   * @return */
  static boolean isPathObstructed(Scalar angle, FloatBuffer floatBuffer) {
    final int position = floatBuffer.position();
    final int size = floatBuffer.limit() / 2; // dimensionality of point: planar lidar
    // ---
    Scalar half = ChassisGeometry.GLOBAL.yHalfWidthMeter();
    CircleClearanceTracker clearanceTracker = new CircleClearanceTracker( //
        DoubleScalar.of(1), half, angle, SensorsConfig.GLOBAL.urg04lx, SafetyConfig.GLOBAL.getClearanceClip());
    // ---
    for (int index = 0; index < size; ++index) {
      float px = floatBuffer.get();
      float py = floatBuffer.get();
      clearanceTracker.isObstructed(Tensors.vector(px, py));
    }
    floatBuffer.position(position);
    return clearanceTracker.violation().isPresent();
  }
}
