// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.mg.HandLabelFileLocations;
import ch.ethz.idsc.demo.mg.gui.VisualizationUtil;
import ch.ethz.idsc.demo.mg.gui.HandLabeler;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Import;

// this class provides a evaluation of the tracking algorithm performance. The ground truth is loaded from a hand labeled .CSV file
// and then compared with the tracking algorithm during runtime.
// TODO maybe save results to a file in the end
public class TrackingEvaluator {
  private final String imagePrefix;
  private final String handLabelFileName;
  private final File handLabelFile;
  private final File evaluationFilePath;
  private final TrackingEvaluatorInstant[] evaluationInstants;
  private final PipelineConfig pipelineConfig; // to pass parameters to TrackingEval
  private final List<List<ImageBlob>> labeledFeatures; // contains handlabeled features
  private final int numberOfLabelInstants;
  private final boolean saveEvaluationFrame;
  private int[] timeStamps; // timestamps for which handlabeld features are available
  private int currentLabelInstant = 0;

  // load the labeled data
  TrackingEvaluator(PipelineConfig pipelineConfig) {
    imagePrefix = pipelineConfig.logFileName.toString();
    handLabelFileName = pipelineConfig.handLabelFileName.toString();
    handLabelFile = HandLabelFileLocations.labels(handLabelFileName);
    evaluationFilePath = HandLabelFileLocations.evaluatedImages(imagePrefix);
    setTimestampsFromCSV(handLabelFile);
    labeledFeatures = HandLabeler.loadFromCSV(handLabelFile, timeStamps);
    numberOfLabelInstants = timeStamps.length;
    evaluationInstants = new TrackingEvaluatorInstant[numberOfLabelInstants];
    this.pipelineConfig = pipelineConfig;
    saveEvaluationFrame = pipelineConfig.saveEvaluationFrame;
  }

  public boolean isGroundTruthAvailable(DavisDvsEvent davisDvsEvent) {
    if (currentLabelInstant <= numberOfLabelInstants - 1) {
      if (davisDvsEvent.time == timeStamps[currentLabelInstant]) {
        return true;
      }
    }
    return false;
  }

  // first version: we only care about feature position
  public void evaluatePerformance(List<ImageBlob> estimatedFeatures) {
    System.out.println("Performance evaluation instant happening now!");
    List<ImageBlob> groundTruthFeatures = labeledFeatures.get(currentLabelInstant);
    // create evaluation instant and run comparison
    evaluationInstants[currentLabelInstant] = new TrackingEvaluatorInstant(pipelineConfig, groundTruthFeatures, estimatedFeatures);
    evaluationInstants[currentLabelInstant].compareFeatures();
    // save evaluation frame
    if (saveEvaluationFrame) {
      saveEvaluationFrame();
    }
    // counter
    currentLabelInstant++;
  }

  // accumulatedEventFrame with estimated and ground truth features
  private void saveEvaluationFrame() {
    List<ImageBlob> estimatedFeatures = evaluationInstants[currentLabelInstant].getEstimatedFeatures();
    List<ImageBlob> groundTruthFeatures = evaluationInstants[currentLabelInstant].getGroundTruthFeatures();
    BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
    // load matching accumulatedEventFrame (very similar as in HandLabeler)
    String imgNumberString = String.format("%04d", currentLabelInstant + 1);
    String fileName = imagePrefix + "_" + imgNumberString + "_" + timeStamps[currentLabelInstant] + ".png";
    File pathToFile = new File(HandLabelFileLocations.images(imagePrefix), fileName);
    System.out.println(pathToFile);
    try {
      bufferedImage = ImageIO.read(pathToFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // overlay groundtruthFeatures
    for (int i = 0; i < groundTruthFeatures.size(); i++) {
      VisualizationUtil.drawImageBlob(bufferedImage.createGraphics(), groundTruthFeatures.get(i), Color.GREEN);
    }
    // overlay estimatedFeatures
    for (int i = 0; i < estimatedFeatures.size(); i++) {
      VisualizationUtil.drawImageBlob(bufferedImage.createGraphics(), estimatedFeatures.get(i), Color.RED);
    }
    // save image
    try {
      String toBeSaved = String.format("%s_%04d_%d.png", imagePrefix, currentLabelInstant + 1, timeStamps[currentLabelInstant]);
      ImageIO.write(bufferedImage, "png", new File(evaluationFilePath, toBeSaved));
      System.out.printf("Evaluation frame saved as %s\n", fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // extract timeStamps from .CSV file
  private void setTimestampsFromCSV(File file) {
    // use list because length is unknown
    List<Integer> timestampList = new ArrayList<>();
    try {
      Tensor inputTensor = Import.of(file);
      // initialize extractedTimestamps
      timestampList.add(inputTensor.get(0).Get(0).number().intValue());
      for (int i = 1; i < inputTensor.length(); i++) {
        if (inputTensor.get(i).Get(0).number().intValue() != timestampList.get(timestampList.size() - 1)) {
          timestampList.add(inputTensor.get(i).Get(0).number().intValue());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // convert list to array
    int[] timeStamps = new int[timestampList.size()];
    for (int i = 0; i < timestampList.size(); i++) {
      timeStamps[i] = timestampList.get(i);
    }
    this.timeStamps = timeStamps;
  }

  // fct to be called to collect results from tracking evaluation
  public TrackingEvaluatorInstant[] getEvaluationInstants() {
    return evaluationInstants;
  }

  // for testing
  public void summarizeResults() {
    System.out.println("The average recall is" + 100 * getAverageRecall() + "%");
    System.out.println("The average precision is" + 100 * getAveragePrecision() + "%");
  }

  private float getAverageRecall() {
    float averageRecall = 0;
    for (int i = 0; i < numberOfLabelInstants; i++) {
      averageRecall += evaluationInstants[i].getRecall() / numberOfLabelInstants;
    }
    return averageRecall;
  }

  private float getAveragePrecision() {
    float averagePrecision = 0;
    for (int i = 0; i < numberOfLabelInstants; i++) {
      averagePrecision += evaluationInstants[i].getPrecision() / numberOfLabelInstants;
    }
    return averagePrecision;
  }
}
