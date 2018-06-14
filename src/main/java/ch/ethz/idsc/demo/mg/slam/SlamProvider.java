// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.ImageToWorldLookup;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;

// implements the slam algorithm "simultaneous localization and mapping for event-based vision systems"
// TODO all three maps have the same domain: introduce a new generic map class? Probably already present somewhere
public class SlamProvider {
  private final ImageToWorldLookup imageToWorldLookup;
  private final OccurrenceMap occurrenceMap;
  private final NormalizationMap normalizationMap;
  private final LikelihoodMap likelihoodMap;
  private final SlamParticleSet slamParticleSet;
  // --
  private Tensor lastExpectedState;
  private int lastTimeStamp;

  SlamProvider(PipelineConfig pipelineConfig) {
    imageToWorldLookup = pipelineConfig.createTransformUtilLookup();
    occurrenceMap = new OccurrenceMap();
    normalizationMap = new NormalizationMap();
    likelihoodMap = new LikelihoodMap();
    slamParticleSet = new SlamParticleSet(pipelineConfig);
  }

  public void receiveEvent(DavisDvsEvent davisDvsEvent) {
    // we map the event onto go kart coordinates since this is gonna be required multiple times
    double[] gokartCoordPos = imageToWorldLookup.imageToWorld(davisDvsEvent.x, davisDvsEvent.y);
    // localization step:
    // state estimate propagation
    slamParticleSet.propagateStateEstimate();
    // state likelihoods update
    slamParticleSet.updateStateLikelihoods(gokartCoordPos, likelihoodMap);
    // mapping step:
    // occurrence map update
    // TODO probably smarter to pass pose and likelihood of all particles instead of whole particle objects
    occurrenceMap.update(gokartCoordPos, slamParticleSet.getParticles());
    // normalization map update
    // this does not need to be done for every event --> choose some threshold maybe 50ms
    if (davisDvsEvent.time - lastTimeStamp > 1000) {
      normalizationMap.update(slamParticleSet.getExpectedState(), lastExpectedState);
      lastExpectedState = slamParticleSet.getExpectedState();
      lastTimeStamp = davisDvsEvent.time;
    }
    // likelihood update
    likelihoodMap.update(occurrenceMap, normalizationMap);
  }
}
