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

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisEventProvider;

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
  private static final int BUFFER_SIZE = 512;
  private static final String HEADER_TERMINATOR = "#End Of ASCII Header";
  // ---
  private final DavisDecoder davisDecoder;
  private final byte[] bytes = new byte[8 * BUFFER_SIZE];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
  private final InputStream inputStream;
  private int available = 0;
  /** lines of header in log file */
  private final List<String> header = new LinkedList<>();

  public AedatFileSupplier(File file, DavisDecoder davisDecoder) throws Exception {
    this.davisDecoder = davisDecoder;
    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
    int skip = 0;
    while (true) {
      String string = bufferedReader.readLine();
      header.add(string);
      skip += string.length() + 2; // add 2 characters of line break
      if (string.equals(HEADER_TERMINATOR))
        break;
    }
    bufferedReader.close();
    inputStream = new FileInputStream(file);
    inputStream.skip(skip);
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
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
        davisDecoder.read(byteBuffer);
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
