// code by jph
package ch.ethz.idsc.gokart.dev;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.retina.dev.joystick.ManualControlListener;
import ch.ethz.idsc.retina.dev.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.StartAndStoppable;

public enum ManualControlSingleton implements StartAndStoppable {
  INSTANCE;
  // ---
  private final ManualControlProvider manualControlProvider = JoystickConfig.GLOBAL.createProvider();
  private final List<ManualControlListener> list = new CopyOnWriteArrayList<>();

  @Override
  public void start() {
    manualControlProvider.start();
    // manualControlProvider.
  }

  @Override
  public void stop() {
    manualControlProvider.stop();
  }

  public void addListener(ManualControlListener manualControlListener) {
    list.add(manualControlListener);
  }

  public void removeListener(ManualControlListener manualControlListener) {
    list.remove(manualControlListener);
  }
}
