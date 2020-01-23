package io.vacco.dns;

import io.vacco.dns.plugin.DAnswerMatchFilter;
import io.vacco.ufn.UFn;
import org.junit.Test;

import java.net.InetAddress;

public class DServerTest {

  @Test
  public void sTest0() {
    UFn.tryRun(() -> {
      InetAddress a0 = InetAddress.getByName("127.0.0.1");
      InetAddress a1 = InetAddress.getByName("amazon.com");
      System.out.println(a0.getHostAddress());
      System.out.println(a1.getHostAddress());
    });
  }

  @Test
  public void sTest1() {
    DConfig cfg = DConfig.from("0.0.0.0", "8080", "9.9.9.9", "53");
    new DServer(cfg, new DProxy(cfg.upstreamAddress, cfg.upstreamPort)
        .withPlugin(new DAnswerMatchFilter("amazon.com", "176.32.103.205"))
    ).processRequest();
  }
}
