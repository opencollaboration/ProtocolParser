package org.cloudburstmc.protocolparser.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nukkitx.digraph.DiGraph;
import com.nukkitx.digraph.DiGraphNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.cloudburstmc.protocolparser.JsonParsable;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BedrockType implements JsonParsable {
    private final String name;
    private final List<BedrockStructure> structures;


    public static BedrockType parse(DiGraph graph) {
        DiGraphNode root = graph.getNodes().values().iterator().next();
        String name = graph.getId();
        name = name.substring(1, name.length() - 1);

        List<BedrockStructure> structures;
        if (!root.getAttributes().isEmpty()) {
            structures = BedrockStructure.parseStructures(graph, root);
        } else {
            structures = Collections.emptyList();
        }

        return new BedrockType(name, structures);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<table><thead><tr><th>Field</th><th>Info</th></tr></thead><tbody>\n");
        for (BedrockStructure structure : structures) {
            builder.append("<tr><td>")
                    .append(structure.getName())
                    .append("</td><td>").append(structure.toString().replaceAll("\n", "\n  "))
                    .append("</td></tr>").append('\n');
        }
        builder.append("</tbody></table>");
        return builder.toString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", getName());

        JsonArray arr = new JsonArray();
        for (BedrockStructure s : structures) {
            arr.add(s.toJson());
        }
        json.add("structures", arr); // include empty array if none
        return json;
    }

    public String getName() {
        return BedrockStructure.getSafeTypeName(name);
    }
}
