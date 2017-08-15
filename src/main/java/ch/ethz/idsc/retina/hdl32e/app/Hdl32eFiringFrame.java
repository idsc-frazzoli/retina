// code by jph
package ch.ethz.idsc.retina.hdl32e.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.hdl32e.Hdl32eFiringListener;
import ch.ethz.idsc.tensor.img.Hue;

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
  private int zoom = 0;
  private float[] posCopy = null;
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D graphics = (Graphics2D) g;
      Dimension dimension = getSize();
      final int midx = dimension.width / 2;
      final int midy = dimension.height / 2;
      float[] posRef = posCopy;
      if (Objects.nonNull(posRef)) {
        for (int c = 0; c < posRef.length; c += 3) {
          float x = posRef[c];
          float y = posRef[c + 1];
          float z = posRef[c + 2];
          Color color = Hue.of(z, 1, 1, 1);
          graphics.setColor(color);
          graphics.fill(new Rectangle(Math.round(midx + x * 4), Math.round(midy + y * 4), 1, 1));
        }
        // graphics.drawString("" + posRef.length, 0, 10);
      }
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

  @Override
  public void digest(float[] position_data, int length) {
    posCopy = Arrays.copyOf(position_data, length);
    jComponent.repaint();
  }
}
