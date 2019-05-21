// code by jph, mh
package ch.ethz.idsc.gokart.core.fuse;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.u3.LabjackU3Publisher;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class AutonomousSafetyModuleTest extends TestCase {
  public void testAuto() throws Exception {
    final int providerSize = RimoSocket.INSTANCE.getPutProviderSize();
    ModuleAuto.INSTANCE.runOne(AutonomousSafetyModule.class);
    assertEquals(providerSize + 1, RimoSocket.INSTANCE.getPutProviderSize());
    ModuleAuto.INSTANCE.endOne(AutonomousSafetyModule.class);
    assertEquals(providerSize, RimoSocket.INSTANCE.getPutProviderSize());
  }

  public void testSimple() throws InterruptedException {
    AutonomousSafetyModule autonomousSafetyModule = new AutonomousSafetyModule();
    final int providerSize = RimoSocket.INSTANCE.getPutProviderSize();
    autonomousSafetyModule.first();
    assertEquals(providerSize + 1, RimoSocket.INSTANCE.getPutProviderSize());
    {
      Optional<RimoPutEvent> putEvent = autonomousSafetyModule.rimoPutProvider.putEvent();
      assertTrue(putEvent.isPresent());
    }
    autonomousSafetyModule.gokartPoseListener.getEvent(GokartPoseEvents.create(Tensors.fromString("{2[m], 3[m], 4}"), RealScalar.ONE));
    {
      Optional<RimoPutEvent> putEvent = autonomousSafetyModule.rimoPutProvider.putEvent();
      assertTrue(putEvent.isPresent());
    }
    {
      ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
      byteBuffer.putShort(12, (short) 320);
      byteBuffer.putShort(14, (short) 320);
      byteBuffer.rewind();
      autonomousSafetyModule.linmotGetListener.getEvent(new LinmotGetEvent(byteBuffer));
    }
    {
      Optional<RimoPutEvent> putEvent = autonomousSafetyModule.rimoPutProvider.putEvent();
      assertTrue(putEvent.isPresent());
    }
    {
      LabjackU3Publisher.accept(new LabjackAdcFrame(new float[] { 5f, 5f, 5f, 8f, 5f }));
      Thread.sleep(10);
      LabjackU3Publisher.accept(new LabjackAdcFrame(new float[] { 5f, 5f, 5f, 8f, 5f }));
      Thread.sleep(10);
      LabjackU3Publisher.accept(new LabjackAdcFrame(new float[] { 5f, 5f, 5f, 8f, 5f }));
      Thread.sleep(10);
    }
    {
      Optional<RimoPutEvent> putEvent = autonomousSafetyModule.rimoPutProvider.putEvent();
      assertFalse(putEvent.isPresent());
    }
    autonomousSafetyModule.last();
    assertEquals(providerSize, RimoSocket.INSTANCE.getPutProviderSize());
  }
}
