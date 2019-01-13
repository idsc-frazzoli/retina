// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.util.LinkedList;
import java.util.List;

import lcm.util.ClassDiscovery;
import lcm.util.ClassVisitor;

enum ClassDiscoveryDemo {
  ;
  public static void main(String[] args) {
    String cp = "/home/datahaki/Projects/lcm-java/target/classes:/home/datahaki/.m2/repository/net/sf/jchart2d/jchart2d/3.3.2/jchart2d-3.3.2.jar:/home/datahaki/.m2/repository/org/apache/xmlgraphics/xmlgraphics-commons/1.3.1/xmlgraphics-commons-1.3.1.jar:/home/datahaki/.m2/repository/commons-io/commons-io/1.3.1/commons-io-1.3.1.jar:/home/datahaki/.m2/repository/commons-logging/commons-logging/1.0.4/commons-logging-1.0.4.jar:/home/datahaki/.m2/repository/com/jidesoft/jide-oss/2.4.8/jide-oss-2.4.8.jar:/home/datahaki/.m2/repository/ch/ethz/idsc/tensor/0.3.3/tensor-0.3.3.jar";
    List<Class<?>> list = new LinkedList<>();
    ClassVisitor cv = new ClassVisitor() {
      @Override
      public void classFound(String jarfile, Class<?> cls) {
        list.add(cls);
      }
    };
    ClassDiscovery.execute(cp, cv);
    // assertTrue(2000 < list.size());
    System.out.println(list.size());
  }
}
