// code by jph, em, ta
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;

public class MPCLudicConfig {
  public static final MPCLudicConfig GLOBAL = AppResources.load(new MPCLudicConfig());
  static MPCLudicConfig FERRY = AppResources.load(new MPCLudicConfig());
  /** The limit for the Go-kart speed */
  @FieldSubdivide(start = "4f[m*s^-1]", end = "16[m*s^-1]", intervals = 12)
  public Scalar maxSpeed = Quantity.of(10, SI.VELOCITY);
  /** Pacejka's formula front wheels parameters */
  public Scalar pacejkaFB = RealScalar.of(9);
  public Scalar pacejkaFC = RealScalar.of(1);
  @FieldSubdivide(start = "6f", end = "8", intervals = 20)
  public Scalar pacejkaFD = RealScalar.of(7.5);
  /** Pacejka's formula rear wheels parameters */
  public Scalar pacejkaRB = RealScalar.of(5.2);
  public Scalar pacejkaRC = RealScalar.of(1.1);
  @FieldSubdivide(start = "6f", end = "8", intervals = 20)
  public Scalar pacejkaRD = RealScalar.of(7);
  // /** stiffness, damping, inertia of the steering column */
  // // TODO Unit is SCT*s^2/SCE
  public Scalar steerInertia = RealScalar.of(0.88);
  // // TODO Unit is SCT*s/SCE
  public Scalar steerDamp = RealScalar.of(0.16);
  // // TODO Unit is SCT/SCE
  public Scalar steerStiff = RealScalar.of(0.01);
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
  public Scalar slackSoftConstraint = RealScalar.of(7);
  /** Regularizer for input TV */
  public Scalar regularizerTV = RealScalar.of(0.01);
  public Scalar regularizerTau = RealScalar.of(0.0001);
  public Scalar torqueScale = RealScalar.of(0.58);
  public Boolean powerSteer = false;
  public Boolean ledSteer = false;
}
