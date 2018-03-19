// code by jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.Aedat31PolarityListener;

public interface Aedat31Decoder {
  void read(ByteBuffer byteBuffer); // LITTLE_ENDIAN

  void addPolarityListener(Aedat31PolarityListener listener);
}
