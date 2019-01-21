// code by jph
package ch.ethz.idsc.retina.davis.app;

@FunctionalInterface
public interface DavisTallyListener {
  void tallyEvent(DavisTallyEvent davisTallyEvent);
}
