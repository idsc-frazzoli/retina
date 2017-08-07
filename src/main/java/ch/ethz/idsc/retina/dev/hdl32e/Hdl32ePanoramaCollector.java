// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;

public class Hdl32ePanoramaCollector implements FiringPacketInterface {
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
  private final List<Hdl32ePanoramaListener> hdl32ePanoramaListeners = new LinkedList<>();
  private HuePanorama hdl32ePanorama = new HuePanorama();

  public void addListener(Hdl32ePanoramaListener hdl32ePanoramaListener) {
    hdl32ePanoramaListeners.add(hdl32ePanoramaListener);
  }

  @Override
  public void process(int firing, int rotational, ByteBuffer byteBuffer) {
    if (rotational < rotational_last) {
      hdl32ePanoramaListeners.forEach(listener -> listener.panorama(hdl32ePanorama));
      hdl32ePanorama = new HuePanorama();
    }
    rotational_last = rotational;
    final int x = hdl32ePanorama.angle.length();
    hdl32ePanorama.angle.append(RealScalar.of(rotational));
    if (x < Hdl32ePanorama.MAX_WIDTH) {
      for (int laser = 0; laser < LASERS; ++laser) {
        // in the outdoors the values for distance typically range from [0, ..., ~52592]
        // 2 mm increments, i.e.
        // distance == 500 corresponds to 1[m]
        // distance == 50000 corresponds to 100[m]
        // distance == 0 -> no return within 100[m]
        // distance == 256 corresponds to 0.512[m]
        int distance = byteBuffer.getShort() & 0xffff;
        byte intensity = byteBuffer.get(); // 255 == most intensive return
        // ---
        final int y = INDEX[laser];
        hdl32ePanorama.setReading(x, y, distance, intensity);
      }
    } else {
      System.err.println("width <= " + x);
    }
  }

  @Override
  public void status(int usec, byte type, byte value) {
  }
}
