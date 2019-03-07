// code by jph
package ch.ethz.idsc.retina.util.io;

import java.io.File;
import java.io.IOException;

/** recursive file/directory deletion
 * 
 * safety from erroneous use is enhanced by three criteria
 * 1) checking the depth of the directory tree T to be deleted
 * against a permitted upper bound "max_depth"
 * 2) checking the number of files to be deleted #F
 * against a permitted upper bound "max_count"
 * 3) if deletion of a file or directory fails, the process aborts
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/DeleteDirectory.html">DeleteDirectory</a> */
public class DeleteDirectory {
  /** Example: The command
   * DeleteDirectory.of(new File("/user/name/myapp/recordings/log20171024"), 2, 1000);
   * deletes given directory with sub directories of depth of at most 2,
   * and max number of total files less than 1000. No files are deleted
   * if directory tree exceeds 2, or total of files exceed 1000.
   * 
   * abort criteria are described at top of class
   * 
   * @param file
   * @param max_depth
   * @param max_count
   * @return
   * @throws Exception if given file does not exist, or criteria are not met */
  // TODO specify option to delete root
  // TODO max depth as 0 in order just to delete contents, rename to "max subfolder"
  public static DeleteDirectory of(File file, int max_depth, int max_count) throws IOException {
    return new DeleteDirectory(file, max_depth, max_count);
  }

  // ---
  private final File root;
  private final int max_depth;
  private int removed = 0;

  /** @param root file or a directory. If root is a file, the file will be deleted.
   * If root is a directory, the directory tree will be deleted.
   * @param max_depth of directory visitor
   * @param max_count of files to delete
   * @throws IOException */
  private DeleteDirectory(final File root, final int max_depth, final int max_count) throws IOException {
    this.root = root;
    this.max_depth = max_depth;
    // ---
    final int count = visitRecursively(root, 0, false);
    if (count <= max_count) // abort criteria 2)
      visitRecursively(root, 0, true);
    else
      throw new IOException("more files to be deleted than allowed (" + max_count + "<=" + count + ") in " + root);
  }

  private int visitRecursively(final File file, final int depth, final boolean delete) throws IOException {
    if (max_depth < depth) // enforce depth limit, abort criteria 1)
      throw new IOException("directory tree exceeds permitted depth");
    // ---
    int count = 0;
    if (file.isDirectory()) // if file is a directory, recur
      for (File entry : file.listFiles())
        count += visitRecursively(entry, depth + 1, delete);
    ++count; // count file as visited
    if (delete) {
      final boolean deleted = file.delete();
      if (!deleted) // abort criteria 3)
        throw new IOException("cannot delete " + file.getAbsolutePath());
      ++removed;
    }
    return count;
  }

  public int deletedCount() {
    return removed;
  }

  public void printNotification() {
    int count = deletedCount();
    if (0 < count)
      System.out.println("deleted " + count + " file(s) in " + root);
  }
}
