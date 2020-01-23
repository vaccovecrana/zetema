package io.vacco.dns.plugin;

import io.vacco.dns.DPacket;
import io.vacco.ufn.UFn;
import org.xbill.DNS.*;

import java.net.InetAddress;
import java.util.*;

class RecordMatch {
  public RRset rSet;
  public Record match;

  public static RecordMatch from(RRset set, Record match) {
    RecordMatch m = new RecordMatch();
    m.rSet = set;
    m.match = match;
    return m;
  }
}

public class DAnswerMatchFilter implements DResponsePlugin {

  private final String domainSuffix;
  private final Set<String> filterMatches;

  public DAnswerMatchFilter(String domainSuffix, String ... exactMatches) {
    this.domainSuffix = Objects.requireNonNull(domainSuffix);
    this.filterMatches = new HashSet<>(Arrays.asList(exactMatches));
  }

  @Override
  public DPacket apply(DPacket request, DPacket response) {
    List<RecordMatch> matches = new ArrayList<>();
    for (RRset sectionRRset : response.message.getSectionRRsets(Section.ANSWER)) {
      sectionRRset.rrs().forEachRemaining(r0 -> {
        if (r0 instanceof ARecord) {
          ARecord r = (ARecord) r0;
          boolean domainMatch = r.getName().toString(true).endsWith(domainSuffix);
          boolean addrMatch = filterMatches.contains(r.getAddress().getHostAddress());
          if (domainMatch && addrMatch) {
            matches.add(RecordMatch.from(sectionRRset, r));
          }
        }
      });
    }
    matches.forEach(m -> {
      response.message.removeRecord(m.match, Section.ANSWER);
    });
    return response;
  }

}
