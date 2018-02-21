// code by jph
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.retina.util.StartAndStoppable;

abstract class PurePursuitBase implements StartAndStoppable {
  /** status default false */
  protected boolean status = false;

  public final void setOperational(boolean status) {
    this.status = status;
  }

  public final boolean isOperational() {
    return status;
  }
}
