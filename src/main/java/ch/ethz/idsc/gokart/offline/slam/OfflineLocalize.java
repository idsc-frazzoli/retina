// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.gui.gokart.top.SensorsConfig;
import ch.ethz.idsc.retina.gui.gokart.top.StoreMapUtil;
import ch.ethz.idsc.retina.gui.gokart.top.ViewLcmFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TableBuilder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.SquareMatrixQ;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Round;

public abstract class OfflineLocalize implements LidarRayBlockListener, DavisImuFrameListener {
  /** 3x3 transformation matrix of lidar to center of rear axle */
  protected static final Tensor LIDAR = Se2Utils.toSE2Matrix(SensorsConfig.GLOBAL.vlp16).unmodifiable();
  // ---
  protected final BufferedImage map_image = StoreMapUtil.load(UserHome.Pictures("hangar03.png"));
  private final TableBuilder tableBuilder = new TableBuilder();
  private Scalar time;
  public final Tensor skipped = Tensors.empty();
  /** 3x3 matrix */
  protected Tensor model;
  private int image_count = 0;
  private Tensor gyro_y = Tensors.empty();

  public OfflineLocalize(Tensor model) {
    if (map_image.getType() != BufferedImage.TYPE_BYTE_GRAY)
      throw new RuntimeException();
    if (!SquareMatrixQ.of(model))
      throw new RuntimeException();
    this.model = model;
  }

  public final void setTime(Scalar time) {
    this.time = time;
  }

  public final Tensor getPositionVector() {
    return Se2Utils.fromSE2Matrix(model);
  }

  @Override
  public void imuFrame(DavisImuFrame davisImuFrame) {
    // Tensor gyro = davisImuFrame.gyroImageFrame().map(Magnitude.ANGULAR_RATE).map(Round._3);
    // System.out.println(time + " " + davisImuFrame.getTime() + " " + gyro);
    Scalar rate = davisImuFrame.gyroImageFrame().Get(1); // image - y axis
    gyro_y.append(rate);
  }

  protected final Scalar getGyroAndReset() {
    Scalar mean = Mean.of(gyro_y).Get();
    gyro_y = Tensors.empty();
    return mean;
  }

  protected final void appendRow(Tensor dstate, Scalar ratio, int sum, double duration) {
    tableBuilder.appendRow( //
        time.map(Magnitude.SECOND), //
        Se2Utils.fromSE2Matrix(model), //
        dstate, //
        Clip.unit().requireInside(ratio), // mathematica 8
        RealScalar.of(sum), //
        RealScalar.of(duration));
    System.out.println(time.map(Magnitude.SECOND).map(Round._2) + " " + ratio);
  }

  protected final void skip() {
    skipped.append(time);
  }

  public final Tensor getTable() {
    return tableBuilder.toTable();
  }

  protected final void render(Tensor points) {
    BufferedImage image = new BufferedImage(map_image.getWidth(), map_image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics2d = image.createGraphics();
    graphics2d.drawImage(map_image, 0, 0, null);
    GeometricLayer geometricLayer = new GeometricLayer(ViewLcmFrame.MODEL2PIXEL_INITIAL, Array.zeros(3));
    geometricLayer.pushMatrix(model);
    geometricLayer.pushMatrix(LIDAR);
    graphics2d.setColor(Color.GREEN);
    for (Tensor x : points) {
      Point2D p = geometricLayer.toPoint2D(x);
      graphics2d.fillRect((int) p.getX(), (int) p.getY(), 2, 2);
    }
    graphics2d.setColor(Color.GRAY);
    {
      Point2D p0 = geometricLayer.toPoint2D(Tensors.vector(0, 0));
      Point2D pX = geometricLayer.toPoint2D(Tensors.vector(10, 0));
      Point2D pY = geometricLayer.toPoint2D(Tensors.vector(0, 10));
      graphics2d.draw(new Line2D.Double(p0, pX));
      graphics2d.draw(new Line2D.Double(p0, pY));
    }
    // graphics2d.drawString("q=" + quality, 0, 10);
    File dir = UserHome.Pictures(getClass().getSimpleName());
    dir.mkdir();
    if (dir.isDirectory())
      try {
        ImageIO.write(image, "png", new File(dir, String.format("%02d.png", image_count)));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    ++image_count;
  }
}
