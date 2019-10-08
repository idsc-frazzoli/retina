// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class LaptimeRender implements RenderInterface {
  private static final Tensor CIRCLE = CirclePoints.of(13).multiply(RealScalar.of(0.1));
  // ---
  private final BSplineTrack bSplineTrack;
  private final List<LaptimePoint> list = new LinkedList<>();
  private Scalar ever = Quantity.of(DoubleScalar.POSITIVE_INFINITY, SI.SECOND);
  // ---
  private Scalar pathProgress = RealScalar.of(-1);

  public LaptimeRender(BSplineTrack bSplineTrack) {
    this.bSplineTrack = bSplineTrack;
    int length = bSplineTrack.combinedControlPoints().length();
    for (int index = 0; index < length; ++index) {
      Scalar s = RealScalar.of(index);
      LaptimePoint laptimePoint = new LaptimePoint(s, bSplineTrack.getPositionXY(s), length);
      list.add(laptimePoint);
    }
  }

  public void setPose(Scalar time, Tensor pose) {
    pathProgress = bSplineTrack.getNearestPathProgress(pose.extract(0, 2));
    list.forEach(laptimePoint -> laptimePoint.digest(time, pathProgress));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Sign.isPositiveOrZero(pathProgress)) {
      GraphicsUtil.setQualityHigh(graphics);
      // {
      // Tensor positionXY = bSplineTrack.getPositionXY(pathProgress);
      // geometricLayer.pushMatrix(Se2Matrix.translation(positionXY.map(Magnitude.METER)));
      // Path2D path2d = geometricLayer.toPath2D(CIRCLE, true);
      // graphics.setColor(Color.BLUE);
      // graphics.draw(path2d);
      // geometricLayer.popMatrix();
      // }
      // ---
      graphics.setColor(new Color(128, 128, 128, 128));
      Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);
      graphics.setFont(font);
      FontMetrics fontMetrics = graphics.getFontMetrics();
      for (LaptimePoint laptimePoint : list)
        if (NumberQ.of(Magnitude.SECOND.apply(laptimePoint.lap()))) {
          Point2D point2d = geometricLayer.toPoint2D(laptimePoint.position.map(Magnitude.METER));
          String string = "" + laptimePoint.lap().map(Round._2);
          int stringWidth = fontMetrics.stringWidth(string);
          graphics.drawString(string, (int) point2d.getX() - stringWidth / 2, (int) point2d.getY());
        }
      // ---
      {
        int pix = 1600;
        Scalar best = list.stream().map(LaptimePoint::lap).reduce(Min::of).get();
        graphics.setColor(Color.GRAY);
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        graphics.drawString("low: " + best.map(Round._3), pix, 25);
        ever = Min.of(best, ever);
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawString("min: " + ever.map(Round._3), pix, 25 + 30);
      }
      GraphicsUtil.setQualityDefault(graphics);
    }
  }
}
