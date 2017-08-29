// code by jph
package ch.ethz.idsc.retina.dev.urg04lxug01;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

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
public class Urg04lxFrame implements Urg04lxEventListener {
  public final JFrame jFrame = new JFrame();
  private final Urg04lxRender urg04lxRender = new Urg04lxRender();
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
    jFrame.setContentPane(jComponent);
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
  public void range(Urg04lxEvent urg04lxEvent) {
    urg04lxRender.setEvent(urg04lxEvent);
    jComponent.repaint();
  }
}
