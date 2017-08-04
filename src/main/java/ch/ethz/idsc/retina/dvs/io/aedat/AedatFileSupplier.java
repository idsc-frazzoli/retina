// code by jph
package ch.ethz.idsc.retina.dvs.io.aedat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis240c.ApsDavisEvent;
import ch.ethz.idsc.retina.dev.davis240c.ApsDavisEventListener;
import ch.ethz.idsc.retina.dev.davis240c.DavisEventListener;
import ch.ethz.idsc.retina.dev.davis240c.DavisEventProvider;
import ch.ethz.idsc.retina.dev.davis240c.DvsDavisEvent;
import ch.ethz.idsc.retina.dev.davis240c.DvsDavisEventListener;
import ch.ethz.idsc.retina.dev.davis240c.ImuDavisEvent;

/** Quotes from the iniLabs User Guide DAVIS240:
 * 
 * "An .aedat file contains headers, where each header line starts with '#'
 * and ends with the hex characters 0x0D 0x0A (CRLF, windows line ending).
 * Then there are a series of 8-byte words."
 * 
 * "An IMU sample is a subclass of an APS type event. 7 words are sent in series,
 * these being 3 axes for accel, temperature, and 3 axes for gyro -
 * TODO look at jAER’s IMUSample class for more info." */
public class AedatFileSupplier implements DavisEventProvider {
  private final byte[] bytes = new byte[8 * StaticHelper.BUFFER_SIZE];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
  private final InputStream inputStream;
  private int available = 0;
  List<String> header = new LinkedList<>();
  private List<DvsDavisEventListener> dvsDavisEventListeners = new LinkedList<>();
  private List<ApsDavisEventListener> apsDavisEventListeners = new LinkedList<>();

  public AedatFileSupplier(File file) throws Exception {
    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
    int skip = 0;
    while (true) {
      String string = bufferedReader.readLine();
      header.add(string);
      // System.out.println(string);
      skip += string.length() + 2; // add line break
      if (string.equals("#End Of ASCII Header"))
        break;
    }
    bufferedReader.close();
    inputStream = new FileInputStream(file);
    inputStream.skip(skip);
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
  }

  @Override
  public void addListener(DavisEventListener davisEventListener) {
    if (davisEventListener instanceof DvsDavisEventListener)
      dvsDavisEventListeners.add((DvsDavisEventListener) davisEventListener);
    if (davisEventListener instanceof ApsDavisEventListener)
      apsDavisEventListeners.add((ApsDavisEventListener) davisEventListener);
  }

  @Override
  public void start() {
    try {
      while (true) {
        if (available == 0) {
          available += inputStream.read(bytes, 0, bytes.length);
          if (available < 2)
            break;
          byteBuffer.position(0);
        }
        int many = byteBuffer.getInt();
        int time = byteBuffer.getInt(); // microseconds
        int x = (many >> 12) & 0x3ff; // length 10 bit
        int y = (many >> 22) & 0x1ff; // length 09 bit
        boolean isDvs = (many & 0x80000000) == 0;
        if (isDvs) {
          int i = (many >> 11) & 1; // length 1 bit
          DvsDavisEvent dvsDavisEvent = new DvsDavisEvent(time, x, y, i);
          dvsDavisEventListeners.forEach(l -> l.dvs(dvsDavisEvent));
        } else {
          int read = (many >> 10) & 0x3;
          if (read == 1) { // signal
            int adc = many & 0x3ff;
            ApsDavisEvent apsDavisEvent = new ApsDavisEvent(time, x, y, adc);
            apsDavisEventListeners.forEach(l -> l.aps(apsDavisEvent));
          } else //
          if (read == 0) { // reset read
          } else //
          if (read == 3) { // imu
            // TODO
            ImuDavisEvent imuDavisEvent = new ImuDavisEvent();
            dvsDavisEventListeners.forEach(l -> l.imu(imuDavisEvent));
          }
        }
        available -= 8;
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void stop() {
    try {
      inputStream.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
