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
  /***************************************************/
  private final Set<Vmu931Channel> set = EnumSet.noneOf(Vmu931Channel.class);
  private final byte[] data = new byte[256];
  // private final
  private final SerialPortWrap serialPortWrap;
  private final Thread thread;

  /** @param serialPort open */
  public Vmu931(String port, Set<Vmu931Channel> set) {
    this.set.addAll(set);
    SerialPort serialPort = SerialPorts.create(port);
    serialPortWrap = new SerialPortWrap(serialPort);
    serialPortWrap.write(Vmu931Channel.ACCELEROMETER.toggle());
    serialPortWrap.write(Vmu931Channel.HEADING.toggle());
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
      statusCallback(status & 0xff, resolution & 0xff, rate == 1, current & 0xff);
      break;
    }
    default:
      break;
    }
  }

  private void statusCallback(int status, int resolution, boolean lowRate, int current) {
    System.out.println("STATUS= " + status + " " + resolution + " low=" + lowRate + " " + current);
    boolean isDirty = false;
    for (Vmu931Channel vmu931Channel : Vmu931Channel.values()) {
      boolean isActive = vmu931Channel.isActive(current);
      System.out.println(vmu931Channel.name() + "=" + isActive);
      if (isActive ^ set.contains(vmu931Channel)) {
        serialPortWrap.write(vmu931Channel.toggle());
        isDirty = true;
      }
    }
    if (isDirty) {
      System.out.println("req status");
      serialPortWrap.write(Vmu931Statics.requestStatus());
    } else {
      System.out.println("device config ok");
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
            if (serialPortWrap.peek(data, 3)) {
              int size = data[1] & 0xff;
              byte type = data[2];
              if (serialPortWrap.peek(data, size)) {
                int term = data[size - 1];
                if (term == MESSAGE_DATA_END) {
                  if (size != 12 && size != 20 && size != 24)
                    System.out.println("SIZE=" + size + " " + (char) type);
                  serialPortWrap.advance(size);
                  handle_data(data);
                } else {
                  serialPortWrap.advance(1);
                  System.err.println("discard because term");
                }
              } else
                Thread.sleep(1);
            }
            break;
          case MESSAGE_TEXT_BEG:
            if (serialPortWrap.peek(data, 3)) {
              System.out.println("TEXT");
              int size = data[1] & 0xff;
              char type = (char) (data[2] & 0xff);
              if (serialPortWrap.peek(data, size)) {
                int term = data[size - 1];
                if (term == MESSAGE_TEXT_END) {
                  System.out.println("SIZE=" + size + " " + type + " " + term);
                  serialPortWrap.advance(size);
                } else {
                  serialPortWrap.advance(1);
                  System.err.println("discard");
                }
              } else
                Thread.sleep(1);
            }
            break;
          default:
            serialPortWrap.advance(1);
            System.err.println("discard because head");
            break;
          }
        } else
          Thread.sleep(1);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  // ---
  public static void main(String[] args) {
    new Vmu931("/dev/ttyACM0", EnumSet.of(Vmu931Channel.ACCELEROMETER, Vmu931Channel.GYROSCOPE));
  }
}
