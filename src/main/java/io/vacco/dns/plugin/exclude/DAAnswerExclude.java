package io.vacco.dns.plugin.exclude;

import io.vacco.dns.DPacket;
import io.vacco.dns.plugin.DResponsePlugin;
import org.slf4j.*;
import org.xbill.DNS.*;
import java.util.*;

public class DAAnswerExclude implements DResponsePlugin {

  private final String domainSuffix;
  private final Set<String> filterMatches;

  public DAAnswerExclude(String domainSuffix, String ... exactMatches) {
    this.domainSuffix = Objects.requireNonNull(domainSuffix);
    this.filterMatches = new HashSet<>(Arrays.asList(exactMatches));
    Logger log = LoggerFactory.getLogger(DAAnswerExclude.class);
    log.info(toString());
  }

  @Override
  public DPacket apply(DPacket request, DPacket response) {
    List<ARecord> aRecords = new ArrayList<>();
    for (RRset sectionRRset : response.message.getSectionRRsets(Section.ANSWER)) {
      sectionRRset.rrs().forEachRemaining(r0 -> {
        if (r0 instanceof ARecord) {
          aRecords.add((ARecord) r0);
        }
      });
    }
    aRecords.stream()
        .filter(ar0 -> ar0.getName().toString(true).endsWith(domainSuffix))
        .filter(ar0 -> filterMatches.contains(ar0.getAddress().getHostAddress()))
        .forEach(ar0 -> response.message.removeRecord(ar0, Section.ANSWER));
    return response;
  }

  public static DAAnswerExclude from(String cfg) {
    return new DAAnswerExclude(
        cfg.split(";")[0],
        cfg.split(";")[1].split(",")
    );
  }

  public static DAAnswerExclude loadEnv() {
    return from(System.getenv("ZETEMA_PLUGIN_ANSWER_EXCLUDE"));
  }

  @Override
  public String toString() {
    return "DAAnswerExclude{" +
        "domainSuffix='" + domainSuffix + '\'' +
        ", filterMatches=" + filterMatches +
        '}';
  }
}
