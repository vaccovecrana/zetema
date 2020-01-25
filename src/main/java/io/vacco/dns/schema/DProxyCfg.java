package io.vacco.dns.schema;

import io.vacco.ufn.UFn;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

public class DProxyCfg {

  public DListen listen;
  public List<DZoneCfg> zones;
  public boolean logAddresses;

  public static DProxyCfg load(Reader r) {
    Yaml y = new Yaml();
    return UFn.tryRt(() -> y.loadAs(r, DProxyCfg.class));
  }

  public static DProxyCfg load(File f) {
    return UFn.tryRt(() -> load(new FileReader(f)));
  }
}
