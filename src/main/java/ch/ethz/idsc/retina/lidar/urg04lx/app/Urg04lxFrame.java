// code by jph
package ch.ethz.idsc.retina.lidar.urg04lx.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.tensor.Scalar;
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
public class Urg04lxFrame {
  private final Timer timer = new Timer();
  // ---
  public final JFrame jFrame = new JFrame();
  public final Urg04lxRender urg04lxRender = new Urg04lxRender();
  private int zoom = 0;
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
          spinnerLabel.addSpinnerListener(scalar -> urg04lxRender.threshold = N.DOUBLE.of(scalar));
          spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "ds");
        }
        {
          SpinnerLabel<Scalar> spinnerLabel = new SpinnerLabel<>();
          spinnerLabel.setStream(Subdivide.of(0.01, 0.1, 9).map(Round._2).stream().map(Scalar.class::cast));
          spinnerLabel.setIndex(2);
          spinnerLabel.addSpinnerListener(scalar -> urg04lxRender.ds_value = N.DOUBLE.of(scalar));
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
    { // periodic task for rendering
      final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
          jComponent.repaint();
        }
      };
      timer.schedule(timerTask, 100, 50);
    }
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        timer.cancel();
      }
    });
    jFrame.setVisible(true);
  }
}
