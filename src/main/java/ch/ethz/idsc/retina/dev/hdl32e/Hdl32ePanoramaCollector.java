// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

public class Hdl32ePanoramaCollector extends AbstractHdl32eFiringPacketConsumer {
  public static final int[] INDEX = new int[] { //
      31, 15, //
      30, 14, //
      29, 13, //
      28, 12, //
      27, 11, //
      26, 10, //
      25, 9, //
      24, 8, //
      23, 7, //
      22, 6, //
      21, 5, //
      20, 4, //
      19, 3, //
      18, 2, //
      17, 1, //
      16, 0 };
  // ---
  private int rotational_last = -1;
  private final Hdl32ePanoramaListener hdl32ePanoramaListener;
  Hdl32ePanorama hdl32ePanorama = new Hdl32ePanorama();
  // ---
  private final Tensor row_d = Array.zeros(LASERS);
  private final Tensor row_i = Array.zeros(LASERS);

  public Hdl32ePanoramaCollector(Hdl32ePanoramaListener hdl32ePanoramaListener) {
    this.hdl32ePanoramaListener = hdl32ePanoramaListener;
  }

  @Override
  public void process(int firing, int rotational, ByteBuffer byteBuffer) {
    if (rotational < rotational_last) {
      hdl32ePanoramaListener.panorama(hdl32ePanorama);
      hdl32ePanorama = new Hdl32ePanorama();
    }
    rotational_last = rotational;
    hdl32ePanorama.angle.append(RealScalar.of(rotational));
    for (int laser = 0; laser < LASERS; ++laser) {
      int distance = byteBuffer.getShort() & 0xffff;
      int intensity = byteBuffer.get() & 0xff;
      // ---
      row_d.set(RealScalar.of(distance), INDEX[laser]);
      row_i.set(RealScalar.of(intensity), INDEX[laser]);
    }
    hdl32ePanorama.distances.append(row_d);
    hdl32ePanorama.intensity.append(row_i);
  }
}
