// code by jph
package ch.ethz.idsc.retina.joystick;

@FunctionalInterface
public interface ManualControlListener {
  /** @param manualControlInterface */
  void manualControl(ManualControlInterface manualControlInterface);
}
