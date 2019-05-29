// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.awt.event.ActionListener;

/** Design rationale:
 *
 * {@link ActionListener#actionPerformed(java.awt.event.ActionEvent)} */
public interface ClothoidPlanListener {
  /** @param clothoidPlan */
  void planReceived(ClothoidPlan clothoidPlan);
}
