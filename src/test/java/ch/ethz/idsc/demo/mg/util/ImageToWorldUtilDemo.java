// code by mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;

enum ImageToWorldUtilDemo {
  ;
  /** for testing
   * 
   * prints for x=170, y=100
   * 3.4386292832405725/-0.4673008409796591 */
  public static void main(String[] args) {
    ImageToGokartUtil test = new PipelineConfig().createImageToGokartUtil();
    ImageToGokartLookup anotherTest = new PipelineConfig().createImageToGokartUtilLookup();
    test.printInfo();
    System.out.println("---");
    int x = 170;
    int y = 100;
    double[] physicalPos = test.imageToGokart(x, y);
    double[] anotherPhysicalPos = anotherTest.imageToGokart(x, y);
    System.out.println(physicalPos[0] + "/" + physicalPos[1]);
    if (physicalPos[0] != 3.4386292832405725 || physicalPos[1] != -0.4673008409796591)
      System.err.println("something has changed");
    if (anotherPhysicalPos[0] != 3.4386292832405725 || anotherPhysicalPos[1] != -0.4673008409796591)
      System.err.println("something has changed");
  }
}
