// code by mg
package ch.ethz.idsc.demo.mg.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.ethz.idsc.demo.mg.ImageFileLocations;
import ch.ethz.idsc.demo.mg.pipeline.TrackedBlob;

// gui to label images that show accumulated events
class HandLabeler {
  private final JFrame jFrame = new JFrame();
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
  private JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawString("Image number: " + currentImgNumber, 10, 380);
      graphics.drawImage(bufferedImage, 0, 0, null);
      drawEllipses(labeledBlobs.get(currentImgNumber), (Graphics2D) graphics);
    }
  };
  private final int numberOfFiles = new File(ImageFileLocations.Test).list().length;
  private int currentImgNumber;
  private List<List<TrackedBlob>> labeledBlobs = new ArrayList<List<TrackedBlob>>(numberOfFiles);

  public HandLabeler() {
    // set up list of list TODO: this could be somewhere nicer
    for (int i = 0; i < numberOfFiles; i++) {
      List<TrackedBlob> emptyList = new ArrayList<TrackedBlob>();
      labeledBlobs.add(emptyList);
    }
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JPanel jPanelMain = new JPanel(new BorderLayout());
    {
      JPanel jPanelTop = new JPanel();
      JButton saveButton = new JButton("save labels");
      JButton loadButton = new JButton("load labels");
      saveButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          saveToFile(ImageFileLocations.Test);
        }
      });
      loadButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          loadFromFile(ImageFileLocations.Test);
        }
      });
      jPanelTop.add(saveButton);
      jPanelTop.add(loadButton);
      // slider to slide through all images in directory
      JSlider jSlider = new JSlider(1, numberOfFiles);
      jSlider.setValue(1);
      // show first image
      setImage(ImageFileLocations.Test + "test0001.png");
      currentImgNumber = jSlider.getValue();
      jSlider.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          currentImgNumber = jSlider.getValue();
          String numberString = String.format("%04d", currentImgNumber);
          setImage(ImageFileLocations.Test + "test" + numberString + ".png");
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
        // add labels with left click
        if (e.getButton() == MouseEvent.BUTTON1) {
          Point p = e.getPoint();
          TrackedBlob blob = new TrackedBlob(new float[] { p.x, p.y }, new double[][] { { 30, 0 }, { 0, 60 } }, true);
          labeledBlobs.get(currentImgNumber).add(blob);
        }
        // remove labels with right click
        if (e.getButton() == MouseEvent.BUTTON3) {
          labeledBlobs.get(currentImgNumber).remove(labeledBlobs.get(currentImgNumber).size() - 1);
        }
        jComponent.repaint();
      }

      @Override
      public void mouseClicked(MouseEvent e) {
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }
    });
    jComponent.addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseMoved(MouseEvent e) {
      }

      @Override
      public void mouseDragged(MouseEvent e) {
      }
    });
    jPanelMain.add("Center", jComponent);
    jFrame.setContentPane(jPanelMain);
    jFrame.setBounds(100, 100, 500, 440);
    jFrame.setVisible(true);
  }

  private void setImage(String pathToFile) {
    try {
      BufferedImage unscaled = ImageIO.read(new File(pathToFile));
      bufferedImage = scaleImage(unscaled, 2);
    } catch (IOException e) {
      e.printStackTrace();
    }
    jComponent.repaint();
  }

  private void drawEllipses(List<TrackedBlob> blobs, Graphics2D graphics) {
    for (int i = 0; i < blobs.size(); i++) {
      Ellipse2D ellipse = new Ellipse2D.Float(blobs.get(i).getPos()[0] - 15, blobs.get(i).getPos()[1] - 30, 30, 60);
      graphics.setColor(Color.WHITE);
      graphics.draw(ellipse);
    }
  }

  private void saveToFile(String pathToFile) {
    File temp = new File(ImageFileLocations.Test);
    try {
      FileOutputStream fos = new FileOutputStream(temp);
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  private void loadFromFile(String pathToFile) {
  }

  private BufferedImage scaleImage(BufferedImage unscaled, int scale) {
    int newWidth = unscaled.getWidth() * scale;
    int newHeight = unscaled.getHeight() * scale;
    BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
    AffineTransform scaleInstance = AffineTransform.getScaleInstance(scale, scale);
    AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);
    scaleOp.filter(unscaled, scaled);
    return scaled;
  }

  public static void main(String[] args) throws InterruptedException {
    HandLabeler handlabeler = new HandLabeler();
  }
}
