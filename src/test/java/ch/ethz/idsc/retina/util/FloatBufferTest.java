// code by jph
package ch.ethz.idsc.retina.util;

import java.nio.FloatBuffer;

import junit.framework.TestCase;

public class FloatBufferTest extends TestCase {
  public void testSimple() {
    float[] array = new float[10];
    FloatBuffer fb = FloatBuffer.wrap(array);
    assertEquals(fb.limit(), 10);
    float enter = 3.14f;
    fb.put(enter);
    assertEquals(fb.limit(), 10);
    assertEquals(fb.position(), 1);
    assertEquals(fb.remaining(), 9);
    float[] ref = fb.array();
    assertEquals(ref, array);
    fb.position(9);
    fb.position(0);
    float value = fb.get();
    assertEquals(enter, value);
    assertEquals(fb.capacity(), 10);
  }

  public void testFlip() {
    float[] array = new float[10];
    FloatBuffer fb = FloatBuffer.wrap(array);
    assertEquals(fb.limit(), 10);
    float enter = 3.14f;
    fb.put(enter);
    fb.flip();
    assertEquals(fb.limit(), 1);
    assertEquals(fb.position(), 0);
    fb.limit(10);
    assertEquals(fb.position(), 0);
    fb.put(1.3f);
    fb.put(3.3f);
    fb.flip();
  }
}
