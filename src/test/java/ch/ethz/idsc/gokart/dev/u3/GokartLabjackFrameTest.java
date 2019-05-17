// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GokartLabjackFrameTest extends TestCase {
  public void testPassive() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(Tensors.vectorFloat(1f, 1f, 0f, 1f, 1f));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00");
  }

  public void testResetFalse() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(Tensors.vectorFloat(2.5f, 0f, 0f, 0f, 0f));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00");
  }

  public void testResetTrue() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(Tensors.vectorFloat(4.7f, 0f, 0f, 0f, 0f));
    assertTrue(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00 B");
  }

  public void testReverseFalse() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(Tensors.vectorFloat(0f, 2.5f, 0f, 0f, 0f));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00");
  }

  public void testReverseTrue() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(Tensors.vectorFloat(0f, 4.7f, 0f, 0f, 0f));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertTrue(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00 R");
  }

  public void testAutonomousFalse() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(Tensors.vectorFloat(0f, 0f, 0f, 2.5f, 0f));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00");
  }

  public void testAutonomousTrue() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(Tensors.vectorFloat(0f, 0f, 0f, 11.5f, 0f));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertTrue(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ZERO);
    assertEquals(gokartLabjackFrame.toString(), "t=0.00 A");
  }

  public void testThrottleForward() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(Tensors.vectorFloat(0f, 0f, 5.2f, 0f, 0f));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ONE);
    assertEquals(gokartLabjackFrame.toString(), "t=1.00");
  }

  public void testThrottleForward2() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(Tensors.vectorFloat(0f, 2.5f, 5.2f, 0f, 0f));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertFalse(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ONE);
  }

  public void testThrottleReverse() {
    GokartLabjackFrame gokartLabjackFrame = //
        new GokartLabjackFrame(Tensors.vectorFloat(0f, 11.5f, 5.2f, 0f, 0f));
    assertFalse(gokartLabjackFrame.isResetPressed());
    assertTrue(gokartLabjackFrame.isReversePressed());
    assertFalse(gokartLabjackFrame.isAutonomousPressed());
    assertEquals(gokartLabjackFrame.getAheadAverage(), RealScalar.ONE.negate());
  }
}
