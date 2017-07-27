// code by jph
package ch.ethz.idsc.retina.dev.urg04lx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;

/** {@link UrgFrame} requires that the binary "urg_provider" is located at
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
public class UrgFrame implements UrgListener {
  public static final double SCALE = 0.15;
  public static final Scalar THRESHOLD = RealScalar.of(30); // [mm]
  // ---
  /** p.2 Detection Area: 240 [deg] */
  Tensor alpha = Subdivide.of(-120 * Math.PI / 180, 120 * Math.PI / 180, 681).unmodifiable();
  /** range contains distances in [mm] for 682 angles TODO confirm units */
  Tensor range = Tensors.empty();

  static Point2D toPoint(Tensor dir) {
    return new Point2D.Double( //
        700 + dir.Get(1).number().doubleValue() * SCALE, //
        200 + dir.Get(0).number().doubleValue() * SCALE);
  }

  JFrame jFrame = new JFrame();
  JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D graphics = (Graphics2D) g;
      {
        Path2D path2d = new Path2D.Double();
        {
          Point2D point2d = toPoint(Tensors.vector(0, 0));
          path2d.moveTo(point2d.getX(), point2d.getY());
        }
        for (int index = 0; index < range.length(); ++index) {
          final Tensor rotation = RotationMatrix.of(alpha.Get(index)).get(Tensor.ALL, 0);
          if (Scalars.lessThan(range.Get(index), THRESHOLD)) {
            graphics.setColor(new Color(30, 30, 30, 128));
            Point2D p1 = toPoint(rotation.multiply(RealScalar.of(2000)));
            Point2D p2 = toPoint(rotation.multiply(RealScalar.of(2050)));
            graphics.draw(new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY()));
          } else {
            Tensor dir = rotation.multiply(range.Get(index));
            final Point2D point = toPoint(dir);
            path2d.lineTo(point.getX(), point.getY());
            Shape shape = new Rectangle2D.Double(point.getX(), point.getY(), 2, 2);
            graphics.setColor(new Color(128, 128, 128, 128));
            graphics.fill(shape);
            if (index == range.length() / 2) {
              graphics.setColor(Color.BLACK);
              graphics.drawString("" + range.Get(index), (int) point.getX(), (int) point.getY());
            }
          }
        }
        graphics.setColor(new Color(128, 128 + 64, 128, 128));
        graphics.fill(path2d);
      }
      {
        Path2D path2d = new Path2D.Double();
        {
          Point2D point2d = toPoint(Tensors.vector(0, 0));
          path2d.moveTo(point2d.getX(), point2d.getY());
        }
        for (int index : new int[] { 0, alpha.length() - 1 }) {
          Tensor dir = RotationMatrix.of(alpha.Get(index)).get(Tensor.ALL, 0).multiply(RealScalar.of(1000));
          Point2D p = toPoint(dir);
          path2d.lineTo(p.getX(), p.getY());
        }
        graphics.setColor(new Color(255, 128, 128, 128));
        graphics.fill(path2d);
      }
      try {
        Scalar min = range.flatten(-1).map(Scalar.class::cast).filter(Scalars::nonZero).reduce(Min::of).get();
        Scalar max = range.flatten(-1).map(Scalar.class::cast).filter(Scalars::nonZero).reduce(Max::of).get();
        graphics.setColor(Color.BLACK);
        graphics.drawString("" + min + " " + max, 10, 10);
      } catch (Exception exception) {
        // ---
      }
    }
  };

  public UrgFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 800, 800);
    jFrame.setContentPane(jComponent);
    jFrame.setVisible(true);
  }

  @Override
  public void urg(String line) {
    range = Tensors.fromString(line.substring(3)); // <- removes "URG" prefix from line
    jComponent.repaint();
  }

  public static void main(String[] args) {
    UrgFrame urgFrame = new UrgFrame();
    urgFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        LiveUrgProvider.INSTANCE.stop();
      }
    });
    LiveUrgProvider.INSTANCE.listeners.add(urgFrame);
    LiveUrgProvider.INSTANCE.start();
  }
}
