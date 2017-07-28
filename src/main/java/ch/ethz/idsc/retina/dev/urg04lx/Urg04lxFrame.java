// code by jph
package ch.ethz.idsc.retina.dev.urg04lx;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.stream.IntStream;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.util.gui.TensorGraphics;
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
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.Sin;

/** {@link Urg04lxFrame} requires that the binary "urg_provider" is located at
 * /home/{username}/Public/urg_provider
 * 
 * https://sourceforge.net/projects/urgnetwork/files/urg_library/
 * 
 * Quote from datasheet:
 * The light source of the sensor is infrared laser of
 * wavelength 785nm with laser class 1 safety
 * Max. Distance: 4000[mm]
 * 
 * The sensor is designed for indoor use only.
 * The sensor is not a safety device/tool.
 * The sensor is not for use in military applications.
 * 
 * typically the distances up to 5[m] can be measured correctly. */
public class Urg04lxFrame implements Urg04lxListener {
  private static final Scalar MILLIMETER_TO_METER = RealScalar.of(0.001);
  public static final double METER_TO_PIXEL = 100; // [m] to [pixel]
  /** points closer than 2[cm] == 0.02[m] are discarded */
  public static final Scalar THRESHOLD = RealScalar.of(0.02); // [m]
  public static final Scalar RAMERDOUGLASPEUKER = RealScalar.of(0.05); // 5[cm] == 0.05[m]
  private static final int INDEX_LAST = 681;
  // ---
  /** p.2 Detection Area: 240 [deg] */
  private final Tensor angle = Subdivide.of(-120 * Math.PI / 180, 120 * Math.PI / 180, INDEX_LAST).unmodifiable();
  private final Tensor direction;
  private final Tensor gridlines = Tensors.empty();
  /** range contains distances in [mm] for 682 angles TODO confirm units */
  private Tensor range = Tensors.empty();
  public final JFrame jFrame = new JFrame();
  JComponent jComponent = new JComponent() {
    final Color BLIND_SPOT = new Color(30, 30, 30, 64);
    private int ofs_x;
    private int ofs_y;

    private Point2D toPoint(Tensor dir) {
      return new Point2D.Double( //
          ofs_x - dir.Get(1).number().doubleValue() * METER_TO_PIXEL, //
          ofs_y - dir.Get(0).number().doubleValue() * METER_TO_PIXEL);
    }

    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D graphics = (Graphics2D) g;
      { // set center of sensor in window
        Dimension dimension = getSize();
        ofs_x = dimension.width / 2;
        ofs_y = dimension.height * 3 / 4;
      }
      { // show blind spot
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
              .boxed().map(points::get));
          // ---
          graphics.setColor(new Color(0, 128 + 64, 128, 255));
          graphics.draw(TensorGraphics.polygonToPath( //
              RamerDouglasPeucker.of(contour, RAMERDOUGLASPEUKER), this::toPoint));
          // ---
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
  };

  public Urg04lxFrame(Urg04lxProvider urgProvider) {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 800, 800);
    jFrame.setContentPane(jComponent);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        urgProvider.stop();
      }
    });
    direction = Transpose.of(Tensors.of(Cos.of(angle), Sin.of(angle)));
    for (Tensor radius : Tensors.vector(1, 2, 3, 4, 5))
      gridlines.append(direction.multiply(radius.Get()));
    jFrame.setVisible(true);
  }

  @Override
  public void urg(String line) {
    range = Tensors.fromString(line.substring(3)).multiply(MILLIMETER_TO_METER); // <- removes "URG" prefix from line
    jComponent.repaint();
  }
}
