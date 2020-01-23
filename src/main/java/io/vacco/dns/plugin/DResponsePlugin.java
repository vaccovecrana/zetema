package io.vacco.dns.plugin;

import io.vacco.dns.DPacket;

public interface DResponsePlugin {

  DPacket apply(DPacket request, DPacket response);

}
