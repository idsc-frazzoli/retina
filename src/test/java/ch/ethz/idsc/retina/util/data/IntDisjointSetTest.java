// code by jph
package ch.ethz.idsc.retina.util.data;

import java.util.Collection;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class IntDisjointSetTest extends TestCase {
  public void testSimple() {
    int n = 100;
    IntDisjointSet disjointSet = new IntDisjointSet(n);
    assertEquals(disjointSet.parents().size(), n);
    assertEquals(disjointSet.representatives().size(), n);
    Distribution distribution = DiscreteUniformDistribution.of(0, n);
    for (int index = 0; index < n; ++index) {
      Scalar scalar = RandomVariate.of(distribution);
      disjointSet.union(index, scalar.number().intValue());
    }
    {
      int maxDepth = IntStream.range(0, n).map(disjointSet::depth).reduce(Math::max).getAsInt();
      assertTrue(1 < maxDepth);
    }
    Collection<Integer> parents = disjointSet.parents(); // before representatives()
    Collection<Integer> representatives = disjointSet.representatives(); // invokes find() on all members
    assertFalse(parents.equals(representatives));
    {
      int maxDepth = IntStream.range(0, n).map(disjointSet::depth).reduce(Math::max).getAsInt();
      assertEquals(maxDepth, 1);
    }
    assertEquals(disjointSet.representatives(), disjointSet.parents());
  }

  public void testSingle() {
    int n = 1000;
    IntDisjointSet disjointSet = new IntDisjointSet(n);
    for (int index = 0; index < n; ++index)
      disjointSet.union(index, 2);
    assertEquals(disjointSet.parents().size(), 1);
    assertEquals(disjointSet.maxRank(), 1);
    assertEquals(disjointSet.parents(), disjointSet.representatives());
    assertEquals(disjointSet.parents().size(), 1);
  }

  public void testDual() {
    int n = 1000;
    IntDisjointSet disjointSet = new IntDisjointSet(n);
    for (int index = 0; index < n; ++index)
      disjointSet.union(index, 100 + (index % 2));
    assertEquals(disjointSet.parents().size(), 2);
    assertEquals(disjointSet.maxRank(), 1);
    assertEquals(disjointSet.parents(), disjointSet.representatives());
    assertEquals(disjointSet.parents().size(), 2);
  }

  public void testSame() {
    int n = 1000;
    IntDisjointSet disjointSet = new IntDisjointSet(n);
    for (int index = 0; index < n; ++index)
      disjointSet.union(index, index);
    assertEquals(disjointSet.parents().size(), n);
    // assertEquals(disjointSet.maxRank(), 1);
    assertEquals(disjointSet.parents(), disjointSet.representatives());
    assertEquals(disjointSet.parents().size(), n);
  }
}
