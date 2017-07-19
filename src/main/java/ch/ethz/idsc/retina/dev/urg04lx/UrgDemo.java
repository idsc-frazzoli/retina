// code by jph
package ch.ethz.idsc.retina.dev.urg04lx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.RotationMatrix;

/** https://sourceforge.net/projects/urgnetwork/files/urg_library/ */
public class UrgDemo {
  public static final double SCALE = 0.05;
  // ---
  Tensor alpha = Subdivide.of(-170 * Math.PI / 180, 170 * Math.PI / 180, 681).unmodifiable();
  Tensor range = Tensors.empty();

  static Point2D toPoint(Tensor dir) {
    return new Point2D.Double( //
        300 - dir.Get(1).number().doubleValue() * SCALE, //
        300 - dir.Get(0).number().doubleValue() * SCALE);
  }

  JFrame jFrame = new JFrame();
  JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D graphics = (Graphics2D) g;
      {
        graphics.setColor(new Color(128, 128, 128, 128));
        Path2D path2d = new Path2D.Double();
        {
          Point2D point2d = toPoint(Tensors.vector(0, 0));
          path2d.moveTo(point2d.getX(), point2d.getY());
        }
        for (int index = 0; index < range.length(); ++index)
          if (Scalars.nonZero(range.Get(index))) {
            Tensor dir = RotationMatrix.of(alpha.Get(index)).get(Tensor.ALL, 0).multiply(range.Get(index));
            Point2D p = toPoint(dir);
            path2d.lineTo(p.getX(), p.getY());
            Shape shape = new Rectangle2D.Double(p.getX(), p.getY(), 2, 2);
            graphics.fill(shape);
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
    }
  };

  public UrgDemo() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 600, 600);
    jFrame.setContentPane(jComponent);
    jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    File dir = new File("/home/datahaki/3rdparty/urg_library-1.2.0/samples/c");
    ProcessBuilder processBuilder = //
        new ProcessBuilder(new File(dir, "urg_provider").toString());
    processBuilder.directory(dir);
    try {
      Process process = processBuilder.start();
      OutputStream outputStream = process.getOutputStream();
      InputStream inputStream = process.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      UrgDemo urg = new UrgDemo();
      urg.jFrame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent windowEvent) {
          try {
            outputStream.write("EXIT\n".getBytes());
            outputStream.flush();
            System.out.println("sent EXIT");
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      });
      while (process.isAlive()) {
        String line = bufferedReader.readLine();
        if (line != null) {
          if (line.startsWith("URG{"))
            urg.repaint(line);
        } else
          Thread.sleep(1);
      }
      System.out.println("urg process terminated");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void repaint(String line) {
    range = Tensors.fromString(line.substring(3));
    jComponent.repaint();
  }
}
