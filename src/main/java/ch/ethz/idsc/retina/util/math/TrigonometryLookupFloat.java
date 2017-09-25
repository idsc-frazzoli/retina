// code by jph
package ch.ethz.idsc.retina.util.math;

import java.nio.FloatBuffer;

public class TrigonometryLookupFloat {
  private final float[] array;

  /** @param length of complete revolution
   * @param flip */
  public TrigonometryLookupFloat(int length, boolean flip) {
    array = new float[2 * length];
    FloatBuffer floatBuffer = FloatBuffer.wrap(array);
    for (int index = 0; index < length; ++index) {
      double angle = index * Math.PI / length * 2;
      floatBuffer.put((float) Math.cos(angle));
      float sini = (float) Math.sin(angle);
      floatBuffer.put(flip ? -sini : sini);
    }
  }

  public float dx(int rotational) {
    rotational <<= 1;
    return array[rotational];
  }

  public float dy(int rotational) {
    rotational <<= 1;
    return array[rotational + 1];
  }
}
