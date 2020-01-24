package io.vacco.dns.plugin.forward;

import io.vacco.dns.impl.*;
import io.vacco.dns.plugin.DPlugin;
import io.vacco.dns.schema.DZoneCfg;
import io.vacco.ufn.UFn;
import java.net.*;

public class DForward implements DPlugin {

  private ThreadLocal<DatagramSocket> upstreamDs = ThreadLocal.withInitial(() -> UFn.tryRt(DatagramSocket::new));
  private InetSocketAddress upstreamAddress;

  public DForward(DZoneCfg zoneCfg) {
    this.upstreamAddress = new InetSocketAddress(
        zoneCfg.plugins.forward.host, zoneCfg.plugins.forward.port
    );
  }

  @Override
  public DQuery apply(DQuery query) throws Exception {
    DPacket request = query.getRequest();
    DatagramSocket us = upstreamDs.get();
    us.send(request.markFor(upstreamAddress));
    DPacket response = DPacket.from(us);
    response.message.getHeader().setID(request.getId());
    return query.withReponse(response);
  }
}
