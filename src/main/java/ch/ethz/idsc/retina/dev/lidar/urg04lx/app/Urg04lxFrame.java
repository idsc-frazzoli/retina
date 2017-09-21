// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.FloatBuffer;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxRangeEvent;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxRangeListener;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.retina.util.math.UniformResample;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Round;

/** {@link Urg04lxFrame} requires that the binary "urg_provider" is located at
 * /home/{username}/Public/urg_provider
 * 
 * https://sourceforge.net/projects/urgnetwork/files/urg_library/
 * 
 * Quote from datasheet: The light source of the sensor is infrared laser of
 * wavelength 785nm with laser class 1 safety Max. Distance: 4000[mm]
 * 
 * The sensor is designed for indoor use only. The sensor is not a safety
 * device/tool. The sensor is not for use in military applications.
 * 
 * typically the distances up to 5[m] can be measured correctly. */
public class Urg04lxFrame implements Urg04lxRangeListener, LidarRayBlockListener {
  public final JFrame jFrame = new JFrame();
  private final Urg04lxRender urg04lxRender = new Urg04lxRender();
  private int zoom = 0;
  private Scalar threshold = RealScalar.of(30);
  private Scalar ds_value = RealScalar.of(0.03);
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      urg04lxRender.render((Graphics2D) g, getSize());
    }
  };

  public Urg04lxFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 600, 600);
    {
      JPanel jPanel = new JPanel(new BorderLayout());
      {
        JToolBar jToolBar = new JToolBar();
        jToolBar.setFloatable(false);
        jToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
        {
          SpinnerLabel<Scalar> spinnerLabel = new SpinnerLabel<>();
          spinnerLabel.setStream(Subdivide.of(10, 200, 19).stream().map(Scalar.class::cast));
          spinnerLabel.setIndex(2);
          spinnerLabel.addSpinnerListener(scalar -> threshold = N.DOUBLE.of(scalar));
          spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "ds");
        }
        {
          SpinnerLabel<Scalar> spinnerLabel = new SpinnerLabel<>();
          spinnerLabel.setStream(Subdivide.of(0.01, 0.1, 9).map(Round._2).stream().map(Scalar.class::cast));
          spinnerLabel.setIndex(2);
          spinnerLabel.addSpinnerListener(scalar -> ds_value = N.DOUBLE.of(scalar));
          spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "ds");
        }
        jPanel.add(jToolBar, BorderLayout.NORTH);
      }
      jPanel.add(jComponent, BorderLayout.CENTER);
      jFrame.setContentPane(jPanel);
    }
    jComponent.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        zoom -= mouseWheelEvent.getWheelRotation();
        urg04lxRender.setZoom(zoom);
        System.out.println(zoom);
      }
    });
    jFrame.setVisible(true);
  }

  @Override
  public void range(Urg04lxRangeEvent urg04lxRangeEvent) {
    urg04lxRender.setEvent(urg04lxRangeEvent);
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    // int limit = floatBuffer.limit();
    Tensor points = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    List<Tensor> result = new UniformResample(threshold, ds_value).apply(points);
    System.out.println(points.length() + " -> blocks = " + result.size());
    urg04lxRender.setPointcloud(result);
    jComponent.repaint();
  }
}
