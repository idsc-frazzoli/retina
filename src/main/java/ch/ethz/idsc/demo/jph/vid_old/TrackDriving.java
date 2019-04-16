// code by jph
package ch.ethz.idsc.demo.jph.vid_old;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.top.AxisAlignedBox;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.gui.top.ExtrudedFootprintRender;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Ramp;

/* package */ class TrackDriving extends AbstractFrameRender {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._063.cyclic().deriveWithAlpha(128);
  private static final ColorDataIndexed COLOR_DATA_INDEXED_32 = COLOR_DATA_INDEXED.deriveWithAlpha(32);
  private static final ColorDataIndexed COLOR_DATA_INDEXED_64 = COLOR_DATA_INDEXED.deriveWithAlpha(64);
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  private static final Tensor FOOTPRINT = VEHICLE_MODEL.footprint();
  private static final Tensor COG = Tensors.of( //
      Magnitude.METER.apply(ChassisGeometry.GLOBAL.xAxleRtoCoM), //
      RealScalar.ZERO);
  private static final int ICON_SIZE = 32;
  private static final AxisAlignedBox AXIS_ALIGNED_BOX = //
      new AxisAlignedBox(RimoTireConfiguration._REAR.halfWidth().multiply(RealScalar.of(0.5)));
  private static final Tensor SLIM = Tensors.matrixDouble( //
      new double[][] { { 0.2, 0.02 }, { -0.2, 0.02 }, { -0.2, -0.02 }, { 0.2, -0.02 } }).unmodifiable();
  // ---
  final Tensor tensor;
  private final int id;
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
  private final ExtrudedFootprintRender extrudedFootprintRender = new ExtrudedFootprintRender();

  public TrackDriving(Tensor tensor, int id) {
    this.tensor = tensor;
    this.id = id;
  }

  public Tensor row(int index) {
    index = Math.min(tensor.length() - 1, index);
    return tensor.get(index);
  }

  public int maxIndex() {
    return tensor.length();
  }

  /** @param name consisting of initials */
  public void setDriver(String name) {
    BufferedImage icon = ResourceData.bufferedImage("/image/driver/" + name + ".png");
    if (Objects.nonNull(icon))
      bufferedImage = icon;
    else
      System.err.println("driver icon not found [" + name + "]");
  }

  private boolean extrusion = false;
  private boolean posetrail = false;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor row = row(render_index);
    Tensor xya = row.extract(10, 13); // unitless
    if (extrusion) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvents.create(GokartPoseHelper.attachUnits(xya), RealScalar.ONE);
      extrudedFootprintRender.gokartPoseListener.getEvent(gokartPoseEvent);
      extrudedFootprintRender.gokartStatusListener.getEvent(new GokartStatusEvent(row.Get(8).number().floatValue()));
      extrudedFootprintRender.render(geometricLayer, graphics);
    }
    if (posetrail) {
      PathRender pathRender = new PathRender(COLOR_DATA_INDEXED_64.getColor(id), 1.5f);
      Tensor points = Tensor.of(tensor.stream().limit(render_index) //
          .map(v -> v.extract(10, 13)) //
          .map(Se2Bijection::new) //
          .map(Se2Bijection::forward) //
          .map(tuo -> tuo.apply(COG)));
      pathRender.setCurve(points, false).render(geometricLayer, graphics);
    }
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    {
      Path2D path2d = geometricLayer.toPath2D(FOOTPRINT);
      path2d.closePath();
      graphics.setColor(COLOR_DATA_INDEXED_32.getColor(id));
      graphics.fill(path2d);
      graphics.setColor(COLOR_DATA_INDEXED.getColor(id));
      graphics.draw(path2d);
    }
    {
      Point2D point2d = geometricLayer.toPoint2D(COG);
      graphics.drawImage(bufferedImage, //
          (int) point2d.getX() - ICON_SIZE / 2, //
          (int) point2d.getY() - ICON_SIZE / 2, ICON_SIZE, ICON_SIZE, null);
    }
    {
      graphics.setStroke(new BasicStroke());
      Tensor torquePair = row.extract(1, 3).multiply(RealScalar.of(3E-4));
      AxleConfiguration axleConfiguration = RimoAxleConfiguration.rear();
      graphics.setColor(new Color(0, 0, 255, 128));
      for (int wheel = 0; wheel < 2; ++wheel) {
        geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(axleConfiguration.wheel(wheel).local()));
        graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_BOX.alongX(torquePair.Get(wheel))));
        geometricLayer.popMatrix();
      }
    }
    {
      graphics.setStroke(new BasicStroke(2.5f));
      Scalar factor = Ramp.FUNCTION.apply(row.Get(9).negate().subtract(RealScalar.of(0.02))).divide(RealScalar.of(-0.06));
      graphics.setColor(new Color(255, 0, 0, 128));
      graphics.draw(geometricLayer.toVector(Tensors.vector(1, 0), UnitVector.of(2, 0).multiply(factor)));
    }
    {
      graphics.setStroke(new BasicStroke());
      AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(Quantity.of(row.Get(8), "SCE"));
      graphics.setColor(new Color(128, 128, 128, 128));
      for (int wheel = 0; wheel < 2; ++wheel) {
        WheelConfiguration wheelConfiguration = axleConfiguration.wheel(wheel);
        geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(wheelConfiguration.local()));
        graphics.fill(geometricLayer.toPath2D(SLIM));
        geometricLayer.popMatrix();
      }
    }
    graphics.setStroke(new BasicStroke());
    geometricLayer.popMatrix();
  }

  public Scalar timeFor(int index) {
    return row(index).Get(0);
  }

  public void setExtrusion(boolean extrusion) {
    this.extrusion = extrusion;
  }
}
