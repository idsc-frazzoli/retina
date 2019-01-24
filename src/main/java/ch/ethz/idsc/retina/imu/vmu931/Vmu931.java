// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Set;

import com.fazecast.jSerialComm.SerialPort;

import ch.ethz.idsc.retina.util.tty.SerialPortWrap;
import ch.ethz.idsc.retina.util.tty.SerialPorts;

/** based on the document
 * Inertial Measurement Unit VMU931
 * User Guide
 * Version 1.3, March 2018, Variense inc. */
public class Vmu931 implements Runnable {
  private static final int MESSAGE_DATA_BEG = 1;
  private static final int MESSAGE_DATA_END = 4;
  private static final int MESSAGE_TEXT_BEG = 2;
  private static final int MESSAGE_TEXT_END = 3;
  private static final int SIZE_MIN = 4;
  private static final String SELFTEST_PASSED = "Test passed. Your device works fine.";
  /***************************************************/
  private final Set<Vmu931Channel> set = EnumSet.noneOf(Vmu931Channel.class);
  private final byte[] data = new byte[256];
  // ---
  private final Vmu931_DPS dps;
  private final Vmu931_G resolution_g;
  private final SerialPortWrap serialPortWrap;
  private final Thread thread;

  /** @param serialPort open
   * @param set
   * @param vmu931_DPS */
  public Vmu931(String port, Set<Vmu931Channel> set, Vmu931_DPS vmu931_DPS, Vmu931_G vmu931_G) {
    this.set.addAll(set);
    this.dps = vmu931_DPS;
    this.resolution_g = vmu931_G;
    SerialPort serialPort = SerialPorts.create(port);
    serialPortWrap = new SerialPortWrap(serialPort);
    // serialPortWrap.write(Vmu931Channel.ACCELEROMETER.toggle());
    // serialPortWrap.write(Vmu931Channel.HEADING.toggle());
    serialPortWrap.write(Vmu931Statics.requestStatus());
    thread = new Thread(this);
    thread.start();
  }

  public void handle_data(byte[] data) {
    char type = (char) (data[2] & 0xff);
    ByteBuffer byteBuffer = ByteBuffer.wrap(data); // big endian
    byteBuffer.position(3);
    switch (type) {
    case Vmu931Statics.ID_ACCELEROMETER:
    case Vmu931Statics.ID_GYROSCOPE:
    case Vmu931Statics.ID_MAGNETOMETER:
    case Vmu931Statics.ID_EULER_ANGLES: {
      int timestamp_ms = byteBuffer.getInt();
      float x = byteBuffer.getFloat();
      float y = byteBuffer.getFloat();
      float z = byteBuffer.getFloat();
      // System.out.println(type + " " + timestamp_ms + " " + x + " " + y);
      break;
    }
    case Vmu931Statics.ID_QUATERNION: {
      int timestamp_ms = byteBuffer.getInt();
      float w = byteBuffer.getFloat();
      float x = byteBuffer.getFloat();
      float y = byteBuffer.getFloat();
      float z = byteBuffer.getFloat();
      // System.out.println(type + " " + timestamp_ms + " " + x + " " + y);
      break;
    }
    case Vmu931Statics.ID_HEADING: {
      int timestamp_ms = byteBuffer.getInt();
      float heading = byteBuffer.getFloat(); // in [deg]
      // System.out.println(type + " " + timestamp_ms + " " + heading);
      break;
    }
    case Vmu931Statics.ID_STATUS: {
      byte status = byteBuffer.get(); // should equal 7 (3 lowest bits set)
      byte resolution = byteBuffer.get();
      byte rate = byteBuffer.get();
      int current = byteBuffer.getInt();
      statusCallback(status, resolution, rate == 1, current & 0xff);
      break;
    }
    default:
      break;
    }
  }

  private void statusCallback(byte status, byte resolution, boolean lowRate, int current) {
    boolean isDirty = false;
    // System.out.println("STATUS= " + status + " " + resolution + " low=" + lowRate + " " + current);
    // for (Vmu931_DPS vmu931_DPS : Vmu931_DPS.values())
    // System.out.println(vmu931_DPS.name() + " " + vmu931_DPS.isActive(resolution));
    if (!dps.isActive(resolution)) {
      System.out.println("vmu931 config dps=" + dps);
      serialPortWrap.write(dps.set());
      isDirty = true;
    }
    // ---
    // for (Vmu931_G vmu931_G : Vmu931_G.values())
    // System.out.println(vmu931_G.name() + " " + vmu931_G.isActive(resolution));
    if (!resolution_g.isActive(resolution)) {
      System.out.println("vmu931 config g=" + resolution_g);
      serialPortWrap.write(resolution_g.set());
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
      serialPortWrap.write(Vmu931Statics.requestStatus());
    } else {
      System.out.println("vmu931 configured");
      // serialPortWrap.write(Vmu931Statics.requestSelftest());
    }
  }

  @Override
  public void run() {
    try {
      while (true) {
        if (serialPortWrap.peek(data, 1)) {
          int head = data[0] & 0xff;
          switch (head) {
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
              } else
                Thread.sleep(1);
            }
            break;
          case MESSAGE_TEXT_BEG:
            if (serialPortWrap.peek(data, SIZE_MIN)) {
              final int size = Math.max(SIZE_MIN, data[1] & 0xff);
              if (serialPortWrap.peek(data, size)) {
                int term = data[size - 1];
                if (term == MESSAGE_TEXT_END) {
                  String string = new String(data, 3, size - 4); //
                  // Self-test started.
                  // Test passed. Your device works fine.
                  System.out.println("vmu931:[" + string + "]");
                  serialPortWrap.advance(size);
                } else
                  serialPortWrap.advance(1);
              } else
                Thread.sleep(1);
            }
            break;
          default:
            serialPortWrap.advance(1);
            break;
          }
        } else
          Thread.sleep(1);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println("VMU931 readout terminated");
    }
  }

  // ---
  public static void main(String[] args) {
    new Vmu931( //
        "/dev/ttyACM0", //
        EnumSet.of(Vmu931Channel.ACCELEROMETER, Vmu931Channel.GYROSCOPE), //
        Vmu931_DPS._500, //
        Vmu931_G._4);
  }
}
