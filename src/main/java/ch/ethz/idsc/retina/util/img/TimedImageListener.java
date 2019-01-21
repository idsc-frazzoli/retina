// code by jph
package ch.ethz.idsc.retina.util.img;

/** consumer of {@link TimedImageEvent}
 * 
 * <p>
 * the interface does not specify how exactly the timestamp correlates to the
 * time at which the image was taken. That definition is up to the
 * implementation. */
@FunctionalInterface
public interface TimedImageListener {
  void timedImage(TimedImageEvent timedImageEvent);
}
