// code by mg
package ch.ethz.idsc.demo.mg.util.slam;

import java.util.Comparator;

import ch.ethz.idsc.demo.mg.slam.SlamParticle;

/** helps to sort {@link SlamParticle}s with descending likelihood */
public enum SlamParticleLikelihoodComparator implements Comparator<SlamParticle> {
  INSTANCE;
  @Override
  public int compare(SlamParticle o1, SlamParticle o2) {
    return Double.compare(o2.getParticleLikelihood(), o1.getParticleLikelihood());
  }

  // TODO MG remove demo code:
  public static void main(String[] args) {
    System.out.println(Double.compare(3, 10)); // sort ascending values
  }
}
