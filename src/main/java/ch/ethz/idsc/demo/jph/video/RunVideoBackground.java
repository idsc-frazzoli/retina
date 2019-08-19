// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.VideoBackground;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum RunVideoBackground {
  ;
  public static void main(String[] args) throws IOException {
    VideoBackground.render( //
        new File("/media/datahaki/data/gokart/lane/20190812/20190812T155500_00/log.lcm"), //
        VideoBackground._20190401, //
        HomeDirectory.Pictures("20190812T155500_00.png"));
  }
}
