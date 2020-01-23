package io.vacco.dns;

import io.vacco.ufn.UFn;
import java.net.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DServer {

  private final DatagramSocket receiver;
  private final DProxy proxy;

  public DServer(DConfig cfg, DProxy proxy) {
    this.receiver = UFn.tryRt(() -> new DatagramSocket(
        cfg.listenPort, InetAddress.getByName(cfg.listenAddress)
    ));
    this.proxy = Objects.requireNonNull(proxy);
  }

  public void processRequest() {
    try {
      DatagramPacket response = proxy.process(DPacket.from(receiver)).get(10, TimeUnit.SECONDS);
      receiver.send(response);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void run() { processRequest(); }

  public static void main(String[] args) {
    DConfig cfg = DConfig.loadEnv();
    DServer server = new DServer(cfg,
        new DProxy(new InetSocketAddress(cfg.upstreamAddress, cfg.upstreamPort))
    );
    server.run();
  }
}
