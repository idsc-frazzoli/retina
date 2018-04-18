// code by jph
package ch.ethz.idsc.demo.mg;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** demo of gui elements and mouse input */
class ImageDisplayFrame {
  private final JFrame jFrame = new JFrame();
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
  private JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(bufferedImage, 0, 0, null);
      // System.out.println("repaint");
    }
  };

  public ImageDisplayFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JPanel jPanelMain = new JPanel(new BorderLayout());
    {
      JPanel jPanelTop = new JPanel(new BorderLayout());
      JButton jButton = new JButton("button");
      jButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          System.out.println("button pressed");
        }
      });
      jPanelTop.add("West", jButton);
      JSlider jSlider = new JSlider(0, 100);
      jSlider.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          System.out.println("change " + jSlider.getValue());
        }
      });
      jPanelTop.add("Center", jSlider);
      jPanelMain.add("North", jPanelTop);
    }
    jComponent.addMouseListener(new MouseListener() {
      @Override
      public void mouseReleased(MouseEvent e) {
        // ---
      }

      @Override
      public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        System.out.println("mouse pressed at " + p);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        // ---
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        // ---
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        // ---
      }
    });
    jComponent.addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseMoved(MouseEvent e) {
        // ---
      }

      @Override
      public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        System.out.println("mouse dragged " + p);
      }
    });
    jPanelMain.add("Center", jComponent);
    jFrame.setContentPane(jPanelMain);
    jFrame.setBounds(100, 100, 400, 400);
    jFrame.setVisible(true);
  }

  public void setImage(BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
    jComponent.repaint();
  }

  public static void main(String[] args) throws InterruptedException {
    ImageDisplayFrame imageDisplayFrame = new ImageDisplayFrame();
    int count = 0;
    while (imageDisplayFrame.jFrame.isVisible()) {
      BufferedImage bufferedImage = ImageSynthAndExportDemo.createImage((count * 10) % 240);
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.drawString("" + count, 10, 10);
      imageDisplayFrame.setImage(bufferedImage);
      Thread.sleep(500);
      ++count;
    }
  }
}
