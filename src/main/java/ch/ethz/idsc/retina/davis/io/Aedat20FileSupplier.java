// code by jph
package ch.ethz.idsc.retina.davis.io;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** parser for aedat version 2.0
 * 
 * Quotes from the iniLabs User Guide DAVIS240:
 * 
 * "An .aedat file contains headers, where each header line starts with '#' and
 * ends with the hex characters 0x0D 0x0A (CRLF, windows line ending). Then
 * there are a series of 8-byte words."
 * 
 * "An IMU sample is a subclass of an APS type event. 7 words are sent in
 * series, these being 3 axes for accel, temperature, and 3 axes for gyro
 * look at jAER’s IMUSample class for more info." */
public class Aedat20FileSupplier implements StartAndStoppable {
  private static final int BUFFER_SIZE = 8 * 512;
  // ---
  private final byte[] bytes = new byte[BUFFER_SIZE];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
  private final DavisDecoder davisDecoder;
  private final InputStream inputStream;

  public Aedat20FileSupplier(File file, DavisDecoder davisDecoder) throws Exception {
    this.davisDecoder = davisDecoder;
    AedatFileHeader aedatFileHeader = new AedatFileHeader(file);
    inputStream = aedatFileHeader.getInputStream();
    byteBuffer.order(ByteOrder.BIG_ENDIAN); // order defined by aedat format
  }

  @Override
  public void start() {
    try {
      int available = 0;
      while (true) {
        if (available == 0) {
          available += inputStream.read(bytes, 0, bytes.length);
          if (available < 2) // end of file, at least 2 bytes are required for next decoding
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
