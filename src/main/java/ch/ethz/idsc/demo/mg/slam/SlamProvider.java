// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// implements the slam algorithm
// TODO all three maps have the same domain: introduce a new generic map class? Probably already present somewhere 
public class SlamProvider {
  private final OccurrenceMap occurrenceMap;
  private final NormalizationMap normalizationMap;
  private final LikelihoodMap likelihoodMap;
  private final SlamParticleSet slamParticleSet;

  SlamProvider(PipelineConfig pipelineConfig) {
    occurrenceMap = new OccurrenceMap();
    normalizationMap = new NormalizationMap();
    likelihoodMap = new LikelihoodMap();
    slamParticleSet = new SlamParticleSet();
  }

  public void receiveEvent(DavisDvsEvent davisDvsEvent) {
    // algorithm has two main steps: localization step and mapping step
    // localization step:
    // state estimate propagation
    slamParticleSet.propagateStateEstimate();
    // state likelihoods update
    slamParticleSet.propagateStateLikelihoods();
    // mapping step:
    // occurrence map update
    occurrenceMap.update();
    // normalization map update
    normalizationMap.update();
    // likelihood update
    likelihoodMap.update();
  }
}
