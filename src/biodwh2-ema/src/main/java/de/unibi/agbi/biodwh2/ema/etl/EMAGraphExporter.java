package de.unibi.agbi.biodwh2.ema.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.ema.EMADataSource;
import de.unibi.agbi.biodwh2.ema.model.EPAREntry;
import de.unibi.agbi.biodwh2.ema.model.HMPCEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class EMAGraphExporter extends GraphExporter<EMADataSource> {
    public EMAGraphExporter(final EMADataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys("name");
        final List<Set<String>> nameGroups = getNormalizedCompanyNameGroups();
        final Map<String, Long> nameNodeIdMap = new HashMap<>();
        for (final Set<String> group : nameGroups) {
            final Node node = graph.addNode("CompanyOrMarketingAuthHolder", "names", group.toArray(new String[0]));
            for (final String name : group)
                nameNodeIdMap.put(name, node.getId());
        }
        for (final EPAREntry entry : dataSource.EPAREntries) {
            final Node node = graph.addNodeFromModel(entry);
            if (entry.marketingAuthorisationHolderOrCompanyName != null)
                graph.addEdge(node, nameNodeIdMap.get(entry.marketingAuthorisationHolderOrCompanyName),
                              "ASSOCIATED_WITH");
        }
        for (final HMPCEntry entry : dataSource.HMPCEntries) {
            final Node node = graph.addNodeFromModel(entry);
            if (entry.use != null)
                for (final String use : StringUtils.split(entry.use, ','))
                    graph.addEdge(node, getOrCreateHerbalUseNode(graph, use.trim()), "USED_FOR");
        }
        return true;
    }

    private List<Set<String>> getNormalizedCompanyNameGroups() {
        final List<Set<String>> nameGroups = new ArrayList<>();
        for (final EPAREntry entry : dataSource.EPAREntries) {
            final String name = entry.marketingAuthorisationHolderOrCompanyName;
            if (name == null)
                continue;
            Set<String> group = findNameGroup(nameGroups, name);
            group = group != null ? group : findNameGroupWithAlternatives(nameGroups, name, "Ltd.", "Ltd", "Limited");
            group = group != null ? group : findNameGroupWithAlternatives(nameGroups, name, "B.V.", "B.V", "BV");
            group = group != null ? group : findNameGroupWithAlternatives(nameGroups, name, "S.A.", "S.A", "SA");
            group = group != null ? group : findNameGroupWithAlternatives(nameGroups, name, "S.A.S.", "S.A.S", "SAS");
            group = group != null ? group : findNameGroupWithAlternatives(nameGroups, name, "S.p.A.", "S.p.A", "SpA");
            if (group == null) {
                group = new HashSet<>();
                nameGroups.add(group);
            }
            group.add(name);
        }
        return nameGroups;
    }

    private Set<String> findNameGroup(final List<Set<String>> nameGroups, final String name) {
        for (final Set<String> group : nameGroups)
            if (group.contains(name))
                return group;
        return null;
    }

    private Set<String> findNameGroupWithAlternatives(final List<Set<String>> nameGroups, final String name,
                                                      final String... alternatives) {
        final Set<String> candidates = new HashSet<>();
        for (int i = 0; i < alternatives.length; i++)
            for (int j = 0; j < alternatives.length; j++)
                if (i != j)
                    candidates.add(name.replace(alternatives[i], alternatives[j]));
        for (final Set<String> group : nameGroups)
            if (!Collections.disjoint(group, candidates))
                return group;
        return null;
    }

    private long getOrCreateHerbalUseNode(final Graph graph, final String use) {
        Node node = graph.findNode("HerbalUse", "name", use);
        if (node == null)
            node = graph.addNode("HerbalUse", "name", use);
        return node.getId();
    }
}
