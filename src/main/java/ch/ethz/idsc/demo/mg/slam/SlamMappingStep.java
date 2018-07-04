// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.SlamFileUtil;
import ch.ethz.idsc.demo.mg.util.SlamMapUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;

// executes the mapping step of the SLAM algorithm
public class SlamMappingStep {
  private final MapProvider[] eventMaps;
  private final String imagePrefix;
  private final boolean localizationMode;
  private final double lookAheadDistance;
  private final double normalizationUpdateRate;
  private final int relevantParticles;
  private double lastNormalizationTimeStamp;

  SlamMappingStep(PipelineConfig pipelineConfig) {
    eventMaps = new MapProvider[3];
    for (int i = 0; i < 3; i++)
      eventMaps[i] = new MapProvider(pipelineConfig);
    imagePrefix = pipelineConfig.logFileName;
    localizationMode = pipelineConfig.localizationMode;
    lookAheadDistance = pipelineConfig.lookAheadDistance.number().doubleValue();
    normalizationUpdateRate = pipelineConfig.normalizationUpdateRate.number().doubleValue();
    relevantParticles = pipelineConfig.relevantParticles.number().intValue();
  }

  public void initialize(double initTimeStamp) {
    lastNormalizationTimeStamp = initTimeStamp;
    if (localizationMode) {
      double[] mapArray = new double[eventMaps[0].getNumberOfCells()];
      SlamFileUtil.loadFromCSV(SlamFileLocations.recordedMaps(imagePrefix), mapArray);
      eventMaps[0].setMapArray(mapArray);
    }
  }

  public void mappingStep(SlamParticle[] slamParticles, double[] eventGokartFrame, double currentTimeStamp) {
    if (eventGokartFrame[0] < lookAheadDistance) {
      SlamMapUtil.updateOccurrenceMapParticles(slamParticles, eventMaps[0], eventGokartFrame, relevantParticles);
    }
    // normalization map currently unused
    if ((currentTimeStamp - lastNormalizationTimeStamp) > normalizationUpdateRate) {
      // SlamMapUtil.updateNormalizationMap(gokartLidarPose.getPose(), lastExpectedPose, eventMaps[1], imageToGokartLookup, gokartToImageUtil, 240, 180,
      // lookAheadDistance);
      // lastExpectedPose = gokartLidarPose.getPose();
      // MapProvider.divide(eventMaps[0], eventMaps[1], eventMaps[2]);
      // lastNormalizationTimeStamp = currentTimeStamp;
    }
  }

  public void mappingStepWithLidar(GokartPoseInterface gokartLidarPose, double[] eventGokartFrame, double currentTimeStamp) {
    SlamMapUtil.updateOccurrenceMapLidar(gokartLidarPose, eventMaps[0], eventGokartFrame);
  }

  public MapProvider getMap(int mapID) {
    return eventMaps[mapID];
  }
}
