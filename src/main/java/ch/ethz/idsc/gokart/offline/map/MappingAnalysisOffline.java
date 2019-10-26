// code by ynager
package ch.ethz.idsc.gokart.offline.map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.map.BayesianOccupancyGrid;
import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.GlobalGokartRender;
import ch.ethz.idsc.gokart.gui.top.GokartRender;
import ch.ethz.idsc.gokart.offline.slam.LidarProcessOffline;
import ch.ethz.idsc.gokart.offline.slam.ScatterImage;
import ch.ethz.idsc.gokart.offline.slam.WallScatterImage;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public abstract class MappingAnalysisOffline extends LidarProcessOffline implements Consumer<BufferedImage> {
  private final SpacialXZObstaclePredicate spacialXZObstaclePredicate = //
      SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final BayesianOccupancyGrid bayesianOccupancyGrid;
  // ---
  private final PredefinedMap predefinedMap;
  private final Scalar delta;
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private GokartPoseEvent gokartPoseEvent;

  public MappingAnalysisOffline(LocalizationConfig localizationConfig, MappingConfig mappingConfig, Scalar delta) {
    super(new Vlp16SegmentProvider(SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue(), -1));
    predefinedMap = localizationConfig.getPredefinedMap();
    this.delta = delta;
    bayesianOccupancyGrid = mappingConfig.createBayesianOccupancyGrid();
  }

  @Override
  protected void protected_event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      bayesianOccupancyGrid.setPose(gokartPoseEvent.getPose());
    }
    // ---
    if (Scalars.lessThan(time_next, time) && //
        Objects.nonNull(gokartPoseEvent)) {
      time_next = time.add(delta);
      ScatterImage scatterImage = new WallScatterImage(predefinedMap);
      BufferedImage bufferedImage = scatterImage.getImage();
      GeometricLayer geometricLayer = new GeometricLayer(predefinedMap.getModel2Pixel(), Tensors.vector(0, 0, 0));
      Graphics2D graphics = bufferedImage.createGraphics();
      bayesianOccupancyGrid.render(geometricLayer, graphics);
      GokartRender gokartRender = new GlobalGokartRender();
      gokartRender.gokartPoseListener.getEvent(gokartPoseEvent);
      gokartRender.render(geometricLayer, graphics);
      // if (Scalars.lessEquals(RealScalar.of(3), Magnitude.SECOND.apply(time)) && flag == false) {
      // grid.setNewlBound(Tensors.vector(20, 20));
      // flag = true;
      // }
      // ---
      // grid.genObstacleMap();
      // System.out.println(time);
      bayesianOccupancyGrid.genObstacleMap();
      accept(bufferedImage);
    }
  }

  @Override
  protected void process(float x, float y, float z) {
    boolean isObstacle = spacialXZObstaclePredicate.isObstacle(x, z);
    // ---
    bayesianOccupancyGrid.processObservation( //
        Tensors.vectorDouble(x, y), //
        isObstacle ? 1 : 0);
  }
}