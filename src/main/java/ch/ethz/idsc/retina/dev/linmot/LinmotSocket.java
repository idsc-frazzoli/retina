// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocket;
import ch.ethz.idsc.retina.util.HexStrings;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

/**  */
public class LinmotSocket extends AutoboxSocket<LinmotGetListener, LinmotPutEvent, LinmotPutProvider> {
  public static final LinmotSocket INSTANCE = new LinmotSocket();
  // ---
  private static final int LOCAL_PORT = 5001;
  private static final String LOCAL_ADDRESS = "192.168.1.1";
  // ---
  private static final int REMOTE_PORT = 5001;
  private static final String REMOTE_ADDRESS = "192.168.1.10";
  // ---
  private static final int SEND_PERIOD_MS = 20;
  // ---

  private LinmotSocket() {
    super(DatagramSocketManager.local(new byte[LinmotGetEvent.LENGTH], LinmotSocket.LOCAL_PORT, LinmotSocket.LOCAL_ADDRESS));
  }

  @Override
  public void accept(byte[] data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    try {
      LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
      listeners.forEach(listener -> listener.linmotGet(linmotGetEvent));
    } catch (Exception exception) {
      System.out.println("fail decode, received =" + length + " : " + HexStrings.from(data));
      System.err.println(exception.getMessage());
    }
  }

  @Override
  protected long getPeriod() {
    return SEND_PERIOD_MS;
  }

  @Override
  protected DatagramPacket getDatagramPacket(LinmotPutEvent linmotPutEvent) {
    byte[] data = new byte[LinmotPutEvent.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    linmotPutEvent.insert(byteBuffer);
    try {
      return new DatagramPacket(data, data.length, //
          InetAddress.getByName(LinmotSocket.REMOTE_ADDRESS), LinmotSocket.REMOTE_PORT);
    } catch (Exception exception) {
      // ---
      System.out.println("LINMOT SEND FAIL");
      exception.printStackTrace();
      System.exit(0); // TODO
    }
    return null;
  }
}
