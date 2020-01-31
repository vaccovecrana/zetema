package io.vacco.dns.plugin.forward;

import io.vacco.dns.impl.*;
import io.vacco.dns.plugin.DPlugin;
import io.vacco.dns.schema.DZoneCfg;
import io.vacco.ufn.UFn;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

public class DForward implements DPlugin {

  private Random r = new Random();
  private ThreadLocal<DatagramSocket> upstreamDs = ThreadLocal.withInitial(() -> UFn.tryRt(DatagramSocket::new));
  private InetSocketAddress[] upstreams;

  public DForward(DZoneCfg zoneCfg) {
    this.upstreams = Arrays.stream(zoneCfg.plugins.forward.to)
        .map(fw -> new InetSocketAddress(fw.ip, fw.port))
        .toArray(InetSocketAddress[]::new);
  }

  @Override
  public DQuery apply(DQuery query) throws Exception {
    DPacket request = query.getRequest();
    DatagramSocket us = upstreamDs.get();
    int targetIdx = upstreams.length == 1 ? 0 : r.nextInt(upstreams.length);
    InetSocketAddress target = upstreams[targetIdx];
    us.send(request.markFor(target));
    DPacket response = DPacket.from(us);
    response.message.getHeader().setID(request.getId());
    return query.withReponse(response);
  }
}
