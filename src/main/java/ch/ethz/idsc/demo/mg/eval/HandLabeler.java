// code by mg
package ch.ethz.idsc.demo.mg.eval;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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

import ch.ethz.idsc.demo.mg.pipeline.ImageBlob;
import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.CSVUtil;
import ch.ethz.idsc.demo.mg.util.VisualizationUtil;

/** GUI for hand labeling of features. Left click adds a feature, right click deletes most recent feature.
 * scrolling while holding ctrl/shift changes x/y-axis length. Feature positoin can be adjusted with wasd keys.
 * Labels can be loaded/saved to a file
 * Filename must have the format imagePrefix_%04dimgNumber_%dtimestamp.fileextension */
// the .CSV file is formatted as follows:
// timestamp , pos[0], pos[1], covariance[0][0], covariance[1][1], covariance[0][1]
// TODO implement ability to rotate ellipse (method stub set up in ImageBlob)
/* package */ class HandLabeler {
  private final int initXAxis; // initial feature shape
  private final int initYAxis;
  private int firstAxis;
  private int secondAxis;
  private final float scaling = 2; // original images are tiny
  private float rotAngle = 0;
  private final int positionDifference = 3; // TODO magic constant
  private final int sizeMultiplier = 20; // TODO magic constant
  // handling of .csv file
  private String imagePrefix;
  private final int numberOfFiles;
  private String fileName;
  private int saveCount = 1;
  // fields for labels
  private int[] timeStamps; // stores timestamp of each image
  private List<List<ImageBlob>> labeledFeatures; // main field of the class
  private int currentImgNumber;
  // visualization
  private final JFrame jFrame = new JFrame();
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
  private JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      setBufferedImage();
      VisualizationUtil.drawEllipsesOnImage(bufferedImage.createGraphics(), labeledFeatures.get(currentImgNumber - 1));
      graphics.drawImage(VisualizationUtil.scaleImage(bufferedImage, scaling), 0, 0, null);
      graphics.drawString("Image number: " + currentImgNumber, 10, 380);
    }
  };
  private final MouseWheelListener mwl = new MouseWheelListener() {
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      if (e.isControlDown()) {
        firstAxis += sizeMultiplier * e.getWheelRotation();
        if (firstAxis < 0) {
          firstAxis = 0;
        }
      }
      if (e.isShiftDown()) {
        secondAxis += sizeMultiplier * e.getWheelRotation();
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
  private final KeyListener keyListener = new KeyAdapter() {
    @Override
    public void keyTyped(KeyEvent e) {
      if (e.getKeyChar() == 'w') {
        shiftFeature(0, -positionDifference);
      }
      if (e.getKeyChar() == 's') {
        shiftFeature(0, positionDifference);
      }
      if (e.getKeyChar() == 'a') {
        shiftFeature(-positionDifference, 0);
      }
      if (e.getKeyChar() == 'd') {
        shiftFeature(positionDifference, 0);
      }
    }

    private void shiftFeature(float d0, float d1) {
      float[] currentPos = labeledFeatures.get(currentImgNumber - 1).get(labeledFeatures.get(currentImgNumber - 1).size() - 1).getPos();
      currentPos[0] += d0;
      currentPos[1] += d1;
      labeledFeatures.get(currentImgNumber - 1).get(labeledFeatures.get(currentImgNumber - 1).size() - 1).setPos(currentPos);
      jComponent.repaint();
    }
  };

  public HandLabeler(PipelineConfig pipelineConfig) {
    // set parameters
    imagePrefix = pipelineConfig.logFileName.toString();
    numberOfFiles = EvaluationFileLocations.images(imagePrefix).list().length;
    fileName = pipelineConfig.handLabelFileName.toString();
    initXAxis = pipelineConfig.initAxis.number().intValue();
    initYAxis = initXAxis;
    firstAxis = initXAxis;
    secondAxis = initXAxis;
    timeStamps = new int[numberOfFiles];
    labeledFeatures = new ArrayList<>(numberOfFiles);
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
          // should avoid overwriting old labeled features TODO should be improved
          String currentFileName = fileName + saveCount;
          CSVUtil.saveToCSV(EvaluationFileLocations.handlabels(currentFileName), labeledFeatures, timeStamps);
          System.out.println("Successfully saved to file " + currentFileName + ".csv");
          saveCount++;
        }
      });
      loadButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          labeledFeatures = CSVUtil.loadFromCSV(EvaluationFileLocations.handlabels(fileName));
          System.out.println("Successfully loaded from file " + fileName + ".csv");
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
          jComponent.repaint();
        }
      });
      jSlider.addKeyListener(keyListener);
      jPanelTop.add("Center", jSlider);
      jPanelMain.add("North", jPanelTop);
    }
    jComponent.addMouseListener(new MouseAdapter() {
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
    });
    jComponent.addMouseWheelListener(mwl);
    jPanelMain.add("Center", jComponent);
    jFrame.addKeyListener(keyListener);
    jFrame.setContentPane(jPanelMain);
    jFrame.setBounds(100, 100, 480, 440);
    jFrame.setFocusable(true);
    jFrame.setVisible(true);
  }

  // set the bufferedImage field according to the currentImgNumber
  private void setBufferedImage() {
    String imgNumberString = String.format("%04d", currentImgNumber);
    String fileName = imagePrefix + "_" + imgNumberString + "_" + timeStamps[currentImgNumber - 1] + ".png";
    File pathToFile = new File(EvaluationFileLocations.images(imagePrefix), fileName);
    try {
      bufferedImage = ImageIO.read(pathToFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // goes through all files in the directory an extracts the timestamps
  private void extractImageTimestamps() {
    // get all filenames and sort
    String[] fileNames = EvaluationFileLocations.images(imagePrefix).list();
    Arrays.sort(fileNames);
    for (int i = 0; i < numberOfFiles; i++) {
      String fileName = fileNames[i];
      // remove file extension
      fileName = fileName.substring(0, fileName.lastIndexOf("."));
      String splitFileName[] = fileName.split("_");
      timeStamps[i] = Integer.parseInt(splitFileName[2]);
    }
  }

  // standalone application
  public static void main(String[] args) {
    PipelineConfig pipelineConfig = new PipelineConfig();
    HandLabeler handlabeler = new HandLabeler(pipelineConfig);
  }
}
