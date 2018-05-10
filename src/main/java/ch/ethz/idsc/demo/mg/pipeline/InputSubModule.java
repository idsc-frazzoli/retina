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
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.util.data.TensorProperties;
import ch.ethz.idsc.tensor.Scalar;

// this module distributes the event stream to the visualization and control pipeline
public class InputSubModule implements OfflineLogListener, DavisDvsListener {
  // set up listener
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  // config
  private final PipelineConfig pipelineConfig = TensorProperties.retrieve(UserHome.file("config.properties"), new PipelineConfig());
  // event filtering
  private final EventFiltering eventFiltering = new EventFiltering(pipelineConfig);
  // blob tracking
  private final BlobTracking tracking = new BlobTracking(pipelineConfig);
  // feature filtering
  private final ImageBlobSelector blobSelector = new ImageBlobSelector(pipelineConfig);
  // blob transformation
  private final ImageToWorldTransform transformer = new ImageToWorldTransform(pipelineConfig);
  // visualization
  private final PipelineVisualization viz = new PipelineVisualization(); // visualization GUI
  private final AccumulatedEventFrame[] eventFrames = new AccumulatedEventFrame[3]; // perception module visualization frames
  private final PhysicalBlobFrame[] physicalFrames = new PhysicalBlobFrame[3]; // control module visualization frames
  // performance evaluation
  private final TrackingEvaluator evaluator = new TrackingEvaluator(pipelineConfig);
  // pipeline configuration
  private boolean evaluatePerformance;
  private boolean saveImages;
  private int maxDuration;
  private int visualizationInterval;
  private int imageCount = 0;
  // image saving
  private String imagePrefix;
   private File pathToImages = HandLabelFileLocations.images(); // path where images are saved
  private int savingInterval;
  // fields for testing
  private float eventCount = 0;
  private float filteredEventCount;
  private int lastImagingTimestamp;
  private int lastSavingTimestamp;
  private int begin, end;
  private long startTime, endTime;

  public InputSubModule(PipelineConfig pipelineConfig) {
    setParameters(pipelineConfig);
    davisDvsDatagramDecoder.addDvsListener(this);
    for (int i = 0; i < eventFrames.length; i++) {
      eventFrames[i] = new AccumulatedEventFrame();
    }
    for (int i = 0; i < physicalFrames.length; i++) {
      physicalFrames[i] = new PhysicalBlobFrame();
    }
  }
  
  private void setParameters(PipelineConfig pipelineConfig) {
    saveImages = (0 != pipelineConfig.saveImages.number().intValue());
    System.out.println(saveImages);
    evaluatePerformance = (0 != pipelineConfig.evaluatePerformance.number().intValue());
    maxDuration = pipelineConfig.maxDuration.number().intValue();
    visualizationInterval = pipelineConfig.visualizationInterval.number().intValue();
    imagePrefix = pipelineConfig.imagePrefix.toString();
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
      begin = davisDvsEvent.time;
      lastImagingTimestamp = davisDvsEvent.time;
      lastSavingTimestamp = davisDvsEvent.time;
      startTime = System.currentTimeMillis();
    }
    ++eventCount;
    // visualization of raw events
    eventFrames[0].receiveEvent(davisDvsEvent);
    // evaluation tool
    if (evaluatePerformance && evaluator.isGroundTruthAvailable(davisDvsEvent)) {
      evaluator.evaluatePerformance(blobSelector.getSelectedBlobs());
    }
    // filtering returns a boolean
    if (eventFiltering.filterPipeline(davisDvsEvent)) {
      // control pipeline
      tracking.receiveEvent(davisDvsEvent);
      blobSelector.receiveActiveBlobs(tracking.getActiveBlobs());
      transformer.transformSelectedBlobs(blobSelector.getSelectedBlobs());
      // visualization
      eventFrames[1].receiveEvent(davisDvsEvent);
      eventFrames[2].receiveEvent(davisDvsEvent);
      ++filteredEventCount;
    }
    // save frames
    if (saveImages && (davisDvsEvent.time - lastSavingTimestamp) > savingInterval * 1000) {
      saveFrame(pathToImages, imagePrefix, davisDvsEvent.time);
      lastSavingTimestamp = davisDvsEvent.time;
    }
    // the events are accumulated for the interval time and then displayed in a single frame
    if ((davisDvsEvent.time - lastImagingTimestamp) > visualizationInterval * 1000) {
      // visualization repaint
      viz.setFrames(constructFrames());
      clearAllFrames();
      lastImagingTimestamp = davisDvsEvent.time;
    }
    if (davisDvsEvent.time - begin > maxDuration * 1000) {
      end = davisDvsEvent.time;
      endTime = System.currentTimeMillis();
      int diff = end - begin;
      System.out.println("Percentage hit by active blobs: " + tracking.hitthreshold / eventCount * 100);
      System.out.println("Elapsed time in the eventstream [ms]: " + diff / 1000 + " with " + eventCount + " events");
      long elapsedTime = endTime - startTime;
      System.out.println("Computation time: " + elapsedTime + "[ms]");
      System.out.format("%.2f%% of the events were processed after filtering.", getFilteredPercentage());
      System.exit(0);
    }
  }

  // for visualization
  private BufferedImage[] constructFrames() {
    BufferedImage[] combinedFrames = new BufferedImage[6];
    combinedFrames[0] = eventFrames[0].getAccumulatedEvents();
    combinedFrames[1] = eventFrames[1].overlayActiveBlobs(blobSelector.getProcessedBlobs());
    combinedFrames[2] = eventFrames[2].overlayHiddenBlobs((tracking.getHiddenBlobs()));
    combinedFrames[3] = physicalFrames[0].overlayPhysicalBlobs((transformer.getPhysicalBlobs()));
    combinedFrames[4] = physicalFrames[1].getFrame();
    combinedFrames[5] = physicalFrames[2].getFrame();
    return combinedFrames;
  }

  // for visualization
  private void clearAllFrames() {
    for (int i = 0; i < eventFrames.length; i++) {
      eventFrames[i].clearImage();
    }
    for (int i = 0; i < physicalFrames.length; i++) {
      physicalFrames[i].clearImage();
    }
  }

  // for visualization
  private void saveFrame(File parentFilePath, String imagePrefix, int timeStamp) {
    try {
      imageCount++;
      String fileName = String.format("%s_%04d_%d.png", imagePrefix, imageCount, timeStamp);
      ImageIO.write(eventFrames[1].overlayActiveBlobs(blobSelector.getProcessedBlobs()), "png", new File(parentFilePath, "1" + fileName));
      // possibility to save whole GUI
      // BufferedImage wholeGUI = viz.getGUIFrame();
      // ImageIO.write(wholeGUI, "png", new File(parentFilePath, fileName));
      System.out.printf("Images saved as %s\n", fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // for testing
  public float getFilteredPercentage() {
    return 100 * filteredEventCount / eventCount;
  }
}
