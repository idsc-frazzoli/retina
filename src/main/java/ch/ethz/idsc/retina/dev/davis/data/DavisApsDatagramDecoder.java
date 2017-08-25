// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.core.ColumnTimedImageListener;
import ch.ethz.idsc.retina.dev.davis.io.DavisDatagram;

public class DavisApsDatagramDecoder {
  private final BufferedImage bufferedImage;
  private final int[] time = new int[240];
  private final byte[] imageData;
  private final List<ColumnTimedImageListener> listeners = new LinkedList<>();
  private boolean isComplete = true;
  private int x_next = 0;

  public DavisApsDatagramDecoder() {
    bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    imageData = dataBufferByte.getData();
  }

  public void addListener(ColumnTimedImageListener columnTimedImageListener) {
    listeners.add(columnTimedImageListener);
  }

  public boolean hasListeners() {
    return !listeners.isEmpty();
  }

  public void decode(ByteBuffer byteBuffer) {
    // if client is started before server, x was observed not to be in range, e.g. x==-1
    int x = byteBuffer.getShort();
    // TODO check that value in valid range
    isComplete &= x == x_next;
    for (int column = 0; column < DavisDatagram.BLOCK_COLUMNS; ++column) {
      time[x] = byteBuffer.getInt();
      for (int y = 0; y < 180; ++y)
        imageData[x + y * 240] = byteBuffer.get(); // TODO increment offset (instead of multiplication)
      ++x;
    }
    x_next = x;
    if (x == 240) {
      final boolean complete = isComplete;
      listeners.forEach(listener -> listener.image(time, bufferedImage, complete));
      isComplete = true;
      x_next = 0;
    }
  }
}
