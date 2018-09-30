// code by jph
package ch.ethz.idsc.demo.jph.sys;

import ch.ethz.idsc.demo.GokartLogFile;

enum GokartLcmImageGenerator {
  ;
  public static void main(String[] args) {
    for (GokartLogFile gokartLogFile : GokartLogFile.range( //
        GokartLogFile._20180813T115544_26cfbbca, //
        GokartLogFile._20180927T162555_44599876)) {
      System.out.println(gokartLogFile);
    }
  }
}
