// code by jph
package ch.ethz.idsc.retina.lidar.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import ch.ethz.idsc.retina.util.img.ImageCopy;

class Col implements Comparable<Col> {
  final int batch;
  final int rotational;
  byte[] value = new byte[16];
  byte[] inten = new byte[16];

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
  public static int history = 1; // TODO very bad design
  // ---
  private final int height;
  // ---
  private final Queue<Col> queue = new LinkedList<>();
  private int batch = 0;
  private int rotational_last;
  private Col col = null;
  private int max_width;

  public SuperGrayscaleLidarPanorama(int height, int history) {
    this.height = height;
    // this.history = history;
  }

  private BufferedImage distancesImage;
  private BufferedImage intensityImage;

  @Override // from LidarPanorama
  public void setRotational(int rotational) {
    if (rotational < rotational_last) {
      for (Iterator<Col> iterator = queue.iterator(); iterator.hasNext();) {
        Col next = iterator.next();
        if (next.batch < batch - history)
          iterator.remove();
        else
          break;
      }
      List<Col> list = new ArrayList<>();
      list.addAll(queue);
      Collections.sort(list);
      max_width = list.size();
      {
        distancesImage = new BufferedImage(max_width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte[] data = ((DataBufferByte) distancesImage.getRaster().getDataBuffer()).getData();
        int count = 0;
        for (Col col : list) {
          for (int index = 0; index < 16; ++index)
            data[count + index * max_width] = col.value[index];
          ++count;
        }
      }
      {
        intensityImage = new BufferedImage(max_width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte[] data = ((DataBufferByte) intensityImage.getRaster().getDataBuffer()).getData();
        int count = 0;
        for (Col col : list) {
          for (int index = 0; index < 16; ++index)
            data[count + index * max_width] = col.inten[index];
          ++count;
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
    col.value[index] = (byte) (distance >> 4);
    col.inten[index] = ivalue;
  }

  @Override // from LidarPanorama
  public BufferedImage distances() {
    return Objects.isNull(distancesImage) //
        ? new ImageCopy().get()
        : distancesImage;
  }

  @Override // from LidarPanorama
  public BufferedImage intensity() {
    return Objects.isNull(intensityImage) //
        ? new ImageCopy().get()
        : intensityImage;
  }

  @Override
  public int getMaxWidth() {
    return max_width;
  }
}
