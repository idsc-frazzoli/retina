// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GokartStateTest extends TestCase {
  public void testLength() {
    GokartState gokartState = new GokartState(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    assertEquals(gokartState.length(), 44);
  }

  public void testSerializationNoBrake() {
    GokartState gokartState1 = new GokartState(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    assertEquals(gokartState1.asVector().length(), 11);
    byte[] array = new byte[gokartState1.length()];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    gokartState1.insert(byteBuffer);
    byteBuffer.flip();
    GokartState gokartState2 = new GokartState(byteBuffer);
    assertEquals(gokartState1.asVector(), gokartState2.asVector());
    assertEquals(gokartState1.asVectorWithUnits(), gokartState2.asVectorWithUnits());
    assertEquals(gokartState1.asVector().length(), gokartState2.asVectorWithUnits().length());
  }

  public void testSerializationWithBrake() {
    GokartState gokartState1 = new GokartState(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
    assertEquals(gokartState1.asVector().length(), 11);
    byte[] array = new byte[gokartState1.length()];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    gokartState1.insert(byteBuffer);
    byteBuffer.flip();
    GokartState gokartState2 = new GokartState(byteBuffer);
    assertEquals(gokartState1.asVector(), gokartState2.asVector());
    assertEquals(gokartState1.asVectorWithUnits(), gokartState2.asVectorWithUnits());
    assertEquals(gokartState1.asVector().length(), gokartState2.asVectorWithUnits().length());
  }

  public void testSome() {
    GokartState gokartState1 = new GokartState(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    GokartState gokartState2 = new GokartState(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0);
    assertEquals(gokartState1.asVector(), gokartState2.asVector());
    assertEquals(gokartState1.asVectorWithUnits(), gokartState2.asVectorWithUnits());
    assertEquals(gokartState1.asVector().length(), gokartState2.asVectorWithUnits().length());
  }

  public void testQuantityWithBrake() {
    GokartState gokartState1 = new GokartState( //
        Quantity.of(1, SI.SECOND), //
        Quantity.of(2, SI.VELOCITY), //
        Quantity.of(3, SI.VELOCITY), //
        Quantity.of(4, SI.PER_SECOND), //
        Quantity.of(5, SI.METER), //
        Quantity.of(6, SI.METER), //
        Quantity.of(7, SI.ONE), //
        Quantity.of(8, SI.PER_SECOND), //
        Quantity.of(9, SI.PER_SECOND), //
        Quantity.of(10, "SCE"), //
        Quantity.of(11, NonSI.DEGREE_CELSIUS));
    GokartState gokartState2 = new GokartState(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
    assertEquals(gokartState1.asVector(), gokartState2.asVector());
    assertEquals(gokartState1.asVectorWithUnits(), gokartState2.asVectorWithUnits());
    assertEquals(gokartState1.asVector().length(), gokartState2.asVectorWithUnits().length());
  }

  public void testQuantityNoBrake() {
    GokartState gokartState1 = new GokartState( //
        Quantity.of(1, SI.SECOND), //
        Quantity.of(2, SI.VELOCITY), //
        Quantity.of(3, SI.VELOCITY), //
        Quantity.of(4, SI.PER_SECOND), //
        Quantity.of(5, SI.METER), //
        Quantity.of(6, SI.METER), //
        Quantity.of(7, SI.ONE), //
        Quantity.of(8, SI.PER_SECOND), //
        Quantity.of(9, SI.PER_SECOND), //
        Quantity.of(10, "SCE"));
    GokartState gokartState2 = new GokartState(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    assertEquals(gokartState1.asVector(), gokartState2.asVector());
    assertEquals(gokartState1.asVectorWithUnits(), gokartState2.asVectorWithUnits());
    assertEquals(gokartState1.asVector().length(), gokartState2.asVectorWithUnits().length());
  }
}
