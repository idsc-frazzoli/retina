// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.dvs.app.AedatLogStatistics;

enum AedatLogStatisticsDemo {
  ;
  public static void main(String[] args) throws Exception {
    AedatLogStatistics.of(Aedat.LOG_03.file);
  }
}
