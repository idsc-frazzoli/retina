// code by jph
package ch.ethz.idsc.demo.jph.davis;

import ch.ethz.idsc.retina.davis.app.AedatLogStatistics;

enum AedatLogStatisticsDemo {
  ;
  public static void main(String[] args) throws Exception {
    AedatLogStatistics.of(Aedat20.LOG_01.file);
  }
}
