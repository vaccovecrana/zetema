package io.vacco.dns;

import io.vacco.dns.impl.DQuery;
import io.vacco.dns.schema.config.DProxy;
import io.vacco.ufn.UFn;
import org.junit.Test;

import java.io.File;
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
    DProxy cfg = DProxy.load(new File("./src/test/resources/config.yml"));
    DServer srv = new DServer(cfg);
    DQuery q = UFn.tryRt(() -> srv.processRequest().get());
    System.out.println(q.getRequest());
    System.out.println(q.getResponse());
    srv.respond(q);
  }
}
