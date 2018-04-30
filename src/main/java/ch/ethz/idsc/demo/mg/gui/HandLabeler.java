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
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
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
import ch.ethz.idsc.demo.mg.pipeline.ImageBlob;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.Primitives;

/** GUI for hand labeling of features. Left click adds a feature, right click deletes most recent feature.
 * scrolling while holding ctrl/shift changes x/y-axis length.
 * Labels can be loaded/saved to a file
 * Filename must have the format imagePrefix_%04dimgNumber_%dtimestamp.fileextension */
// the .CSV file is formatted as follows:
// timestamp , pos[0], pos[1], covariance[0][0], covariance[1][1], covariance[0][1]
// TODO implement ability to rotate ellipse (method stub set up in ImageBlob)
public class HandLabeler {
  private final int initXAxis = 400; // initial feature shape
  private final int initYAxis = initXAxis; // initial feature shape
  private final int numberOfFiles = HandLabelFileLocations.images().list().length;
  private final float scaling = 2; // original images are tiny
  private int firstAxis = initXAxis;
  private int secondAxis = initYAxis;
  private float rotAngle = 0;
  // handling of .csv file
  private String imagePrefix = "Dubi9e";
  private String fileName = imagePrefix + "_labeledFeatures.csv";
  private static final String COMMA_DELIMITER = ",";
  private static final String NEW_LINE = "\n";
  // fields for labels
  private int[] timeStamps = new int[numberOfFiles]; // stores timestamp of each image
  private List<List<ImageBlob>> labeledFeatures = new ArrayList<>(numberOfFiles); // main field of the class
  private int currentImgNumber;
  // visualization
  private final JFrame jFrame = new JFrame();
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
  private JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      setBufferedImage();
      drawEllipsesOnImage(labeledFeatures.get(currentImgNumber - 1), bufferedImage.createGraphics());
      graphics.drawImage(scaleImage(bufferedImage, scaling), 0, 0, null);
      graphics.drawString("Image number: " + currentImgNumber, 10, 380);
    }
  };
  private final MouseWheelListener mwl = new MouseWheelListener() {
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      if (e.isControlDown()) {
        firstAxis += 20 * e.getWheelRotation();
        if (firstAxis < 0) {
          firstAxis = 0;
        }
      }
      if (e.isShiftDown()) {
        secondAxis += 20 * e.getWheelRotation();
        if (secondAxis < 0) {
          secondAxis = 0;
        }
      }
      if (e.isAltDown()) {
        rotAngle += 0.1 * e.getWheelRotation();
      }
      double[][] updatedCov = new double[][] { { firstAxis, 0 }, { 0, secondAxis } };
      // change shape of most recent feature
      labeledFeatures.get(currentImgNumber - 1).get(labeledFeatures.get(currentImgNumber - 1).size() - 1).setCovariance(updatedCov);
      jComponent.repaint();
    }
  };

  public HandLabeler() {
    // set up empty list of lists
    for (int i = 0; i < numberOfFiles; i++) {
      List<ImageBlob> emptyList = new ArrayList<>();
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
          saveToCSV(HandLabelFileLocations.labels(fileName), labeledFeatures);
          System.out.println("Successfully saved to file " + fileName);
        }
      });
      loadButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          labeledFeatures = loadFromCSV(HandLabelFileLocations.labels(fileName), timeStamps);
          System.out.println("Successfully loaded from file " + fileName);
          // repaint such that saved blobs of current image are displayed
          jComponent.repaint();
        }
      });
      jPanelTop.add(saveButton);
      jPanelTop.add(loadButton);
      // slider to slide through all images in directory
      JSlider jSlider = new JSlider(1, numberOfFiles);
      // set up GUI to show first image
      jSlider.setValue(1);
      currentImgNumber = jSlider.getValue();
      // extract all timestamps
      extractImageTimestamps();
      // when slider moves, show updated image
      jSlider.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          currentImgNumber = jSlider.getValue();
          setBufferedImage();
          jComponent.repaint();
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
          // point coordinates need to be scaled back since we click on a scaled image
          ImageBlob blob = new ImageBlob(new float[] { p.x / scaling, p.y / scaling }, new double[][] { { initXAxis, 0 }, { 0, initYAxis } },
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
    jFrame.setBounds(100, 100, 480, 440);
    jFrame.setVisible(true);
  }

  // scale image for better visualization - static because it is used by all visualization tools
  public static BufferedImage scaleImage(BufferedImage unscaled, float scale) {
    int newWidth = (int) (unscaled.getWidth() * scale);
    int newHeight = (int) (unscaled.getHeight() * scale);
    BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_INDEXED);
    AffineTransform scaleInstance = AffineTransform.getScaleInstance(scale, scale);
    AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    scaleOp.filter(unscaled, scaled);
    return scaled;
  }

  // set the bufferedImage field according to the currentImgNumber
  private void setBufferedImage() {
    String imgNumberString = String.format("%04d", currentImgNumber);
    String fileName = imagePrefix + "_" + imgNumberString + "_" + timeStamps[currentImgNumber - 1] + ".png";
    File pathToFile = new File(HandLabelFileLocations.images(), fileName);
    try {
      bufferedImage = ImageIO.read(pathToFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // draw ellipses for image based on list of blobs for the image.
  private void drawEllipsesOnImage(List<ImageBlob> blobs, Graphics2D graphics) {
    for (int i = 0; i < blobs.size(); i++) {
      AccumulatedEventFrame.drawImageBlob(graphics, blobs.get(i), Color.WHITE);
    }
  }

  // goes through all files in the directory an extracts the timestamps
  private void extractImageTimestamps() {
    // get all filenames and sort
    String[] fileNames = HandLabelFileLocations.images().list();
    Arrays.sort(fileNames);
    for (int i = 0; i < numberOfFiles; i++) {
      String fileName = fileNames[i];
      // remove file extension
      fileName = fileName.substring(0, fileName.lastIndexOf("."));
      String splitFileName[] = fileName.split("_");
      timeStamps[i] = Integer.parseInt(splitFileName[2]);
    }
  }

  // saves labeledFeatures in a .CSV file
  private void saveToCSV(File file, List<List<ImageBlob>> labeledFeatures) {
    FileWriter writer = null;
    try {
      writer = new FileWriter(file);
      for (int i = 0; i < labeledFeatures.size(); i++) {
        for (int j = 0; j < labeledFeatures.get(i).size(); j++) {
          writer.append(String.valueOf(timeStamps[i]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(labeledFeatures.get(i).get(j).getPos()[0]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(labeledFeatures.get(i).get(j).getPos()[1]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(labeledFeatures.get(i).get(j).getCovariance()[0][0]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(labeledFeatures.get(i).get(j).getCovariance()[1][1]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(labeledFeatures.get(i).get(j).getCovariance()[1][0]));
          writer.append(NEW_LINE);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.flush();
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // loads labeledFeatures from .CSV file
  public static List<List<ImageBlob>> loadFromCSV(File file, int[] timeStamps) {
    // set up empty list
    List<List<ImageBlob>> extractedFeatures = new ArrayList<>(timeStamps.length);
    for (int i = 0; i < timeStamps.length; i++) {
      List<ImageBlob> emptyList = new ArrayList<>();
      extractedFeatures.add(emptyList);
    }
    try {
      Tensor inputTensor = Import.of(file);
      for (Tensor row : inputTensor) {
        int timestamp = row.Get(0).number().intValue();
        int index = Arrays.binarySearch(timeStamps, timestamp);
        float[] pos = Primitives.toFloatArray(row.extract(1, 3));
        double[][] cov = new double[][] { { row.Get(3).number().doubleValue(), row.Get(5).number().doubleValue() },
            { row.Get(5).number().doubleValue(), row.Get(4).number().doubleValue() } };
        extractedFeatures.get(index).add(new ImageBlob(pos, cov, timestamp, true));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return extractedFeatures;
  }

  public static void main(String[] args) {
    HandLabeler handlabeler = new HandLabeler();
  }
}
