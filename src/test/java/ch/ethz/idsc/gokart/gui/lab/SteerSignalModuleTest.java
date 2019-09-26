package ch.ethz.idsc.gokart.gui.lab;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class SteerSignalModuleTest extends TestCase {
  public void testSimple() throws Exception {
    ModuleAuto.INSTANCE.runOne(SteerSignalModule.class);
    ModuleAuto.INSTANCE.endOne(SteerSignalModule.class);
  }

  public void testDirect() throws InterruptedException {
    SteerSignalModule steerSignalModule = new SteerSignalModule();
    steerSignalModule.first();
    for (int index = 0; index <= 10; ++index) {
      Optional<SteerPutEvent> putEvent = steerSignalModule.putEvent();
      SteerPutEvent steerPutEvent = putEvent.get();
      Scalar torque = steerPutEvent.getTorque();
      System.out.println(torque);
      Thread.sleep(100);
    }
    steerSignalModule.last();
  }
}
