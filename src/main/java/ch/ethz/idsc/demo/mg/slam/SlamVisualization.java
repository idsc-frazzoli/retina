// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

// similar to pipelineVisualization. Provides a live update of SlamMapFrame
// TODO probably create abstract visualization class and then extend Slamvisualization and PipelineVisualization?
public class SlamVisualization {
  private final JFrame jFrame = new JFrame();
  private final BufferedImage[] bufferedImage = new BufferedImage[3];
  private final JComponent jComponent = new JComponent() {
    
    @Override
    protected void paintComponent(Graphics graphics) {
    // draw the three maps from the slam algorithm
    }
  };
  
  public SlamVisualization() {
    bufferedImage[0] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    bufferedImage[1] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    bufferedImage[2] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    // ...
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(jComponent);
    jFrame.setBounds(100, 100, 1050, 950);
    jFrame.setVisible(true);
  }
  
  public void setFrame(BufferedImage bufferedImage) {
    // set bufferedImages 
    jComponent.repaint();
  }
}
