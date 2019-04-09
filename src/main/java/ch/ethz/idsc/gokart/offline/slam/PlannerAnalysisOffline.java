// code by ynager
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Objects;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.pos.GokartPoseContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pos.MappedPoseInterface;
import ch.ethz.idsc.gokart.core.pure.TrajectoryEvents;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.GokartRender;
import ch.ethz.idsc.gokart.gui.top.TrajectoryRender;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.WaypointRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.planar.Arrowhead;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

public class PlannerAnalysisOffline implements OfflineLogListener {
  private final Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180425.csv");
  private final RenderInterface renderInterface = //
      new WaypointRender(Arrowhead.of(0.9), new Color(64, 192, 64, 255)).setWaypoints(waypoints);
  private final TrajectoryRender trajectoryRender = new TrajectoryRender();
  private final MappedPoseInterface gokartPoseInterface = new GokartPoseContainer();
  private final Scalar delta = Quantity.of(0.1, SI.SECOND);
  // ---
  private GokartPoseEvent gokartPoseEvent;
  private ScatterImage scatterImage;
  private Scalar time_next = Quantity.of(0, SI.SECOND);

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
      PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
      scatterImage = new WallScatterImage(predefinedMap);
      // ---
      GeometricLayer geometricLayer = new GeometricLayer(predefinedMap.getModel2Pixel(), Tensors.vector(0, 0, 0));
      BufferedImage image = scatterImage.getImage();
      Graphics2D graphics = image.createGraphics();
      gokartPoseInterface.setPose(gokartPoseEvent.getPose(), gokartPoseEvent.getQuality());
      GokartRender gokartRender = new GokartRender();
      gokartRender.gokartPoseListener.getEvent(gokartPoseEvent);
      trajectoryRender.render(geometricLayer, graphics);
      renderInterface.render(geometricLayer, graphics);
      gokartRender.render(geometricLayer, graphics);
      // ---
      try {
        // TODO different filename
        ImageIO.write(image, "png", HomeDirectory.Pictures("log", Magnitude.SECOND.apply(time).toString() + ".png"));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }
}