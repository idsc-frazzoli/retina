// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.io.File;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.map.RieterFrame;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.gokart.offline.video.BackgroundImages;
import ch.ethz.idsc.gokart.offline.video.TrackVideoConfig;
import ch.ethz.idsc.gokart.offline.video.TrackVideoWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

// TODO JPH test coverage
/* package */ enum RunTrackVideo {
  ;
  public static void main(String[] args) throws Exception {
    final BackgroundImage backgroundImage = BackgroundImages.rieter(RieterFrame.LOWER);
    ImageIO.write(backgroundImage.bufferedImage(), "png", HomeDirectory.Pictures("some.png"));
    // if (true)
    // return;
    final String imageName = "doughnut_05";
    // final String imageName = "slipmpc_02";
    // Tensor table = Import.of(HomeDirectory.Documents("lidarpose", imageName + ".csv.gz"));
    // TensorUnaryOperator tensorUnaryOperator = CenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, GaussianWindow.FUNCTION), 3);
    // Tensor gp = tensorUnaryOperator.apply(Tensor.of(table.stream().map(r -> r.extract(1, 4))));
    // Tensor lp = tensorUnaryOperator.apply(Tensor.of(table.stream().map(r -> r.extract(5, 8))));
    // System.out.println(imageName);
    final File file = new File("/media/datahaki/data/gokart/doughnut/20191104T150512_05", "log.lcm");
    // final File file = new File("/media/datahaki/data/gokart/rmpcslip/20191104T115120_02", "log.lcm");
    final File dest = HomeDirectory.file(imageName + ".mp4");
    final TrackVideoConfig trackVideoConfig = new TrackVideoConfig();
    // trackVideoConfig.frameRate = PoseLcmServerModule.RATE; // .multiply(RationalScalar.HALF);
    // trackVideoConfig.offlineVideoRenders.add(new LidarPoseVideoRender(table, gp, lp));
    trackVideoConfig.lidarPoints = 15_000;
    // trackVideoConfig.frameLimit = 100;
    try (TrackVideoWriter trackVideoWriter = new TrackVideoWriter(backgroundImage, trackVideoConfig, dest)) {
      OfflineLogPlayer.process(file, trackVideoWriter);
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    }
    System.out.println("[done.]");
  }
}
