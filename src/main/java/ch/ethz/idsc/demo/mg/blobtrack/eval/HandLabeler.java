// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.eval;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.demo.mg.util.vis.VisBlobTrackUtil;
import ch.ethz.idsc.retina.util.img.BufferedImageResize;

/** GUI for hand labeling of features. Left click adds a feature, right click deletes most recent feature.
 * scrolling while holding ctrl/shift changes x/y-axis length. Feature position can be adjusted with wasd keys.
 * Features can be rotated with scrolling while holding alt key.
 * Labels can be loaded/saved to a file
 * Filename must have the format imagePrefix_%04dimgNumber_%dtimestamp.fileextension
 * TODO MG would be more convenient if slider can be moved with left hand, e.g. y and x keys */
/* package */ class HandLabeler {
  /** draw ellipses for image based on list of blobs for the image.
   *
   * @param graphics
   * @param list */
  static void drawEllipsesOnImage(Graphics2D graphics, List<ImageBlob> list) {
    list.forEach(imageBlob -> VisBlobTrackUtil.drawImageBlob(graphics, imageBlob, Color.WHITE));
  }

  private final float scaling = 2; // original images are tiny
  private final int initXAxis; // initial feature shape
  private final int initYAxis;
  private final int positionDifference;
  private final int sizeMultiplier;
  private final int defaultBlobID;
  private double rotAngle = 0;
  private double firstAxis;
  private double secondAxis;
  // handling of .csv file
  private final String imagePrefix;
  private final int numberOfFiles;
  private final String fileName;
  private int saveCount = 1;
  // fields for labels
  private List<List<ImageBlob>> labeledFeatures; // main field of the class
  private int[] timeStamps; // stores timestamp of each image
  private int currentImgNumber;
  // visualization
  private final JFrame jFrame = new JFrame();
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
  private JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      setBufferedImage();
      drawEllipsesOnImage(bufferedImage.createGraphics(), labeledFeatures.get(currentImgNumber - 1));
      graphics.drawImage(BufferedImageResize.of(bufferedImage, scaling), 0, 0, null);
      graphics.drawString("Image number: " + currentImgNumber, 10, 380);
    }
  };
  private final MouseWheelListener mwl = new MouseWheelListener() {
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      if (e.isControlDown()) {
        firstAxis += sizeMultiplier * e.getWheelRotation();
        if (firstAxis < 0)
          firstAxis = 0;
      }
      if (e.isShiftDown()) {
        secondAxis += sizeMultiplier * e.getWheelRotation();
        if (secondAxis < 0)
          secondAxis = 0;
      }
      if (e.isAltDown()) {
        rotAngle += 0.1 * e.getWheelRotation();
        rotAngle %= Math.PI;
      }
      // change shape of most recent feature
      labeledFeatures.get(currentImgNumber - 1).get(labeledFeatures.get(currentImgNumber - 1).size() - 1).setCovariance(firstAxis, secondAxis, rotAngle);
      jComponent.repaint();
    }
  };
  private final KeyListener keyListener = new KeyAdapter() {
    @Override
    public void keyTyped(KeyEvent e) {
      if (e.getKeyChar() == 'w')
        shiftFeature(0, -positionDifference);
      if (e.getKeyChar() == 's')
        shiftFeature(0, positionDifference);
      if (e.getKeyChar() == 'a')
        shiftFeature(-positionDifference, 0);
      if (e.getKeyChar() == 'd')
        shiftFeature(positionDifference, 0);
    }

    private void shiftFeature(float d0, float d1) {
      float[] currentPos = labeledFeatures.get(currentImgNumber - 1).get(labeledFeatures.get(currentImgNumber - 1).size() - 1).getPos();
      currentPos[0] += d0;
      currentPos[1] += d1;
      labeledFeatures.get(currentImgNumber - 1).get(labeledFeatures.get(currentImgNumber - 1).size() - 1).setPos(currentPos);
      jComponent.repaint();
    }
  };
  private final MouseAdapter mouseAdapter = new MouseAdapter() {
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
      // add labels with left click
      if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
        Point p = mouseEvent.getPoint();
        // point coordinates need to be scaled back since we click on a scaled image
        ImageBlob blob = new ImageBlob(new float[] { p.x / scaling, p.y / scaling }, new double[][] { { initXAxis, 0 }, { 0, initYAxis } },
            timeStamps[currentImgNumber - 1], true, defaultBlobID);
        labeledFeatures.get(currentImgNumber - 1).add(blob);
      }
      // remove last added label with right click
      if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
        labeledFeatures.get(currentImgNumber - 1).remove(labeledFeatures.get(currentImgNumber - 1).size() - 1);
      }
      jComponent.repaint();
    }
  };
  private final ActionListener loadListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      labeledFeatures = EvalUtil.loadFromCSV(EvaluationFileLocations.HANDLABEL_CSV.subfolder(fileName), timeStamps);
      System.out.println("Successfully loaded from file " + fileName + ".csv");
      // repaint such that saved blobs of current image are displayed
      jComponent.repaint();
    }
  };
  private final ActionListener saveListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      // avoid overwriting the original labeledFeatures csv
      String currentFileName = fileName + saveCount;
      EvalUtil.saveToCSV(EvaluationFileLocations.HANDLABEL_CSV.subfolder(currentFileName), labeledFeatures, timeStamps);
      System.out.println("Successfully saved to file " + currentFileName + ".csv");
      saveCount++;
    }
  };

  public HandLabeler(BlobTrackConfig pipelineConfig) {
    // set parameters
    imagePrefix = pipelineConfig.davisConfig.logFilename();
    numberOfFiles = MgEvaluationFolders.HANDLABEL.subfolder(imagePrefix).list().length;
    fileName = pipelineConfig.handLabelFileName.toString();
    positionDifference = pipelineConfig.positionDifference.number().intValue();
    sizeMultiplier = pipelineConfig.sizeMultiplier.number().intValue();
    defaultBlobID = pipelineConfig.defaultBlobID.number().intValue();
    initXAxis = pipelineConfig.initAxis.number().intValue();
    initYAxis = initXAxis;
    firstAxis = initXAxis;
    secondAxis = initXAxis;
    timeStamps = EvalUtil.getTimestampsFromImages(numberOfFiles, imagePrefix);
    labeledFeatures = new ArrayList<>(numberOfFiles);
    // set up empty list of lists
    for (int i = 0; i < numberOfFiles; i++)
      labeledFeatures.add(new ArrayList<>());
    JPanel jPanelMain = new JPanel(new BorderLayout());
    {
      JPanel jPanelTop = new JPanel();
      JButton saveButton = new JButton("save labels");
      saveButton.addActionListener(saveListener);
      jPanelTop.add(saveButton);
      JButton loadButton = new JButton("load labels");
      loadButton.addActionListener(loadListener);
      jPanelTop.add(loadButton);
      // slider to slide through all images in directory
      JSlider jSlider = new JSlider(1, numberOfFiles);
      // set up GUI to show first image
      jSlider.setValue(1);
      currentImgNumber = jSlider.getValue();
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
    jPanelMain.add("Center", jComponent);
    jComponent.addMouseListener(mouseAdapter);
    jComponent.addMouseWheelListener(mwl);
    jFrame.addKeyListener(keyListener);
    jFrame.setContentPane(jPanelMain);
    jFrame.setBounds(100, 100, 480, 440);
    jFrame.setFocusable(true);
    jFrame.setVisible(true);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  // set the bufferedImage field according to the currentImgNumber
  private void setBufferedImage() {
    String imgNumberString = String.format("%04d", currentImgNumber);
    String fileName = imagePrefix + "_" + imgNumberString + "_" + timeStamps[currentImgNumber - 1] + ".png";
    File pathToFile = new File(MgEvaluationFolders.HANDLABEL.subfolder(imagePrefix), fileName);
    try {
      bufferedImage = ImageIO.read(pathToFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // standalone application
  public static void main(String[] args) {
    BlobTrackConfig pipelineConfig = new BlobTrackConfig();
    new HandLabeler(pipelineConfig);
  }
}
