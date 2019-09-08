// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import com.fazecast.jSerialComm.SerialPort;

import ch.ethz.idsc.retina.util.tty.SerialPortWrap;
import ch.ethz.idsc.retina.util.tty.SerialPorts;

/** based on the document
 * Inertial Measurement Unit VMU931
 * User Guide
 * Version 1.3, March 2018, Variense inc.
 * https://polybox.ethz.ch/index.php/s/J6kOEoy7D4zBCbf */
// TODO JPH prevent reissuing calibration or self-test while calibration or self-test is going on
public class Vmu931 implements Runnable {
  private static final long SLEEP_MS_SHORT = 1;
  private static final long SLEEP_MS_LONG = 3;
  private static final byte MESSAGE_DATA_BEG = 1;
  private static final byte MESSAGE_DATA_END = 4;
  private static final byte MESSAGE_TEXT_BEG = 2;
  private static final byte MESSAGE_TEXT_END = 3;
  private static final int SIZE_MIN = 4;
  /***************************************************/
  private final Set<Vmu931Channel> set = EnumSet.noneOf(Vmu931Channel.class);
  private final Set<Vmu931Reply> replies = EnumSet.noneOf(Vmu931Reply.class);
  private final byte[] data = new byte[256];
  // ---
  private final String port;
  private final Vmu931_DPS dps;
  private final Vmu931_G resolution_g;
  private final Vmu931Listener vmu931Listener;
  private final Thread thread = new Thread(this);
  private SerialPortWrap serialPortWrap;
  private volatile boolean isLaunched = true;
  private boolean isConfigured = false;

  /** @param port
   * @param set
   * @param vmu931_DPS
   * @param vmu931_G
   * @param vmu931Listener */
  public Vmu931( //
      String port, //
      Set<Vmu931Channel> set, //
      Vmu931_DPS vmu931_DPS, //
      Vmu931_G vmu931_G, //
      Vmu931Listener vmu931Listener) {
    this.port = port;
    this.set.addAll(set);
    this.dps = vmu931_DPS;
    this.resolution_g = vmu931_G;
    this.vmu931Listener = Objects.requireNonNull(vmu931Listener);
  }

  public void open() {
    SerialPort serialPort = SerialPorts.create(port);
    serialPortWrap = new SerialPortWrap(serialPort);
    thread.start();
    // ---
    requestStatus();
    System.out.println("vmu931 request status");
  }

  /** TODO JPH what is printout when starting?
   * writes "Test passed. Your device works fine." */
  public void requestSelftest() {
    replies.remove(Vmu931Reply.SELFTEST);
    serialPortWrap.write(Vmu931Statics.requestSelftest());
  }

  /** triggers calibration which blocks measurement readout,
   * writes "Calibration started.", and terminates then
   * writes "Calibration completed." */
  public void requestCalibration() {
    replies.remove(Vmu931Reply.CALIBRATION);
    serialPortWrap.write(Vmu931Statics.requestCalibration());
  }

  public void requestStatus() {
    int len = serialPortWrap.write(Vmu931Statics.requestStatus());
    System.out.println("requestStatus: " + len);
  }

