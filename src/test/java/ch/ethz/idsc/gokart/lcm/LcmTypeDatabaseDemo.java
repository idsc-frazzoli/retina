// code by jph
package ch.ethz.idsc.gokart.lcm;

import lcm.spy.LcmTypeDatabase;
import lcm.spy.LcmTypeDatabaseBuilder;
import lcm.util.ClassPaths;

public class LcmTypeDatabaseDemo {
  private static int testCp(String classpath) {
    LcmTypeDatabase lcmTypeDatabase = LcmTypeDatabaseBuilder.create(classpath);
    return lcmTypeDatabase.size();
  }

  public static void main(String[] args) {
    {
      String classpath = //
          "/home/datahaki/Projects/lcm-java/target/classes:/home/datahaki/.m2/repository/net/sf/jchart2d/jchart2d/3.3.2/jchart2d-3.3.2.jar:/home/datahaki/.m2/repository/org/apache/xmlgraphics/xmlgraphics-commons/1.3.1/xmlgraphics-commons-1.3.1.jar:/home/datahaki/.m2/repository/commons-io/commons-io/1.3.1/commons-io-1.3.1.jar:/home/datahaki/.m2/repository/commons-logging/commons-logging/1.0.4/commons-logging-1.0.4.jar:/home/datahaki/.m2/repository/com/jidesoft/jide-oss/2.4.8/jide-oss-2.4.8.jar:/home/datahaki/.m2/repository/ch/ethz/idsc/tensor/0.3.3/tensor-0.3.3.jar";
      System.out.println(testCp(classpath));
    }
    {
      String classpath = ClassPaths.getDefault();
      System.out.println(testCp(classpath));
    }
  }
}
