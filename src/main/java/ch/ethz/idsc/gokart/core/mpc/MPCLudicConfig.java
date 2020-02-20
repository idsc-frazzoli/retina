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
  /** Pacejka's formula front wheels parameters */
  public Scalar pacejkaFC = RealScalar.of(1);
  /** Pacejka's formula front wheels parameters, FD controls maximum grip limit */
  @FieldSubdivide(start = "6f", end = "8", intervals = 20)
  public Scalar pacejkaFD = RealScalar.of(7.8);
  /** Pacejka's formula rear wheels parameters */
  public Scalar pacejkaRB = RealScalar.of(5.2);
  /** Pacejka's formula rear wheels parameters */
  public Scalar pacejkaRC = RealScalar.of(1.1);
  /** Pacejka's formula rear wheels parameters, RD controls maximum grip limit */
  @FieldSubdivide(start = "6f", end = "8", intervals = 20)
  public Scalar pacejkaRD = RealScalar.of(7.3);
   /** Inertia of the steering column in SCT*s^2/SCE */
  //@FieldSubdivide(start = "0.01", end = "4", intervals = 50)
  public Scalar steerInertia = RealScalar.of(3.3);
  /** Damping of the steering column in SCT*s/SCE */
  //@FieldSubdivide(start = "0.01", end = "1", intervals = 50)
  public Scalar steerDamp = RealScalar.of(0.24);
  /** Stiffness of the steering column in SCT/SCE */
  //@FieldSubdivide(start = "0.001", end = "0.5", intervals = 50)
  public Scalar steerStiff = RealScalar.of(0.9595);
  /** Parameters of the cost function (without unit of measure) */
  /** Lag Error cost, ensures controller uses closest control point for progress */
  public Scalar lagError = RealScalar.of(1);
  /** Lateral Error cost, punishes deviation from the centerline */
  public Scalar latError = RealScalar.of(0.01);
  /** Path Progress cost, rewards passing control points */
  public Scalar progress = RealScalar.of(0.2);
  /** Regularizer for input AB, cost on acceleration and braking */
  public Scalar regularizerAB = RealScalar.of(0.0004);
  /** Regularizer for speedCost, punishes traveling faster than max speed */
  public Scalar speedCost = RealScalar.of(0.04);
  /** Slack variable for soft constraint, punishes leaving the limits of the track */
  public Scalar slackSoftConstraint = RealScalar.of(7);
  /** Regularizer for input TV, cost of using torque vectoring */
  public Scalar regularizerTV = RealScalar.of(0.0075);
  /** Regularizer for input Tau, cost of using steering torque, only used for torque modes*/
  public Scalar regularizerTau = RealScalar.of(0.0005);
  @FieldSubdivide(start = "0.2f", end = "1.2", intervals = 25)
  public Boolean manualMode = false;
  public Boolean powerSteer = false;
  /**Number of PID updates to wait before publishing LED update */
  public int ledUpdateCycle =11;
}
