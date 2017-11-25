// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

public interface PutListener<PE> {
  /** @param putEvent sent to micro-autobox */
  void putEvent(PE putEvent);
}
