// code by jph
package ch.ethz.idsc.gokart.offline.api;

public interface LogFile {
  /** @return a string of the form 20180524T175331_f5b40700.lcm.00 */
  String getFilename();

  /** @return a string of the form 20180524T175331 */
  String getTitle();
}
