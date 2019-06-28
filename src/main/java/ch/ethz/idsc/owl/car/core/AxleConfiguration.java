// code by jph
package ch.ethz.idsc.owl.car.core;

// TODO JPH move to package gokart.X
@FunctionalInterface
public interface AxleConfiguration {
  /** @param wheel index: 0 corresponds to left wheel of axle, and 1 corresponds to right wheel of axle
   * @return configuration of wheel with given index */
  WheelConfiguration wheel(int wheel);
}
