// code by swisstrolley+
package ch.ethz.idsc.retina.util.sys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** ModuleWatchdog runs every PERIOD_S and checks if all the modules that should
 * be running are running as given by their threads. If not it will try to
 * restart the module.
 *
 * The initialization of this INSTANCE starts the watchdog. checkAlive is called
 * by the main method and will restart the module if the timeOut is exceeded.
 * This should be reliable as if the main fails... (it will never happen). */
public enum ModuleAuto {
  INSTANCE;
  /** map for holding the module list */
  // TODO JPH choose a more thread-safe data structure
  private final Map<Class<? extends AbstractModule>, AbstractModule> moduleMap = new LinkedHashMap<>();

  /** Methods for launching the modules
   * @throws Exception */
  public void runAll(List<Class<? extends AbstractModule>> modules) {
    System.out.println(new Date() + " Module Auto: Launch all");
    for (Class<? extends AbstractModule> module : modules)
      try {
        runOne(module);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    System.out.println(new Date() + " Module Auto: Launch all done");
  }

  /** terminates modules non-parallel and in reverse order of launching */
  public void endAll() {
    System.out.println(new Date() + " Module Auto: Terminate all");
    List<AbstractModule> list = new ArrayList<>(moduleMap.values());
    Collections.reverse(list);
    list.stream().forEach(AbstractModule::terminate);
    moduleMap.clear();
    System.out.println(new Date() + " Module Auto: Terminate all done");
  }

  /** @param module
   * @throws Exception */
  public void runOne(Class<? extends AbstractModule> module) throws Exception {
    synchronized (moduleMap) {
      if (moduleMap.containsKey(module)) {
        System.out.println(new Date() + " Module Auto: Already launched: " + module);
        return;
      }
    }
    AbstractModule abstractModule = module.getDeclaredConstructor().newInstance();
    System.out.println(new Date() + " Module Auto: Launching: " + module);
    abstractModule.launch();
    synchronized (moduleMap) {
      moduleMap.put(module, abstractModule);
    }
  }

  public void endOne(Class<? extends AbstractModule> module) {
    AbstractModule abstractModule = null;
    synchronized (moduleMap) {
      abstractModule = moduleMap.remove(module);
    }
    if (Objects.nonNull(abstractModule)) {
      System.out.println(new Date() + " Module Auto: Terminating: " + module);
      abstractModule.terminate();
    } else
      System.err.println("not registered: " + module.getSimpleName());
  }

  /** @param module
   * @return instance of module if module was started before or null */
  @SuppressWarnings("unchecked")
  public <T extends AbstractModule> T getInstance(Class<? extends AbstractModule> module) {
    return (T) moduleMap.get(module);
  }
}
