package io.vacco.dns.plugin.aanswer;

import io.vacco.dns.impl.*;
import io.vacco.dns.plugin.DPlugin;
import io.vacco.dns.schema.config.DZone;
import io.vacco.ufn.UFn;
import org.xbill.DNS.*;

import java.net.InetAddress;

public class DAAnswer implements DPlugin {

  private final DAAnswerCfg answerCfg;

  public DAAnswer(DZone zoneCfg) {
    this.answerCfg = zoneCfg.plugins.aAnswer;
  }

  @Override
  public DQuery apply(DQuery query) {
    Message ans = (Message) query.getRequest().message.clone();
    answerCfg.with.forEach(ip -> ans.addRecord(
        UFn.tryRt(() -> new ARecord(
            ans.getQuestion().getName(), DClass.IN,
            answerCfg.ttl, InetAddress.getByName(ip)
        )), Section.ANSWER
    ));
    query.withReponse(DnsPacket.fromMessage(ans));
    return query;
  }
}
