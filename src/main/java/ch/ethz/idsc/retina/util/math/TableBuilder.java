// code by jph
package ch.ethz.idsc.retina.util.math;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.MatrixQ;

/** concept similar to {@link StringBuilder}
 * 
 * potentially faster than
 * {@link Tensors#empty()} with subsequent {@link Tensor#append(Tensor)} */
public class TableBuilder {
  /** LinkedList was found to be the faster than ArrayDeque */
  private final Deque<Tensor> deque = new LinkedList<>();

  /** entries of given tensors are flattened into a vector,
   * which is appended as a row to the table.
   * 
   * @param tensors */
  public void appendRow(Tensor... tensors) {
    // TODO v051 use Flatten#of
    deque.add(Tensor.of(Stream.of(tensors).flatMap(tensor -> tensor.flatten(-1))));
  }

  /** @return number of rows */
  public int getRowCount() {
    return deque.size();
  }

  /** result satisfies {@link MatrixQ#of(Tensor)} if all rows have the same length.
   * 
   * @return tensor with rows as entries */
  public Tensor toTable() {
    return Tensor.of(deque.stream());
  }
}
