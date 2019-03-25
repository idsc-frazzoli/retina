// code by jph
package ch.ethz.idsc.retina.util.data;

public interface DataEventInterface extends BufferInsertable, OfflineVectorInterface {
  /** @return */
  byte[] asArray();
}
