// code by gjoel
package ch.ethz.idsc.demo.jg;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JFileChooser;

public enum FileHelper {
  ;
  public static Optional<File> open(String[] args) {
    return open(args.length > 0 ? args[0] : null);
  }

  public static Optional<File> open(String fileName) {
    if (Objects.nonNull(fileName)) {
      File file = new File(fileName);
      if (file.isFile()) {
        System.out.println("INFO open " + file.getAbsolutePath());
        return Optional.of(file);
      }
      System.err.println("WARN unable to find/open " + file.getAbsolutePath());
    }
    return choose();
  }

  public static Optional<File> choose() {
    JFileChooser fileChooser = new JFileChooser();
    int returnVal = fileChooser.showOpenDialog(fileChooser);
    if (returnVal == JFileChooser.APPROVE_OPTION)
      try {
        return Optional.of(fileChooser.getSelectedFile());
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    return Optional.empty();
  }
}
