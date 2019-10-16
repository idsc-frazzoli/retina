// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.nio.ByteBuffer;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.gokart.offline.video.OfflineVideoRender;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.CirclePoints;

class LidarPoseVideoRender implements OfflineVideoRender {
  private static final Scalar RADIUS = RealScalar.of(2);
  // ---
  private final NavigableMap<Scalar, Tensor> map_gp = new TreeMap<>();
  private final NavigableMap<Scalar, Tensor> map_lp = new TreeMap<>();
  // ---
  private Scalar time;

  public LidarPoseVideoRender(Tensor tensor, Tensor gp, Tensor lp) {
    for (int index = 0; index < tensor.length(); ++index) {
      map_gp.put(tensor.Get(index, 0), gp.get(index));
      map_lp.put(tensor.Get(index, 0), lp.get(index));
    }
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    this.time = time;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(time)) {
      GraphicsUtil.setQualityHigh(graphics);
      graphics.setStroke(new BasicStroke(1.5f));
      {
        Entry<Scalar, Tensor> lowerEntry = map_gp.lowerEntry(Magnitude.SECOND.apply(time));
        if (Objects.nonNull(lowerEntry)) {
          Tensor gp = lowerEntry.getValue();
          geometricLayer.pushMatrix(Se2Matrix.of(gp));
          {
            graphics.setColor(new Color(255, 0, 255, 255));
            graphics.draw(geometricLayer.toLine2D(UnitVector.of(2, 0).multiply(RADIUS)));
            graphics.setColor(new Color(0, 255, 255, 255));
            graphics.draw(geometricLayer.toLine2D(UnitVector.of(2, 1).multiply(RADIUS)));
          }
          geometricLayer.popMatrix();
        } else
          System.err.println("lidar pose unavailable");
      }
      {
        Entry<Scalar, Tensor> lowerEntry = map_lp.lowerEntry(Magnitude.SECOND.apply(time));
        if (Objects.nonNull(lowerEntry)) {
          Tensor lp = lowerEntry.getValue();
          geometricLayer.pushMatrix(Se2Matrix.of(lp));
          graphics.setColor(new Color(128, 128, 128, 128));
          graphics.draw(geometricLayer.toPath2D(CirclePoints.of(20).multiply(RealScalar.of(0.1)), true));
          {
            graphics.setColor(new Color(255, 0, 0, 255));
            graphics.draw(geometricLayer.toLine2D(UnitVector.of(2, 0).multiply(RADIUS)));
            graphics.setColor(new Color(0, 255, 0, 255));
            graphics.draw(geometricLayer.toLine2D(UnitVector.of(2, 1).multiply(RADIUS)));
          }
          geometricLayer.popMatrix();
        } else
          System.err.println("lidar pose unavailable");
      }
      GraphicsUtil.setQualityDefault(graphics);
    } else
      System.err.println("time missing");
  }
}
