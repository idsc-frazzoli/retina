// code by jph
package ch.ethz.idsc.retina.util.math;

import java.nio.FloatBuffer;

import ch.ethz.idsc.tensor.lie.AngleVector;

/** rapid lookup of cos/sin values
 * instead of calls to {@link Math#cos(double)} etc.
 * 
 * @see AngleVector */
public class AngleVectorLookupFloat {
  private final float[] array;

  /** @param length of complete revolution, resolution
   * @param flip true for clockwise, false for ccw
   * @param angle_offset */
  public AngleVectorLookupFloat(int length, boolean flip, double angle_offset) {
    array = new float[2 * length];
    FloatBuffer floatBuffer = FloatBuffer.wrap(array);
    for (int index = 0; index < length; ++index) {
      double angle = index * 2 * Math.PI / length;
      angle = flip ? -angle + angle_offset : angle + angle_offset;
      floatBuffer.put((float) Math.cos(angle));
      floatBuffer.put((float) Math.sin(angle));
    }
  }

  /** @param rotational
   * @return Math.cos at given rotational index */
  public float dx(int rotational) {
    rotational <<= 1;
    return array[rotational];
  }

  /** @param rotational
   * @return Math.sin at given rotational index */
  public float dy(int rotational) {
    rotational <<= 1;
    return array[rotational + 1];
  }
}
