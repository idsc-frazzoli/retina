// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import java.util.Comparator;

import ch.ethz.idsc.demo.mg.slam.SlamParticle;

/** helps to sort {@link SlamParticle}s with descending likelihood */
/* package */ enum SlamParticleLikelihoodComparator implements Comparator<SlamParticle> {
  INSTANCE;
  // ---
  @Override // form Comparator
  public int compare(SlamParticle o1, SlamParticle o2) {
    return Double.compare(o2.getParticleLikelihood(), o1.getParticleLikelihood());
  }
}
