// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.nio.FloatBuffer;

import ch.ethz.idsc.owl.math.map.Se2ForwardAction;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.retina.util.math.Se2AxisYProject;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Clip;

public enum StaticHelper {
  ;
  /* package */ static boolean isPathObstructed( //
      SteerColumnInterface steerColumnInterface, FloatBuffer floatBuffer) {
    if (steerColumnInterface.isSteerColumnCalibrated()) {
      Scalar angle = SteerConfig.GLOBAL.getAngleFromSCE(steerColumnInterface); // <- calibration checked
      return isPathObstructed(angle, floatBuffer);
    }
    return true;
  }

  /** @param angle without unit but interpretation as radians
   * @param floatBuffer
   * @return */
  /* package */ static boolean isPathObstructed(Scalar angle, FloatBuffer floatBuffer) {
    final int position = floatBuffer.position();
    final int size = floatBuffer.limit() / 2; // dimensionality of point: planar lidar
    Scalar half = ChassisGeometry.GLOBAL.yHalfWidthMeter();
    Clip clip_Y = Clip.function(half.negate(), half); // TODO there is a small error as gokart turns
    Scalar speed = RealScalar.ONE; // assume unit speed // TODO use actual speed in logic
    Tensor u = Tensors.of(speed, RealScalar.ZERO, angle.multiply(speed)).unmodifiable();
    final Scalar clearanceFrontMeter = SafetyConfig.GLOBAL.clearanceFrontMeter();
    Scalar min = clearanceFrontMeter;
    for (int index = 0; index < size; ++index) {
      float px = floatBuffer.get(); // TODO use unit m
      float py = floatBuffer.get();
      Tensor point = Tensors.vector(px + 1.67, py); // TODO redundant to Urg04 Config, and not 100% accurate
      final Scalar t = Se2AxisYProject.of(u, point);
      // negate() in the next line helps to move point from front of gokart to y-axis of rear axle
      Se2ForwardAction se2ForwardAction = new Se2ForwardAction(Se2Utils.integrate_g0(u.multiply(t.negate())));
      Tensor v = se2ForwardAction.apply(point);
      if (clip_Y.isInside(v.Get(1))) // check y-coordinate of back projected point
        min = Min.of(min, t); // negate t again
    }
    floatBuffer.position(position);
    // System.out.println("min = " + min + " " + isPathObstructed);
    return Scalars.lessThan(min, clearanceFrontMeter);
  }
}
