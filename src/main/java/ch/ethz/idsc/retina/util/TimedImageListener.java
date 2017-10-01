// code by jph
package ch.ethz.idsc.retina.util;

/** provides an image with a single timestamp.
 * 
 * <p>
 * the interface does not specify how exactly the timestamp correlates to the
 * time at which the image was taken. That definition is up to the
 * implementation. */
public interface TimedImageListener {
  void timedImage(TimedImageEvent timedImageEvent);
}
