// code by mh
package ch.ethz.idsc.gokart.core.tvec;

public abstract class AbstractTorqueVectoring implements TorqueVectoringInterface {
  final TorqueVectoringConfig torqueVectoringConfig;

  protected AbstractTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    this.torqueVectoringConfig = torqueVectoringConfig;
  }
}
