// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.mg.HandLabelFileLocations;
import ch.ethz.idsc.demo.mg.gui.AccumulatedEventFrame;
import ch.ethz.idsc.demo.mg.gui.PhysicalBlobFrame;
import ch.ethz.idsc.demo.mg.gui.PipelineVisualization;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// this module distributes the event stream to the visualization and control pipeline
// TODO if we just wanna evaluate tracking performance, no need to incorporate blobTransform
// TODO collectResults fct once evaluator is implemented
public class InputSubModule implements OfflineLogListener, DavisDvsListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  // pipeline modules
  private final EventFiltering eventFiltering;
  private final BlobTracking tracking;
  private final ImageBlobSelector blobSelector;
  private BlobTransform transformer = null; // default initialization if unused
  private TrackingEvaluator evaluator = null;
  // visualization
  private boolean visualizePipeline;
  private PipelineVisualization visualizer = null;
  private AccumulatedEventFrame[] eventFrames = null;
  private PhysicalBlobFrame[] physicalFrames = null;
  // pipeline configuration
  private boolean calibrationAvailable;
  private boolean evaluatePerformance;
  private int visualizationInterval;
  private int imageCount = 0;
  // image saving
  private boolean saveImages;
  private String imagePrefix;
  private File parentFilePath;
  private int savingInterval;
  // log summary
  private float eventCount = 0;
  private float filteredEventCount;
  private int lastImagingTimestamp;
  private int lastSavingTimestamp;
  private int firstTimestamp, lastTimestamp;
  private long startTime, endTime;

  public InputSubModule(PipelineConfig pipelineConfig) {
    setParameters(pipelineConfig);
    // initialize pipeline modules
    eventFiltering = new EventFiltering(pipelineConfig);
    tracking = new BlobTracking(pipelineConfig);
    blobSelector = new ImageBlobSelector(pipelineConfig);
    // calibration required for transformation to physical space
    if (calibrationAvailable) {
      transformer = new BlobTransform();
      // TODO inelegant
      TransformUtil.initialize(pipelineConfig);
    }
    // optional evaluation
    if (evaluatePerformance) {
      evaluator = new TrackingEvaluator(pipelineConfig);
    }
    // optional visualization
    if (visualizePipeline) {
      visualizer = new PipelineVisualization();
      eventFrames = new AccumulatedEventFrame[3];
      for (int i = 0; i < eventFrames.length; i++) {
        eventFrames[i] = new AccumulatedEventFrame(pipelineConfig);
      }
      if (calibrationAvailable) {
        physicalFrames = new PhysicalBlobFrame[3];
        for (int i = 0; i < physicalFrames.length; i++) {
          physicalFrames[i] = new PhysicalBlobFrame(pipelineConfig);
        }
      }
    }
    // initialize listener
    davisDvsDatagramDecoder.addDvsListener(this);
  }

  private void setParameters(PipelineConfig pipelineConfig) {
    saveImages = pipelineConfig.saveImages;
    visualizePipeline = pipelineConfig.visualizePipeline;
    calibrationAvailable = pipelineConfig.calibrationAvailable;
    evaluatePerformance = pipelineConfig.evaluatePerformance;
    visualizationInterval = pipelineConfig.visualizationInterval.number().intValue();
    imagePrefix = pipelineConfig.logFileName.toString();
    parentFilePath = HandLabelFileLocations.images(imagePrefix);
    savingInterval = pipelineConfig.savingInterval.number().intValue();
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    // initialize timers
    if (eventCount == 0) {
      firstTimestamp = davisDvsEvent.time;
      lastImagingTimestamp = davisDvsEvent.time;
      lastSavingTimestamp = davisDvsEvent.time;
      startTime = System.currentTimeMillis();
    }
    ++eventCount;
    // visualization of raw events
    if (visualizePipeline) {
      eventFrames[0].receiveEvent(davisDvsEvent);
    }
    // evaluation tool
    if (evaluatePerformance && evaluator.isGroundTruthAvailable(davisDvsEvent)) {
      evaluator.evaluatePerformance(blobSelector.getSelectedBlobs());
    }
    // filtering returns a boolean
    if (eventFiltering.filterPipeline(davisDvsEvent)) {
      ++filteredEventCount;
      // control pipeline
      tracking.receiveEvent(davisDvsEvent);
      blobSelector.receiveActiveBlobs(tracking.getActiveBlobs());
      if (calibrationAvailable) {
        transformer.transformSelectedBlobs(blobSelector.getSelectedBlobs());
      }
      // visualization
      if (visualizePipeline) {
        eventFrames[1].receiveEvent(davisDvsEvent);
        eventFrames[2].receiveEvent(davisDvsEvent);
      }
    }
    // save frames
    if (saveImages && (davisDvsEvent.time - lastSavingTimestamp) > savingInterval * 1000) {
      saveFrame(parentFilePath, imagePrefix, davisDvsEvent.time);
      lastSavingTimestamp = davisDvsEvent.time;
    }
    // the events are accumulated for the interval time and then displayed in a single frame
    if (visualizePipeline && (davisDvsEvent.time - lastImagingTimestamp) > visualizationInterval * 1000) {
      // visualization repaint
      visualizer.setFrames(constructFrames());
      clearAllFrames();
      lastImagingTimestamp = davisDvsEvent.time;
    }
    lastTimestamp = davisDvsEvent.time;
  }

  public void summarizeLog() {
    endTime = System.currentTimeMillis();
    int diff = lastTimestamp - firstTimestamp;
    System.out.println("Percentage hit by active blobs: " + tracking.hitthreshold / eventCount * 100);
    System.out.println("Elapsed time in the eventstream [ms]: " + diff / 1000 + " with " + eventCount + " events");
    long elapsedTime = endTime - startTime;
    System.out.println("Computation time: " + elapsedTime + "[ms]");
    System.out.format("%.2f%% of the events were processed after filtering.\n", (100 * filteredEventCount / eventCount));
    if(evaluatePerformance) {
      evaluator.summarizeResults();
    }
  }

  // for visualization
  private BufferedImage[] constructFrames() {
    BufferedImage[] combinedFrames = new BufferedImage[6];
    combinedFrames[0] = eventFrames[0].getAccumulatedEvents();
    combinedFrames[1] = eventFrames[1].overlayActiveBlobs(blobSelector.getProcessedBlobs());
    combinedFrames[2] = eventFrames[2].overlayHiddenBlobs((tracking.getHiddenBlobs()));
    if (calibrationAvailable) {
      combinedFrames[3] = physicalFrames[0].overlayPhysicalBlobs((transformer.getPhysicalBlobs()));
      combinedFrames[4] = physicalFrames[1].getFrame();
      combinedFrames[5] = physicalFrames[2].getFrame();
    }
    return combinedFrames;
  }

  // for visualization
  private void clearAllFrames() {
    for (int i = 0; i < eventFrames.length; i++) {
      eventFrames[i].clearImage();
    }
  }

  // for image saving
  private void saveFrame(File parentFilePath, String imagePrefix, int timeStamp) {
    try {
      imageCount++;
      String fileName = String.format("%s_%04d_%d.png", imagePrefix, imageCount, timeStamp);
      ImageIO.write(eventFrames[0].getAccumulatedEvents(), "png", new File(parentFilePath, fileName));
      // possibility to save whole GUI
      // BufferedImage wholeGUI = viz.getGUIFrame();
      // ImageIO.write(wholeGUI, "png", new File(parentFilePath, fileName));
      System.out.printf("Images saved as %s\n", fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
