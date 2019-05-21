// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class GokartLabjackFrameTest extends TestCase {
  public void testPassive() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 1f, 1f, 0f, 1f, 1f });
    GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(labjackAdcFrame.allADC());
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00");
  }

  public void testResetFalse() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 2.5f, 0f, 0f, 0f, 0f });
    GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(labjackAdcFrame.allADC());
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00");
  }

  public void testResetTrue() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 4.7f, 0f, 0f, 0f, 0f });
    GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(labjackAdcFrame.allADC());
    assertTrue(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00 B");
  }

  public void testReverseFalse() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 2.5f, 0f, 0f, 0f });
    GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(labjackAdcFrame.allADC());
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00");
  }

  public void testReverseTrue() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 4.7f, 0f, 0f, 0f });
    GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(labjackAdcFrame.allADC());
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertTrue(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00 R");
  }

  public void testAutonomousFalse() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 0f, 2.5f, 0f });
    GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(labjackAdcFrame.allADC());
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00");
  }

  public void testAutonomousTrue() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 0f, 10.1f, 0f });
    GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(labjackAdcFrame.allADC());
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertTrue(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00 A");
  }

  public void testThrottleForward() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 5.2f, 0f, 0f });
    GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(labjackAdcFrame.allADC());
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ONE);
    assertEquals(gokartLabjackFrame.toString(), "t=1.00");
  }

  public void testThrottleForward2() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 2.5f, 5.2f, 0f, 0f });
    GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(labjackAdcFrame.allADC());
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ONE);
  }

  public void testThrottleReverse() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 11.5f, 5.2f, 0f, 0f });
    GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(labjackAdcFrame.allADC());
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertTrue(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ONE.negate());
  }
}
