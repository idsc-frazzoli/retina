// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.calib.RimoSinusIonModel;
import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.perc.GokartSegmentProjection;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ class FreeSpaceVideo implements OfflineLogListener, AutoCloseable {
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final GokartSegmentProjection gokartSegmentProjection = new GokartSegmentProjection( //
      Magnitude.ONE.toDouble(SensorsConfig.GLOBAL.vlp16_twist), //
      Magnitude.METER.toDouble(SensorsConfig.GLOBAL.vlp16Height), //
      -1) {
    @Override // from Vlp16SegmentProjection
    public void freeSpaceUntil(int azimuth, float x, float y) {
      tensor.append(Tensors.vector(x, y));
    }
  };
  private final Mp4AnimationWriter mp4AnimationWriter;
  private Tensor tensor = Tensors.empty();
  private final BufferedImage mapImage = new BufferedImage(640, 640, BufferedImage.TYPE_4BYTE_ABGR);
  private final Graphics2D mapGraphics = mapImage.createGraphics();
  private final Tensor model2Pixel = PredefinedMap.DUBILAB_LOCALIZATION_20190314.getModel2Pixel();

  public FreeSpaceVideo() throws InterruptedException, IOException {
    velodyneDecoder.addRayListener(gokartSegmentProjection);
    mp4AnimationWriter = new Mp4AnimationWriter( //
        HomeDirectory.file("some2.mp4").toString(), //
        new Dimension(640, 640), //
        5);
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
      GeometricLayer geometricLayer = GeometricLayer.of(model2Pixel);
      geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
      geometricLayer.pushMatrix(SensorsConfig.GLOBAL.vlp16Gokart());
      {
        {
          mapGraphics.setColor(Color.BLACK);
          for (Tensor vector : tensor) {
            Point2D point2d = geometricLayer.toPoint2D(vector);
            mapGraphics.fillRect((int) point2d.getX(), (int) point2d.getY(), 2, 2);
          }
        }
        {
          mapGraphics.setColor(Color.WHITE);
          mapGraphics.fill(geometricLayer.toPath2D(tensor.append(Array.zeros(2))));
        }
        System.out.println(tensor.length());
      }
      frameGraphics.drawImage(mapImage, 0, 0, null);
      {
        frameGraphics.setColor(Color.RED);
        frameGraphics.fill(geometricLayer.toPath2D(tensor));
      }
      {
        frameGraphics.setColor(Color.GREEN);
        frameGraphics.fill(geometricLayer.toPath2D(RimoSinusIonModel.standard().footprint()));
      }
      geometricLayer.popMatrix();
      geometricLayer.popMatrix();
      mp4AnimationWriter.write(frameImage);
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
      OfflineLogPlayer.process(HomeDirectory.file("ensemblelaps", "dynamic", "m13.lcm"), freeSpaceVideo);
    }
  }
}
