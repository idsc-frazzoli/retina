// code by jph, em, ta
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class MPCLudicConfig {
  public static final MPCLudicConfig GLOBAL = AppResources.load(new MPCLudicConfig());
  static MPCLudicConfig FERRY = AppResources.load(new MPCLudicConfig());
  // /** Pacejka's formula front wheels parameters */
  // public Scalar pacejkaFB = RealScalar.of(9);
  // public Scalar pacejkaFC = RealScalar.of(1);
  // public Scalar pacejkaFD = RealScalar.of(10);
  // /** Pacejka's formula rear wheels parameters */
  // public Scalar pacejkaRB = RealScalar.of(5.2);
  // public Scalar pacejkaRC = RealScalar.of(1.1);
  // public Scalar pacejkaRD = RealScalar.of(10);
  // /** stiffness, damping, inertia of the steering column */
  // // TODO Unit is SCT/SCE
  // public Scalar steerStiff = RealScalar.of(0.8875);
  // // TODO Unit is SCT*s/SCE
  // public Scalar steerDamp = RealScalar.of(0.1625);
  // // TODO Unit is SCT*s^2/SCE
  // public Scalar steerInertia = RealScalar.of(0.0125);
  /** Parameters of the cost function (without unit of measure) */
  /** Lag Error cost */
  public Scalar lagError = RealScalar.of(1);
  /** Lateral Error cost */
  public Scalar latError = RealScalar.of(0.01);
  /** Path Progress cost */
  public Scalar progress = RealScalar.of(0.2);
  /** Regularizer for input AB */
  public Scalar regularizerAB = RealScalar.of(0.0004);
  /** Regularizer for speedCost */
  public Scalar speedCost = RealScalar.of(0.04);
  /** Slack variable for soft constraint */
  public Scalar slackSoftConstraint = RealScalar.of(5);
  /** Regularizer for input TV */
  public Scalar regularizerTV = RealScalar.of(0.01);
}
