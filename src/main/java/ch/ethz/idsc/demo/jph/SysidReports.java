// code by jph
package ch.ethz.idsc.demo.jph;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.gokart.offline.api.LogFile;

/* package */ enum SysidReports {
  ;
  public static void main(String[] args) {
    List<LogFile> list = Arrays.asList( //
        GokartLogFile._20180418T102854_5a650fbf, //
        GokartLogFile._20180418T132333_bca165ae, //
        GokartLogFile._20180427T121545_22662115, //
        GokartLogFile._20180427T123334_22662115);
    for (LogFile logFile : list)
      try {
        System.out.println(logFile.getFilename());
        ProduceReport.of(logFile);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }
}
