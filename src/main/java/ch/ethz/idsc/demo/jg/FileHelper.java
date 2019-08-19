// code by gjoel
package ch.ethz.idsc.demo.jg;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.retina.util.io.DialogInput;

public enum FileHelper {
  ;
  public static Optional<File> open(String[] args) {
    return FileHelper.open(args.length > 0 ? args[0] : null);
  }

  public static Optional<File> open(String fileName) {
    if (Objects.nonNull(fileName)) {
      File file = new File(fileName);
      if (file.isFile()) {
        System.out.println("INFO open " + file.getAbsolutePath());
        return Optional.of(file);
      } else if (file.isDirectory())
        return DialogInput.chooseFile(fileName);
      System.err.println("WARN unable to find/open " + file.getAbsolutePath());
    }
    return DialogInput.chooseFile();
  }
}
