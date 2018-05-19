// code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Flatten;
import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRange;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.SquaredEuclideanDistanceFunction;

public enum ClustersTracking {
  ;
  /** @param oldClusters
   * @param newScan latest lidar points
   * @param eps parameter for DBSCAN
   * @param minPoints parameter for DBSCAN */
  // TODO also handle empty input
  public static void elkiDBSCAN(ClusterCollection oldClusters, Tensor newScan, double eps, int minPoints) {
    Tensor scans = oldClusters.toMatrices().append(newScan);
    oldClusters.getCollection().forEach(ClusterDeque::appendEmpty);
    int sizeCollection = oldClusters.getCollection().size();
    int[] array = scans.stream().mapToInt(Tensor::length).toArray();
    NavigableMap<Integer, Integer> origin = partitionMap(array, Function.identity());
    Tensor matrix = Flatten.of(scans, 1);
    Database database = StaticHelper.database(matrix);
    Stopwatch stopwatch = Stopwatch.started();
    DBSCAN<NumberVector> dbscan = //
        new DBSCAN<>(SquaredEuclideanDistanceFunction.STATIC, eps, minPoints);
    Clustering<Model> clustering = dbscan.run(database);
    long ns = stopwatch.display_nanoSeconds();
    System.out.println((ns * 1e-6) + "ms");
    for (Cluster<Model> cluster : clustering.getAllClusters())
      if (!cluster.isNoise()) {
        NavigableMap<Integer, Tensor> navigableMap = partitionMap(array, i -> Tensors.empty());
        DBIDs ids = cluster.getIDs();
        Relation<NumberVector> relation = database.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
        DBIDRange idRange = (DBIDRange) relation.getDBIDs();
        NavigableSet<Integer> navigableSet = new TreeSet<>();
        for (DBIDIter iter = ids.iter(); iter.valid(); iter.advance()) {
          int offset = idRange.getOffset(iter);
          navigableSet.add(origin.floorEntry(offset).getValue());
          Entry<Integer, Tensor> floorEntry = navigableMap.floorEntry(offset);
          floorEntry.getValue().append(matrix.get(offset));
        }
        if (navigableSet.contains(sizeCollection))
          switch (navigableSet.size()) {
          case 1:
            oldClusters.addToCollection(navigableMap.lastEntry().getValue());
            break;
          case 2:
            Tensor points = navigableMap.lastEntry().getValue();
            ClusterDeque next = oldClusters.getCollection().get(navigableSet.iterator().next());
            next.replaceLast(points);
            break;
          default:
            oldClusters.addToCollection(navigableMap.lastEntry().getValue());
            System.out.println("case " + navigableSet.size());
            break;
          }
        else {
          // TODO comment on this case, unhandled?
        }
      }
    oldClusters.maintainUntil(sizeCollection);
  }

  private static <T> NavigableMap<Integer, T> partitionMap(int[] array, Function<Integer, T> function) {
    NavigableMap<Integer, T> navigableMap = new TreeMap<>();
    int sum = 0;
    for (int index = 0; index < array.length; ++index) {
      navigableMap.put(sum, function.apply(index));
      sum += array[index];
    }
    return navigableMap;
  }
}
