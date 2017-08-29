// code by jph
package ch.ethz.idsc.retina.dev.urg04lxug01;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.util.gui.TensorGraphics;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.RamerDouglasPeucker;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.Sin;

public class Urg04lxRender {
  private static final double MILLIMETER_TO_METER = 0.001;
  /** points closer than 2[cm] == 0.02[m] are discarded */
  public static final Scalar THRESHOLD = RealScalar.of(0.02); // [m]
  public static final Scalar RAMERDOUGLASPEUKER = RealScalar.of(0.05); // 5[cm] == 0.05[m]
  private static final Color BLIND_SPOT = new Color(230, 30, 30, 64);
  private static final int INDEX_LAST = 681;
  // ---
  /** p.2 Detection Area: 240 [deg] */
  private final Tensor angle = Subdivide.of(-120 * Math.PI / 180, 120 * Math.PI / 180, INDEX_LAST).unmodifiable();
  private final Tensor direction;
  private final Tensor gridlines = Tensors.empty();
  /** range contains distances in [mm] for 682 angles */
  private Tensor _range = Tensors.empty();
  private Scalar METER_TO_PIXEL; // [m] to [pixel]
  private int ofs_x;
  private int ofs_y;

  public Urg04lxRender() {
    direction = Transpose.of(Tensors.of(Cos.of(angle), Sin.of(angle)));
    for (Tensor radius : Tensors.vector(1, 2, 3, 4, 5))
      gridlines.append(direction.multiply(radius.Get()));
    setZoom(0);
  }

  private Point2D toPoint(Tensor dir) {
    dir = dir.multiply(METER_TO_PIXEL);
    return new Point2D.Double( //
        ofs_x + dir.Get(0).number().doubleValue(), //
        ofs_y - dir.Get(1).number().doubleValue());
  }

  public void render(Graphics2D graphics, Dimension dimension) {
    Tensor range = _range.copy();
    {
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, dimension.width, dimension.height);
    }
    { // set center of sensor in window
      ofs_x = dimension.width * 1 / 4;
      ofs_y = dimension.height / 2;
    }
    { // show blind spot to the rear
      Path2D path2d = TensorGraphics.polygonToPath( //
          Tensors.of(Tensors.vector(0, 0), direction.get(0), direction.get(INDEX_LAST)) //
          , this::toPoint);
      graphics.setColor(new Color(64, 64, 64, 64));
      graphics.fill(path2d);
    }
    { // draw distance lines with 1[m] spacing
      graphics.setColor(new Color(64, 128, 64, 128));
      Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 5 }, 0);
      graphics.setStroke(dashed);
      for (Tensor swipe : gridlines)
        graphics.draw(TensorGraphics.polygonToPath(swipe, this::toPoint));
      graphics.setStroke(new BasicStroke());
    }
    {
      for (int index = 0; index < range.length(); ++index) {
        final Tensor rotation = direction.get(index);
        if (Scalars.lessThan(range.Get(index), THRESHOLD)) {
          graphics.setColor(BLIND_SPOT);
          Point2D p1 = toPoint(rotation.multiply(RealScalar.of(index % 10 == 0 ? 1 : 2)));
          Point2D p2 = toPoint(rotation.multiply(RealScalar.of(2.05)));
          graphics.draw(new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY()));
        } else {
          Tensor dir = rotation.multiply(range.Get(index));
          final Point2D point = toPoint(dir);
          Shape shape = new Rectangle2D.Double(point.getX(), point.getY(), 2, 2);
          graphics.setColor(new Color(128, 128, 128, 128));
          graphics.fill(shape);
          if (index == range.length() / 2) {
            graphics.setColor(Color.BLACK);
            graphics.drawString( //
                range.Get(index).map(Round._3).toString(), (int) point.getX(), (int) point.getY());
          }
        }
      }
      if (0 < range.length()) {
        Tensor points = range.pmul(direction);
        Tensor contour = Tensor.of(IntStream.range(0, range.length()) //
            .filter(index -> Scalars.lessThan(THRESHOLD, range.Get(index))) //
            .mapToObj(points::get));
        // ---
        graphics.setColor(new Color(0, 128 + 64, 128, 255));
        try {
          Tensor path = RamerDouglasPeucker.of(contour, RAMERDOUGLASPEUKER);
          graphics.draw(TensorGraphics.polygonToPath(path, this::toPoint));
        } catch (Exception exception) {
          System.err.println("nono");
          // ---
        }
        contour.append(Array.zeros(2));
        graphics.setColor(new Color(128, 128 + 64, 128, 64));
        graphics.fill(TensorGraphics.polygonToPath(contour, this::toPoint));
      }
    }
    try {
      // display min max range in sensor data
      Scalar min = range.flatten(-1).map(Scalar.class::cast).filter(Scalars::nonZero).reduce(Min::of).get();
      Scalar max = range.flatten(-1).map(Scalar.class::cast).reduce(Max::of).get();
      graphics.setColor(Color.BLACK);
      graphics.drawString(Tensors.of(min, max).map(Round._3).toString() + "[m]", 10, 10);
    } catch (Exception exception) {
      // ---
    }
  }

  public void setEvent(Urg04lxEvent urg04lxEvent) {
    _range = Tensors.empty();
    for (int count = 0; count < urg04lxEvent.range.length; ++count)
      _range.append(DoubleScalar.of(urg04lxEvent.range[count] * MILLIMETER_TO_METER));
  }

  public void setZoom(int zoom) {
    METER_TO_PIXEL = Power.of(4 / 3.0, zoom).multiply(RealScalar.of(100));
  }
}
