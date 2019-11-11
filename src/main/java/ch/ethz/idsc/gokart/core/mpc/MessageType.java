// code by mh
package ch.ethz.idsc.gokart.core.mpc;

/* package */ enum MessageType {
  /** control update: send state -> get control and prediction */
  CONTROL_REQUEST, // 0
  /** path update: send new path parameter (not used) */
  PATH_PARAMETER, // 1
  /** parameter update: send new parameters */
  OPTIMIZATION_PARAMETER_KINEMATIC, // 2
  /** control update: receive this from MPC program */
  CONTROL_PREDICTION, // 3
  /** parameter update: send new parameters */
  OPTIMIZATION_PARAMETER_DYNAMIC, // 4
  /** parameter update: send new parameters */
  OPTIMIZATION_PARAMETER_LUDIC, // 5
  ;
}
