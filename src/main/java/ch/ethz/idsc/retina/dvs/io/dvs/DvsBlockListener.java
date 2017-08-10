// code by jph
package ch.ethz.idsc.retina.dvs.io.dvs;

/** notifies that block of aps columns is completed */
public interface DvsBlockListener {
  void dvsBlockReady(int length);
}
