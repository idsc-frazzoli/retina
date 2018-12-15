// code by jph
package ch.ethz.idsc.gokart.dev;

import ch.ethz.idsc.retina.dev.u3.LabjackAdcFrame;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class GokartLabjackFrameTest extends TestCase {
  public void testPassive() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(new LabjackAdcFrame(new float[] { 1f, 1f, 0f, 1f, 1f }));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
  }

  public void testResetTrue() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(new LabjackAdcFrame(new float[] { 2.5f, 0f, 0f, 0f, 0f }));
    assertTrue(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
  }

  public void testReverseTrue() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(new LabjackAdcFrame(new float[] { 0f, 2.5f, 0f, 0f, 0f }));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertTrue(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
  }

  public void testAutonomousTrue() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(new LabjackAdcFrame(new float[] { 0f, 0f, 0f, 2.5f, 0f }));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertTrue(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
  }

  public void testThrottleForward() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(new LabjackAdcFrame(new float[] { 0f, 0f, 5.2f, 0f, 0f }));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ONE);
  }

  public void testThrottleReverse() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(new LabjackAdcFrame(new float[] { 0f, 2.5f, 5.2f, 0f, 0f }));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertTrue(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ONE.negate());
  }
}
