// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public enum SteerSocket implements StartAndStoppable {
  INSTANCE;
  /** local */
  public static final int LOCAL_PORT = 5002;
  public static final String LOCAL_ADDRESS = "192.168.1.1";
  /** remote */
  public static final int REMOTE_PORT = 5002;
  public static final String REMOTE_ADDRESS = "192.168.1.10";
  // ---
  private final DatagramSocketManager datagramSocketManager = //
      DatagramSocketManager.local(new byte[SteerGetEvent.LENGTH], SteerSocket.LOCAL_PORT, SteerSocket.LOCAL_ADDRESS);
  private final List<SteerGetListener> list = new LinkedList<>();

  public void addListener(SteerGetListener steerGetListener) {
    list.add(steerGetListener);
  }

  @Override
  public void start() {
    datagramSocketManager.start();
  }

  public void send(DatagramPacket datagramPacket) throws IOException {
    datagramSocketManager.send(datagramPacket);
  }

  @Override
  public void stop() {
    datagramSocketManager.stop();
  }
}
