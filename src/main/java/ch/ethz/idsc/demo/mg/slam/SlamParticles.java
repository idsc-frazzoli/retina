// code by jph
package ch.ethz.idsc.demo.mg.slam;

/* package */ enum SlamParticles {
  ;
  /** @param length
   * @return array of given length of SlamParticle instances */
  public static SlamParticle[] allocate(int length) {
    SlamParticle[] slamParticles = new SlamParticle[length];
    for (int index = 0; index < length; ++index)
      slamParticles[index] = new SlamParticle();
    return slamParticles;
  }
}
