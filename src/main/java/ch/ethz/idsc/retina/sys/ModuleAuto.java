// code by swisstrolley+
package ch.ethz.idsc.retina.sys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** ModuleWatchdog runs every PERIOD_S and checks if all the modules that should
 * be running are running as given by their threads. If not it will try to
 * restart the module.
 *
 * The initialisation of this INSTANCE starts the watchdog. checkAlive is called
 * by the main method and will restart the module if the timeOut is exceeded.
 * This should be reliable as if the main fails... (it will never happen). */
public enum ModuleAuto {
  INSTANCE;
  /** map for holding the module list */
  private Map<Class<?>, AbstractModule> moduleMap = new LinkedHashMap<>();

  /** Methods for launching the modules */
  public void runAll(List<Class<?>> modules) {
    System.out.println(new Date() + " Module Auto: Launch all");
    for (Class<?> module : modules)
      runOne(module);
    System.out.println(new Date() + " Module Auto: Launch all done");
  }

  /** terminates modules non-parallel and in reverse order of launching */
  public void terminateAll() {
    System.out.println(new Date() + " Module Auto: Terminate all");
    List<AbstractModule> list = new ArrayList<>(moduleMap.values());
    Collections.reverse(list);
    list.stream().forEach(AbstractModule::terminate);
    moduleMap.clear();
    System.out.println(new Date() + " Module Auto: Terminate all done");
  }

  public void runOne(Class<?> module) {
    if (moduleMap.containsKey(module)) {
      System.out.println(new Date() + " Module Auto: Already launched: " + module);
      return;
    }
    try {
      AbstractModule instance = (AbstractModule) module.newInstance();
      System.out.println(new Date() + " Module Auto: Launching: " + module);
      instance.launch();
      moduleMap.put(module, instance);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void terminateOne(Class<?> module) {
    if (moduleMap.containsKey(module)) {
      AbstractModule abstractModule = moduleMap.get(module);
      moduleMap.remove(module);
      System.out.println(new Date() + " Module Auto: Terminating: " + module);
      abstractModule.terminate();
    } else
      System.err.println("not registered: " + module.getSimpleName());
  }
}
