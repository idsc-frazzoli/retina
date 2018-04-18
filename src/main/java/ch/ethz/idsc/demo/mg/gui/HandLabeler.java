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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import ch.ethz.idsc.demo.mg.HandLabelFileLocations;
import ch.ethz.idsc.demo.mg.TrackedBlobIO;
import ch.ethz.idsc.demo.mg.pipeline.TrackedBlob;

/** GUI for hand labeling of features. Left click adds a feature, right click deletes most recent feature.
 * Labels can be loaded/saved to a file
 * Filename must have the format imagePrefix_%04dimgNumber_%dtimestamp.fileextension */
// TODO how to specify shape of feature?
public class HandLabeler {
  private final int longAxis = 60; // default feature shape
  private final int shortAxis = 30; // default feature shape
  private final int numberOfFiles = new File(HandLabelFileLocations.Images).list().length;
  private final float scaling = 2;
  private int currentImgNumber;
  private String fileName = "labeledFeatures.dat";
  private String imagePrefix = "dubi8a";
  private int[] timeStamps = new int[numberOfFiles]; // stores timestamp of each image
  private List<List<TrackedBlob>> labeledFeatures = new ArrayList<>(numberOfFiles); // main field of the class
  private final JFrame jFrame = new JFrame();
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
  private JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(bufferedImage, 0, 0, null);
      graphics.drawString("Image number: " + currentImgNumber, 10, 380);
      drawEllipsesOnImage(labeledFeatures.get(currentImgNumber - 1), (Graphics2D) graphics);
    }
  };
  private final MouseWheelListener mwl = new MouseWheelListener() {
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      System.out.println("mousewheel = " + e.getWheelRotation());
    }
  };

  public HandLabeler() {
    // set up empty list of lists
    for (int i = 0; i < numberOfFiles; i++) {
      List<TrackedBlob> emptyList = new ArrayList<>();
      labeledFeatures.add(emptyList);
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
          TrackedBlobIO.saveFeatures(HandLabelFileLocations.Labels + fileName, labeledFeatures);
          System.out.println("Successfully saved to file " + fileName);
        }
      });
      loadButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          labeledFeatures = TrackedBlobIO.loadFeatures(HandLabelFileLocations.Labels + fileName);
          System.out.println("Successfully loaded from file " + fileName);
          jComponent.repaint();
        }
      });
      jPanelTop.add(saveButton);
      jPanelTop.add(loadButton);
      // extract image timestamps
      // slider to slide through all images in directory
      JSlider jSlider = new JSlider(1, numberOfFiles);
      // set up GUI to show first image
      jSlider.setValue(1);
      currentImgNumber = jSlider.getValue();
      extractImageTimestamps();
      displayImage();
      // when slider moves, show updated image
      jSlider.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          currentImgNumber = jSlider.getValue();
          displayImage();
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
          TrackedBlob blob = new TrackedBlob(new float[] { p.x / scaling, p.y / scaling }, new double[][] { { shortAxis, 0 }, { 0, longAxis } },
              timeStamps[currentImgNumber - 1], true);
          labeledFeatures.get(currentImgNumber - 1).add(blob);
        }
        // remove last added label with right click
        if (e.getButton() == MouseEvent.BUTTON3) {
          labeledFeatures.get(currentImgNumber - 1).remove(labeledFeatures.get(currentImgNumber - 1).size() - 1);
        }
        jComponent.repaint();
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        // ---
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        // ---
      }

      @Override
      public void mouseExited(MouseEvent e) {
        // ---
      }
    });
    jComponent.addMouseWheelListener(mwl);
    jPanelMain.add("Center", jComponent);
    jFrame.setContentPane(jPanelMain);
    jFrame.setBounds(100, 100, 500, 440);
    jFrame.setVisible(true);
  }

  public static BufferedImage scaleImage(BufferedImage unscaled, float scale) {
    int newWidth = (int) (unscaled.getWidth() * scale);
    int newHeight = (int) (unscaled.getHeight() * scale);
    BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_INDEXED);
    AffineTransform scaleInstance = AffineTransform.getScaleInstance(scale, scale);
    AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    scaleOp.filter(unscaled, scaled);
    return scaled;
  }

  // display a scaled image according to which position the slider is
  private void displayImage() {
    String imgNumberString = String.format("%04d", currentImgNumber);
    String fileName = imagePrefix +"_"+ imgNumberString + "_" + timeStamps[currentImgNumber - 1] + ".png";
    String pathToFile = HandLabelFileLocations.Images + fileName;
    try {
      BufferedImage unscaled = ImageIO.read(new File(pathToFile));
      bufferedImage = scaleImage(unscaled, scaling);
    } catch (IOException e) {
      e.printStackTrace();
    }
    jComponent.repaint();
  }

  // draw ellipses for image based on list of blobs for the image.
  private void drawEllipsesOnImage(List<TrackedBlob> blobs, Graphics2D graphics) {
    for (int i = 0; i < blobs.size(); i++) {
      Ellipse2D ellipse = new Ellipse2D.Float(blobs.get(i).getPos()[0] * scaling - shortAxis / 2, blobs.get(i).getPos()[1] * scaling - longAxis / 2, shortAxis,
          longAxis);
      graphics.setColor(Color.WHITE);
      graphics.draw(ellipse);
    }
  }

  // goes through all files in the directory an extracts the timestamps
  private void extractImageTimestamps() {
    // get all filenames and sort
    String[] fileNames = new File(HandLabelFileLocations.Images).list();
    Arrays.sort(fileNames);
    for (int i = 0; i < numberOfFiles; i++) {
      String fileName = fileNames[i];
      // remove file extension
      fileName = fileName.substring(0, fileName.lastIndexOf("."));
      String splitFileName[] = fileName.split("_");
      timeStamps[i] = Integer.parseInt(splitFileName[2]);
    }
  }

  public static void main(String[] args) {
    HandLabeler handlabeler = new HandLabeler();
  }
}
