// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/** {@link Hdl32eFiringFrame} requires that the binary "urg_provider" is located at
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
public class Hdl32eFiringFrame implements Hdl32eFiringListener {
  public final JFrame jFrame = new JFrame();
  // private final Urg04lxRender urg04lxRender = new Urg04lxRender();
  private int zoom = 0;
  private float[] posCopy;
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D graphics = (Graphics2D) g;
      float[] posRef = posCopy;
      {
        for (int c = 0; c < posRef.length; c += 3) {
          float x = posRef[c];
          float y = posRef[c + 1];
          graphics.fill(new Rectangle(Math.round(300 + x), Math.round(300 + y), 2, 2));
        }
      }
      // urg04lxRender.render((Graphics2D) g, getSize());
    }
  };

  public Hdl32eFiringFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 600, 600);
    jFrame.setContentPane(jComponent);
    jComponent.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        zoom -= mouseWheelEvent.getWheelRotation();
        // urg04lxRender.setZoom(zoom);
        System.out.println(zoom);
      }
    });
    jFrame.setVisible(true);
  }
  // @Override
  // public void urg(String line) {
  // urg04lxRender.setLine(line);
  // jComponent.repaint();
  // }

  @Override
  public void digest(float[] position_data, int length) {
    posCopy = Arrays.copyOf(position_data, length);
    // System.out.println(position_data.length);
    jComponent.repaint();
  }
}
