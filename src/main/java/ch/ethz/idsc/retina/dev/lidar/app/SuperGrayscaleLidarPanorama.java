// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.util.img.ImageCopy;

class Col implements Comparable<Col> {
  final int batch;
  final int rotational;
  byte[] value = new byte[16];

  public Col(int batch, int rotational) {
    this.batch = batch;
    this.rotational = rotational;
  }

  @Override // from Comparable
  public int compareTo(Col row) {
    return Integer.compare(rotational, row.rotational);
  }
}

/** grayscale images visualizing distance and intensity */
public class SuperGrayscaleLidarPanorama implements LidarPanorama {
  private final int height;
  // ---
  private final int history;
  final Queue<Col> queue = new LinkedList<>();

  public SuperGrayscaleLidarPanorama(int max_width, int height, int history) {
    this.height = height;
    this.history = history;
  }

  private int batch = 0;
  private int rotational_last;
  private Col col = null;
  private int max_width;

  @Override // from LidarPanorama
  public void setRotational(int rotational) {
    if (rotational < rotational_last) {
      synchronized (queue) {
        Iterator<Col> iterator = queue.iterator();
        while (iterator.hasNext()) {
          Col next = iterator.next();
          if (next.batch < batch - history)
            iterator.remove();
          else
            break;
        }
      }
      ++batch;
    }
    rotational_last = rotational;
    col = new Col(batch, rotational);
    synchronized (queue) {
      queue.add(col);
    }
  }

  @Override // from LidarPanorama
  public void setReading(int index, int distance, byte ivalue) {
    col.value[index] = (byte) (distance >> 5);
  }

  @Override // from LidarPanorama
  public BufferedImage distances() {
    List<Col> list = new ArrayList<>();
    synchronized (queue) {
      list.addAll(queue);
    }
    Collections.sort(list);
    max_width = list.size();
    BufferedImage distancesImage = new BufferedImage(max_width, height, BufferedImage.TYPE_BYTE_GRAY);
    byte[] distances = ((DataBufferByte) distancesImage.getRaster().getDataBuffer()).getData();
    int count = 0;
    int[] offset = new int[height];
    IntStream.range(0, height).forEach(i -> offset[i] = i * max_width);
    for (Col col : list) {
      for (int index = 0; index < 16; ++index)
        distances[count + index * max_width] = col.value[index];
      ++count;
    }
    return distancesImage;
  }

  @Override // from LidarPanorama
  public BufferedImage intensity() {
    return new ImageCopy().get();
  }

  @Override
  public int getMaxWidth() {
    return max_width;
  }
}
