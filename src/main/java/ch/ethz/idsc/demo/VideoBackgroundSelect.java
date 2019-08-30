// code by gjoel
package ch.ethz.idsc.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import ch.ethz.idsc.demo.jg.FileHelper;

/** produces a high resolution image with lidar obstacles */
/* package */ enum VideoBackgroundSelect {
  ;
  public static void main(String[] args) throws IOException {
    Optional<File> optionalFile = FileHelper.open(args);
    if (optionalFile.isPresent()) {
      File file = optionalFile.get();
      try {
        VideoBackground.render(file);
      } catch (FileNotFoundException e) {
        // most common error is to select file instead of directory
        VideoBackground.render(file.getParentFile());
      }
    }
  }
}