  @Override // from Runnable
  public void run() {
    try {
      while (isLaunched) {
        if (serialPortWrap.peek(data, 1)) {
          switch (data[0]) {
          case MESSAGE_DATA_BEG:
            if (serialPortWrap.peek(data, SIZE_MIN)) {
              final int size = Math.max(SIZE_MIN, data[1] & 0xff);
              if (serialPortWrap.peek(data, size)) {
                int term = data[size - 1];
                if (term == MESSAGE_DATA_END) {
                  serialPortWrap.advance(size);
                  handle_data(data);
                } else
                  serialPortWrap.advance(1);
              } else {
                // System.out.println("wait data");
                Thread.sleep(SLEEP_MS_SHORT);
              }
            }
            break;
          case MESSAGE_TEXT_BEG:
            if (serialPortWrap.peek(data, SIZE_MIN)) {
              final int size = Math.max(SIZE_MIN, data[1] & 0xff);
              if (serialPortWrap.peek(data, size)) {
                int term = data[size - 1];
                if (term == MESSAGE_TEXT_END) {
                  // string is trimmed because the reply usually terminates with two newline chars
                  final String string = new String(data, 3, size - 4).trim();
                  // System.out.println("vmu931:[" + string + "]");
                  Vmu931Reply.match(string, replies::add);
                  serialPortWrap.advance(size);
                } else
                  serialPortWrap.advance(1);
              } else {
                // System.out.println("wait text");
                Thread.sleep(SLEEP_MS_SHORT);
              }
            }
            break;
          default:
            System.err.println("vmu931 discard head " + data[0]);
            serialPortWrap.advance(1);
            break;
          }
        } else {
          // System.out.println("wait rx");
          Thread.sleep(SLEEP_MS_LONG);
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println("VMU931 readout terminated exception");
    }
    System.out.println("VMU931 readout terminated");
  }

  public void handle_data(byte[] data) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data); // big endian
    byteBuffer.position(3);
    // System.out.println("DATA=" + data[2]);
    switch (data[2]) {
    case Vmu931Statics.ID_ACCELEROMETER:
      vmu931Listener.accelerometer(byteBuffer);
      break;
    case Vmu931Statics.ID_GYROSCOPE:
      vmu931Listener.gyroscope(byteBuffer);
      break;
    case Vmu931Statics.ID_MAGNETOMETER:
    case Vmu931Statics.ID_EULER_ANGLES: {
      // int timestamp_ms = byteBuffer.getInt();
      // float x = byteBuffer.getFloat();
      // float y = byteBuffer.getFloat();
      // float z = byteBuffer.getFloat();
      // System.out.println(type + " " + timestamp_ms + " " + x + " " + y);
      break;
    }
    case Vmu931Statics.ID_QUATERNION: {
      // int timestamp_ms = byteBuffer.getInt();
      // float w = byteBuffer.getFloat();
      // float x = byteBuffer.getFloat();
      // float y = byteBuffer.getFloat();
      // float z = byteBuffer.getFloat();
      // System.out.println(type + " " + timestamp_ms + " " + x + " " + y);
      break;
    }
    case Vmu931Statics.ID_HEADING: {
      // int timestamp_ms = byteBuffer.getInt();
      // float heading = byteBuffer.getFloat(); // in [deg]
      // System.out.println(type + " " + timestamp_ms + " " + heading);
      break;
    }
    case Vmu931Statics.ID_STATUS: {
      byte status = byteBuffer.get(); // should equal 7 (3 lowest bits set)
      byte resolution = byteBuffer.get();
      byte rate = byteBuffer.get();
      int current = byteBuffer.getInt();
      callbackStatus(status, resolution, rate == 1, current);
      break;
    }
    default:
      break;
    }
  }

  private void callbackStatus(byte status, byte resolution, boolean lowRate, int current) {
    boolean isDirty = false;
    // System.out.println("STATUS= " + status + " " + resolution + " low=" + lowRate + " " + current);
    // for (Vmu931_DPS vmu931_DPS : Vmu931_DPS.values())
    // System.out.println(vmu931_DPS.name() + " " + vmu931_DPS.isActive(resolution));
    if (!dps.isActive(resolution)) {
      System.out.println("vmu931 config dps=" + dps);
      serialPortWrap.write(dps.setActive());
      isDirty = true;
    }
    // ---
    // for (Vmu931_G vmu931_G : Vmu931_G.values())
    // System.out.println(vmu931_G.name() + " " + vmu931_G.isActive(resolution));
    if (!resolution_g.isActive(resolution)) {
      System.out.println("vmu931 config res=" + resolution + "->g=" + resolution_g);
      serialPortWrap.write(resolution_g.setActive());
      isDirty = true;
    }
    // ---
    for (Vmu931Channel vmu931Channel : Vmu931Channel.values()) {
      boolean isActive = vmu931Channel.isActive(current);
      boolean rqActive = set.contains(vmu931Channel);
      if (isActive ^ rqActive) {
        System.out.println("vmu931 config " + vmu931Channel.name() + ": " + isActive + "->" + rqActive);
        serialPortWrap.write(vmu931Channel.toggle());
        isDirty = true;
      }
    }
    if (isDirty) {
      System.out.println("vmu931 request status");
      requestStatus();
    } else
      isConfigured = true;
  }

  public boolean isConfigured() {
    return isConfigured;
  }

  public void close() {
    System.out.println();
    isLaunched = false;
    thread.interrupt();
    if (Objects.nonNull(serialPortWrap))
      serialPortWrap.close();
  }

  public boolean isCalibrated() {
    return replies.contains(Vmu931Reply.CALIBRATION);
  }
}
