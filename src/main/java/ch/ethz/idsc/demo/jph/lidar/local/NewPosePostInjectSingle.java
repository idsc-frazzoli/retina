// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.vmu931.PlanarVmu931Type;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.offline.api.FirstLogMessage;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.pose.LidarLocalizationOffline;
import ch.ethz.idsc.gokart.offline.pose.LogPosePostInject;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum NewPosePostInjectSingle {
  ;
  public static void in(GokartLogInterface gokartLogInterface) throws Exception {
    File file = gokartLogInterface.file();
    in(file, gokartLogInterface.pose(), new File(file.getParentFile(), "post.lcm"));
  }

  public static void in(File origin, Tensor pose, File target) throws Exception {
    // final File post_lcm = HomeDirectory.file(StaticHelper.POST_LCM);
    LidarLocalizationOffline lidarLocalizationOffline = //
        new LidarLocalizationOffline(PredefinedMap.DUBILAB_LOCALIZATION_20190309, pose);
    LogPosePostInject logPosePostInject = new LogPosePostInject();
    lidarLocalizationOffline.gokartPoseListeners.add(logPosePostInject);
    logPosePostInject.process(origin, target, lidarLocalizationOffline);
  }

  public static void main(String[] args) throws Exception {
    SensorsConfig.GLOBAL.planarVmu931Type = PlanarVmu931Type.FLIPPED.name();
    // GokartLogInterface gokartLogInterface = GokartLogAdapter.of(new File("/media/datahaki/data/gokart/cuts/20190328/20190328T164433_01"));
    File source = new File("/media/datahaki/data/gokart/tokio/20190310/20190310T220933_00", "log.lcm");
    Optional<ByteBuffer> optional = FirstLogMessage.of(source, GokartPoseChannel.INSTANCE.channel());
    in( //
        source, //
        // DatahakiLogFileLocator.file(GokartLogFile._20190418T145229_b6a70baf), //
        // Tensors.fromString("{28.9864287[m], 27.5183134[m], -1.1886032}"), //
        GokartPoseEvent.of(optional.get()).getPose(), //
        new File(source.getParentFile(), "post.lcm"));
  }
}
