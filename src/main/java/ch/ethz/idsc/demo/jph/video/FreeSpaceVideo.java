// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.perc.Vlp16SegmentProjection;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.HomeDirectory;

public class FreeSpaceVideo implements OfflineLogListener, AutoCloseable {
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final Vlp16SegmentProjection vlp16SegmentProjection = new Vlp16SegmentProjection(SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue(), -1) {
    @Override // from Vlp16SegmentProjection
    public void freeSpaceUntil(int azimuth, float x, float y) {
      tensor.append(Tensors.vector(x, y));
    }
  };
  private final Mp4AnimationWriter mp4AnimationWriter;
  private Tensor tensor = Tensors.empty();
  BufferedImage mapImage = new BufferedImage(640, 640, BufferedImage.TYPE_4BYTE_ABGR);

  public FreeSpaceVideo() throws InterruptedException, IOException {
    velodyneDecoder.addRayListener(vlp16SegmentProjection);
    mp4AnimationWriter = new Mp4AnimationWriter( //
        HomeDirectory.file("some2.mp4").toString(), //
        new Dimension(640, 640), //
        10);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(CHANNEL_LIDAR))
      velodyneDecoder.lasers(byteBuffer);
    else //
    if (channel.equals(GokartPoseChannel.INSTANCE.channel())) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      BufferedImage frameImage = new BufferedImage(640, 640, BufferedImage.TYPE_3BYTE_BGR);
      Graphics2D frameGraphics = frameImage.createGraphics();
      Tensor model2Pixel = PredefinedMap.DUBILAB_LOCALIZATION_20190314.getModel2Pixel();
      GeometricLayer geometricLayer = GeometricLayer.of(model2Pixel);
      {
        geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
        geometricLayer.pushMatrix(SensorsConfig.GLOBAL.vlp16Gokart());
        Graphics2D mapGraphics = mapImage.createGraphics();
        {
          mapGraphics.setStroke(new BasicStroke());
          mapGraphics.setColor(Color.BLACK);
          for (Tensor vector : tensor) {
            Point2D point2d = geometricLayer.toPoint2D(vector);
            mapGraphics.fillRect((int) point2d.getX(), (int) point2d.getY(), 1, 1);
          }
        }
        {
          mapGraphics.setColor(Color.WHITE);
          mapGraphics.fill(geometricLayer.toPath2D(tensor.append(Array.zeros(2))));
        }
        System.out.println(tensor.length());
        geometricLayer.popMatrix();
        geometricLayer.popMatrix();
      }
      frameGraphics.drawImage(mapImage, 0, 0, null);
      mp4AnimationWriter.append(frameImage);
      // System.out.println("here");
      tensor = Tensors.empty();
    }
  }

  @Override // from AutoCloseable
  public void close() throws Exception {
    mp4AnimationWriter.close();
  }

  public static void main(String[] args) throws InterruptedException, IOException, Exception {
    try (FreeSpaceVideo freeSpaceVideo = new FreeSpaceVideo()) {
      OfflineLogPlayer.process(new File("/home/datahaki/ensemblelaps/dynamic", "m13.lcm"), freeSpaceVideo);
    }
  }
}
