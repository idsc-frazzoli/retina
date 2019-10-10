// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.io.File;

import ch.ethz.idsc.demo.VideoBackground;
import ch.ethz.idsc.gokart.core.pos.PoseLcmServerModule;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.gokart.offline.video.TrackVideoConfig;
import ch.ethz.idsc.gokart.offline.video.TrackVideoWriter;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.win.GaussianWindow;

// TODO JPH test coverage
/* package */ enum RunTrackVideo {
  ;
  public static void main(String[] args) throws Exception {
    final BackgroundImage backgroundImage = BackgroundImage.from( //
        HomeDirectory.Pictures("20190701.png"), //
        VideoBackground._20190401);
    final String imageName = "20190701T175650_01";
    Tensor table = Import.of(HomeDirectory.Documents("lidarpose", imageName + ".csv.gz"));
    TensorUnaryOperator tensorUnaryOperator = CenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, GaussianWindow.FUNCTION), 3);
    Tensor gp = tensorUnaryOperator.apply(Tensor.of(table.stream().map(r -> r.extract(1, 4))));
    Tensor lp = tensorUnaryOperator.apply(Tensor.of(table.stream().map(r -> r.extract(5, 8))));
    // System.out.println(imageName);
    final File file = new File("/media/datahaki/data/gokart/0701mpc/" + imageName, "log.lcm");
    final File dest = HomeDirectory.file("video", imageName + ".mp4");
    final TrackVideoConfig trackVideoConfig = new TrackVideoConfig();
    trackVideoConfig.frameRate = PoseLcmServerModule.RATE.multiply(RationalScalar.HALF);
    trackVideoConfig.offlineVideoRenders.add(new LidarPoseVideoRender(table, gp, lp));
    // trackVideoConfig.frameLimit = 200;
    try (TrackVideoWriter trackVideoWriter = new TrackVideoWriter(backgroundImage, trackVideoConfig, dest)) {
      OfflineLogPlayer.process(file, trackVideoWriter);
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    }
    System.out.println("[done.]");
  }
}
