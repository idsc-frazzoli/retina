// code by mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;

enum TransformUtilDemo {
  ;
  /** for testing
   * 
   * prints
   * 3.4386292832405725/-0.4673008409796591 */
  public static void main(String[] args) {
    TransformUtil test = new PipelineConfig().createTransformUtil();
    double[] physicalPos = test.imageToWorld(170, 100);
    System.out.println(physicalPos[0] + "/" + physicalPos[1]);
  }
}
