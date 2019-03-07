// code by jph
package ch.ethz.idsc.gokart.core;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import junit.framework.TestCase;

public class AutoboxSocketTest extends TestCase {
  public void testLinmot() {
    AutoboxSocket<LinmotGetEvent, LinmotPutEvent> instance = LinmotSocket.INSTANCE;
    instance.byteArrayConsumer.accept(new byte[16]);
  }

  public void testSteer() {
    AutoboxSocket<SteerGetEvent, SteerPutEvent> instance = SteerSocket.INSTANCE;
    instance.byteArrayConsumer.accept(new byte[44]);
  }
}
