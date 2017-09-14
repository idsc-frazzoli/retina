// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.UniversalDatagramClient;

/** client listens at UDP socket to receive 16-byte linmot messages */
public class RimoGetDatagramClient implements ByteArrayConsumer, StartAndStoppable {
  public static final int LENGTH = 2 * 14;
  // ---
  private final byte[] bytes = new byte[LENGTH];
  private final UniversalDatagramClient universalDatagramClient;
  private final List<RimoGetListener> listeners = new LinkedList<>();

  public RimoGetDatagramClient(String group, int port) {
    universalDatagramClient = UniversalDatagramClient.create(bytes, group, port);
    universalDatagramClient.addListener(this);
  }

  public void addListener(RimoGetListener listener) {
    listeners.add(listener);
  }

  @Override
  public void accept(byte[] data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    RimoGetEvent rimoGetL = new RimoGetEvent(byteBuffer);
    RimoGetEvent rimoGetR = new RimoGetEvent(byteBuffer);
    listeners.forEach(listener -> listener.rimoGet(rimoGetL, rimoGetR));
  }

  @Override
  public void start() {
    universalDatagramClient.start();
    System.out.println("socket start");
  }

  @Override
  public void stop() {
    universalDatagramClient.stop();
  }
}
