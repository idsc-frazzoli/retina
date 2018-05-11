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
import ch.ethz.idsc.demo.mg.gui.AccumulatedEventFrame;
import ch.ethz.idsc.demo.mg.gui.HandLabeler;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Import;

// this class provides a evaluation of the tracking algorithm performance. The ground truth is loaded from a hand labeled .CSV file
// and then compared with the tracking algorithm during runtime.
public class TrackingEvaluator {
  private final String imagePrefix;
  private final String handLabelFileName;
  private final File handLabelFile;
  private final File evaluationFilePath;
  private final List<List<ImageBlob>> labeledFeatures; // contains handlabeled features
  private int[] timeStamps; // timestamps for which handlabeld features are available
  private int currentLabelInstant = 0;
  private int numberOfLabelInstants = 0;
  // visualization
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);

  // load the labeled data
  TrackingEvaluator(PipelineConfig pipelineConfig) {
    imagePrefix = pipelineConfig.logFileName.toString();
    handLabelFileName = pipelineConfig.handLabelFileName.toString();
    handLabelFile = HandLabelFileLocations.labels(handLabelFileName);
    evaluationFilePath = HandLabelFileLocations.evaluatedImages(imagePrefix);
    setTimestampsFromCSV(handLabelFile);
    numberOfLabelInstants = timeStamps.length;
    labeledFeatures = HandLabeler.loadFromCSV(handLabelFile, timeStamps);
  }

  public boolean isGroundTruthAvailable(DavisDvsEvent davisDvsEvent) {
    if (currentLabelInstant <= numberOfLabelInstants - 1) {
      if (davisDvsEvent.time == timeStamps[currentLabelInstant]) {
        return true;
      }
    }
    return false;
  }

  // compare estimated with ground truth features and calculate performance metric
  // first version: we only care about feature position
  public void evaluatePerformance(List<ImageBlob> estimatedFeatures) {
    System.out.println("Performance evaluation instant happening now!");
    List<ImageBlob> groundTruthFeatures = labeledFeatures.get(currentLabelInstant);
    evaluationViz(timeStamps[currentLabelInstant], estimatedFeatures, groundTruthFeatures);
    // array to save distance and feature number of closest estimate
    float[][] distToClosestEstimate = new float[groundTruthFeatures.size()][2];
    // TODO initialize to large distance and feature number that is impossible
    // ..
    // iterate through all combinations
    for (int i = 0; i < groundTruthFeatures.size(); i++) {
      for (int j = 0; j < estimatedFeatures.size(); j++) {
        float currentDist = computeDistance(groundTruthFeatures.get(i), estimatedFeatures.get(j));
        if (currentDist < distToClosestEstimate[i][0]) {
          distToClosestEstimate[i][0] = currentDist;
          distToClosestEstimate[i][1] = j;
        }
      }
    }
    // setup for logic:
    // if distToClosestEstimate[i][0] is smaller than threshold, we have a Tp
    // if distToClosestEstimate[i][0] is larger than threshold, we have a Fn
    // all estimatedFeatures that are not assigned Tp are therefore Fp
    // precision: Tp/(Tp+Fp)
    // recall: Tp/(Tp+Fn)
    // counter
    currentLabelInstant++;
  }

  // visualize evaluation
  private void evaluationViz(int currentTimestamp, List<ImageBlob> estimatedFeatures, List<ImageBlob> groundTruthFeatures) {
    // load matching accumulatedEventFrame (very similar as in HandLabeler)
    String imgNumberString = String.format("%04d", currentLabelInstant+1);
    String fileName = imagePrefix + "_" + imgNumberString + "_" + timeStamps[currentLabelInstant] + ".png";
    File pathToFile = new File(HandLabelFileLocations.images(imagePrefix), fileName);
    try {
      bufferedImage = ImageIO.read(pathToFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // overlay groundtruthFeatures
    for (int i = 0; i < groundTruthFeatures.size(); i++) {
      AccumulatedEventFrame.drawImageBlob(bufferedImage.createGraphics(), groundTruthFeatures.get(i), Color.GREEN);
    }
    // overlay estimatedFeatures
    for (int i = 0; i < estimatedFeatures.size(); i++) {
      AccumulatedEventFrame.drawImageBlob(bufferedImage.createGraphics(), estimatedFeatures.get(i), Color.RED);
    }
    // save image
    try {
      String toBeSaved = String.format("%s_%04d_%d.png", imagePrefix, currentLabelInstant, timeStamps[currentLabelInstant]);
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

  private static float computeDistance(ImageBlob firstBlob, ImageBlob secondBlob) {
    float distance = 0;
    return distance; // TODO implementation incomplete
  }
}
