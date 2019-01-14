// code by jph
package ch.ethz.idsc.retina.util;

import java.nio.FloatBuffer;

import junit.framework.TestCase;

public class FloatBufferTest extends TestCase {
  public void testSimple() {
    float[] array = new float[10];
    FloatBuffer floatBuffer = FloatBuffer.wrap(array);
    assertEquals(floatBuffer.limit(), 10);
    float enter = 3.14f;
    floatBuffer.put(enter);
    assertEquals(floatBuffer.limit(), 10);
    assertEquals(floatBuffer.position(), 1);
    assertEquals(floatBuffer.remaining(), 9);
    float[] ref = floatBuffer.array();
    assertEquals(ref, array);
    floatBuffer.position(9);
    floatBuffer.position(0);
    float value = floatBuffer.get();
    assertEquals(enter, value);
    assertEquals(floatBuffer.capacity(), 10);
  }

  public void testFlip() {
    float[] array = new float[10];
    FloatBuffer floatBuffer = FloatBuffer.wrap(array);
    assertEquals(floatBuffer.limit(), 10);
    float enter = 3.14f;
    floatBuffer.put(enter);
    floatBuffer.flip();
    assertEquals(floatBuffer.limit(), 1);
    assertEquals(floatBuffer.position(), 0);
    floatBuffer.limit(10);
    assertEquals(floatBuffer.position(), 0);
    floatBuffer.put(1.3f);
    floatBuffer.put(3.3f);
    floatBuffer.flip();
  }
}
