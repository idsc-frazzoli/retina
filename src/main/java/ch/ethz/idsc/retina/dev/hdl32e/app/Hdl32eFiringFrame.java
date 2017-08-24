// code by jph
package ch.ethz.idsc.retina.dev.hdl32e.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringListener;
import ch.ethz.idsc.retina.util.Stopwatch;
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
  private FiringContainer firingContainer;
  private final Stopwatch stopwatch = new Stopwatch();
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      long period = stopwatch.stop();
      stopwatch.start();
      Graphics2D graphics = (Graphics2D) g;
      Dimension dimension = getSize();
      {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, dimension.width, dimension.height);
        graphics.setColor(Color.GRAY);
      }
      final int midx = dimension.width / 2;
      final int midy = dimension.height / 2;
      FiringContainer ref = firingContainer;
      if (Objects.nonNull(ref)) {
        int count = 0;
        for (int c = 0; c < ref.position.length; c += 3) {
          float x = ref.position[c];
          float y = ref.position[c + 1];
          float z = ref.position[c + 2];
          double alpha = (ref.intensity[count] & 0xff) / 255.0;
          Color color = Hue.of(z, 1, 1, alpha);
          graphics.setColor(color);
          graphics.fill(new Rectangle(Math.round(midx + x * 4), Math.round(midy + y * 4), 1, 1));
          ++count;
        }
        graphics.setColor(Color.GRAY);
        graphics.drawString("" + ref.size(), 0, 10);
      }
      graphics.setColor(Color.RED);
      graphics.drawString(String.format("%4.1f Hz", (1.0e9 / period)), 0, 20);
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
  public void digest(FloatBuffer floatBuffer, ByteBuffer byteBuffer) {
    FiringContainer firingContainer = new FiringContainer();
    firingContainer.position = Arrays.copyOf(floatBuffer.array(), floatBuffer.limit());
    firingContainer.intensity = Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
    this.firingContainer = firingContainer;
    jComponent.repaint();
  }
}
