package io.vacco.dns.schema.config;

import io.vacco.ufn.UFn;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

public class DProxy {

  public DListen listen;
  public List<DZone> zones;
  public boolean logAddresses;

  public static DProxy load(Reader r) {
    Yaml y = new Yaml();
    return UFn.tryRt(() -> y.loadAs(r, DProxy.class));
  }

  public static DProxy load(File f) {
    return UFn.tryRt(() -> load(new FileReader(f)));
  }
}
