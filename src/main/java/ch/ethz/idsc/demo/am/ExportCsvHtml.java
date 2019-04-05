// code by jph
package ch.ethz.idsc.demo.am;

import java.io.File;

import ch.ethz.idsc.demo.jph.log.DynamicsConversionBulk;

enum ExportCsvHtml {
  ;
  public static void main(String[] args) {
    DynamicsConversionBulk.all(new File("/media/datahaki/data/gokart/cuts/20190401"));
  }
}
