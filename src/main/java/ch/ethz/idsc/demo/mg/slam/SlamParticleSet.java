// code by mg
package ch.ethz.idsc.demo.mg.slam;

// provides a set of SlamParticles
public class SlamParticleSet {
  private final SlamParticle[] slamParticleSet;
  
  SlamParticleSet(){
    int i = 1;
    slamParticleSet = new SlamParticle[i];
  }

  // propagate each single particle
  public void propagateStateEstimate() {
    // ..
  }

  // propagate each single particle
  public void propagateStateLikelihoods() {
    // ..
  }
}
