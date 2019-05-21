// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GokartLabjackLcmClientTest extends TestCase {
  public void testSimple() throws Exception {
    GokartLabjackLcmClient gokartLabjackLcmClient = //
        new GokartLabjackLcmClient(GokartLcmChannel.LABJACK_U3_ADC, Quantity.of(0.2, SI.SECOND));
    gokartLabjackLcmClient.start();
    assertFalse(gokartLabjackLcmClient.getManualControl().isPresent());
    LabjackU3Publisher.accept(new LabjackAdcFrame(new float[] { 5f, 5f, 5f, 10f, 5f }));
    Thread.sleep(30);
    {
      Optional<ManualControlInterface> optional = gokartLabjackLcmClient.getManualControl();
      assertTrue(optional.isPresent());
      ManualControlInterface manualControlInterface = optional.get();
      assertFalse(manualControlInterface.isAutonomousPressed());
      assertFalse(manualControlInterface.isResetPressed());
    }
    LabjackU3Publisher.accept(new LabjackAdcFrame(new float[] { 5f, 5f, 5f, 10f, 5f }));
    Thread.sleep(30);
    {
      Optional<ManualControlInterface> optional = gokartLabjackLcmClient.getManualControl();
      assertTrue(optional.isPresent());
      ManualControlInterface manualControlInterface = optional.get();
      assertTrue(manualControlInterface.isAutonomousPressed());
      assertTrue(manualControlInterface.isResetPressed());
    }
    gokartLabjackLcmClient.stop();
  }

  public void testSimple2() throws Exception {
    GokartLabjackLcmClient gokartLabjackLcmClient = //
        new GokartLabjackLcmClient(GokartLcmChannel.LABJACK_U3_ADC, Quantity.of(0.2, SI.SECOND));
    gokartLabjackLcmClient.start();
    assertFalse(gokartLabjackLcmClient.getManualControl().isPresent());
    LabjackU3Publisher.accept(new LabjackAdcFrame(new float[5]));
    Thread.sleep(30);
    {
      Optional<ManualControlInterface> optional = gokartLabjackLcmClient.getManualControl();
      assertTrue(optional.isPresent());
      ManualControlInterface manualControlInterface = optional.get();
      assertFalse(manualControlInterface.isAutonomousPressed());
      assertFalse(manualControlInterface.isResetPressed());
      Scalar scalar = manualControlInterface.getAheadAverage();
      assertEquals(scalar, RealScalar.ZERO);
    }
    LabjackU3Publisher.accept(new LabjackAdcFrame(new float[] { 5f, 5f, 5.1f, 10f, 5f }));
    Thread.sleep(30);
    {
      Optional<ManualControlInterface> optional = gokartLabjackLcmClient.getManualControl();
      assertTrue(optional.isPresent());
      ManualControlInterface manualControlInterface = optional.get();
      assertFalse(manualControlInterface.isAutonomousPressed());
      assertFalse(manualControlInterface.isResetPressed());
      Scalar scalar = manualControlInterface.getAheadAverage();
      Chop._01.requireClose(scalar, RealScalar.ONE);
    }
    LabjackU3Publisher.accept(new LabjackAdcFrame(new float[5]));
    Thread.sleep(30);
    {
      Optional<ManualControlInterface> optional = gokartLabjackLcmClient.getManualControl();
      assertTrue(optional.isPresent());
      ManualControlInterface manualControlInterface = optional.get();
      assertFalse(manualControlInterface.isAutonomousPressed());
      assertFalse(manualControlInterface.isResetPressed());
    }
    gokartLabjackLcmClient.stop();
  }
}
