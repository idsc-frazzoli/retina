// code by gjoel
package ch.ethz.idsc.retina.util.io;

import java.io.File;
import java.util.Optional;

import javax.swing.JFileChooser;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/DialogInput.html">DialogInput</a> */
public enum DialogInput {
  ;
  public static Optional<File> chooseFile() {
    return chooseFile("");
  }

  public static Optional<File> chooseFile(String currentPath) {
    JFileChooser fileChooser = new JFileChooser(currentPath);
    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    int returnVal = fileChooser.showOpenDialog(fileChooser);
    if (returnVal == JFileChooser.APPROVE_OPTION)
      try {
        return Optional.of(fileChooser.getSelectedFile());
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    return Optional.empty();
  }

  public static void main(String[] args) {
    chooseFile();
  }
}
