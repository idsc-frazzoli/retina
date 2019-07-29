// code by ynager
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Objects;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.plan.TrajectoryEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.GlobalGokartRender;
import ch.ethz.idsc.gokart.gui.top.GokartRender;
import ch.ethz.idsc.gokart.gui.top.TrajectoryRender;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.WaypointRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

public class PlannerAnalysisOffline implements OfflineLogListener {
  private final RenderInterface renderInterface;
  private final TrajectoryRender trajectoryRender = new TrajectoryRender();
  private final Scalar delta = Quantity.of(0.1, SI.SECOND);
  private final File folder = HomeDirectory.Pictures(getClass().getSimpleName());
  // ---
  private GokartPoseEvent gokartPoseEvent;
  private ScatterImage scatterImage;
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private int count = -1;

  public PlannerAnalysisOffline(Tensor waypoints) {
    folder.mkdir();
    renderInterface = new WaypointRender(Arrowhead.of(0.9), new Color(64, 192, 64, 255)).setWaypoints(waypoints);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
    } else //
    if (channel.equals(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME))
      trajectoryRender.trajectory(TrajectoryEvents.trajectory(byteBuffer));
    else //
    if (Scalars.lessThan(time_next, time) && Objects.nonNull(gokartPoseEvent)) {
      time_next = time.add(delta);
      System.out.print("Extracting log at " + time.map(Round._2) + "\n");
      PredefinedMap predefinedMap = LocalizationConfig.GLOBAL.getPredefinedMap();
      scatterImage = new WallScatterImage(predefinedMap);
      // ---
      GeometricLayer geometricLayer = GeometricLayer.of(predefinedMap.getModel2Pixel());
      BufferedImage image = scatterImage.getImage();
      Graphics2D graphics = image.createGraphics();
      GokartRender gokartRender = new GlobalGokartRender();
      gokartRender.gokartPoseListener.getEvent(gokartPoseEvent);
      trajectoryRender.render(geometricLayer, graphics);
      renderInterface.render(geometricLayer, graphics);
      gokartRender.render(geometricLayer, graphics);
      // ---
      try {
        ImageIO.write(image, "png", new File(folder, String.format("%06d.png", ++count)));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }
}