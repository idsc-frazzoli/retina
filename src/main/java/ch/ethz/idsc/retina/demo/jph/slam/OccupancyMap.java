// code by jph
package ch.ethz.idsc.retina.demo.jph.slam;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEventListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class OccupancyMap implements LidarSpacialEventListener {
  private static final int WIDTH = 1024;
  private static final float METER_TO_PIXEL = 50;
  private final BufferedImage bufferedImage = new BufferedImage(WIDTH, WIDTH, BufferedImage.TYPE_BYTE_GRAY);
  private final byte[] bytes;
  private Tensor global;
  private Tensor pose;

  public OccupancyMap() {
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    bytes = dataBufferByte.getData();
    global = IdentityMatrix.of(3);
    pose = IdentityMatrix.of(3);
  }

  @Override
  public void spacial(LidarSpacialEvent lidarSpacialEvent) {
  }
}
