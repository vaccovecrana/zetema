package io.vacco.dns;

import java.util.Objects;

public class DConfig {

  public String listenAddress, upstreamAddress;
  public int listenPort, upstreamPort;

  public static DConfig from(String listenAddress, String lp, String upstreamAddress, String up) {
    DConfig cfg = new DConfig();
    cfg.listenAddress = listenAddress != null ? listenAddress : "127.0.0.1";
    cfg.upstreamAddress = Objects.requireNonNull(upstreamAddress);
    cfg.listenPort = lp != null ? Integer.parseInt(lp) : 53;
    cfg.upstreamPort = up != null ? Integer.parseInt(up) : 53;
    return cfg;
  }

  public static DConfig loadEnv() {
    return from(
        System.getenv("DNS_FILTER_LISTEN_ADDRESS"),
        System.getenv("DNS_FILTER_LISTEN_PORT"),
        System.getenv("DNS_FILTER_UPSTREAM_ADDRESS"),
        System.getenv("DNS_FILTER_UPSTREAM_PORT")
    );
  }
}
