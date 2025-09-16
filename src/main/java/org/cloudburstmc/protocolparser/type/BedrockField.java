package org.cloudburstmc.protocolparser.type;

import com.google.gson.JsonObject;
import com.nukkitx.digraph.DiGraph;
import com.nukkitx.digraph.DiGraphNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BedrockField extends BedrockStructure {
    private final String name;
    private final String type;
    private final String notes;

    public static BedrockField parse(DiGraph graph, DiGraphNode nameNode) {
        DiGraphNode typeNode = graph.getEdges().higherEntry(nameNode.getId()).getValue().getNode2();

        String name = (String) nameNode.getAttribute("label");
        String notes = getNotes(nameNode);
        String type = (String) typeNode.getAttribute("label");

        return new BedrockField(name, type, notes);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if (notes.isEmpty()) {
            return type;
        } else {
            return "<table><tbody><tr><td>" + type + "</td><td>" + getMarkdownNotes(notes) + "</td></tr></tbody></table>";
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);

        JsonObject typeObj = new JsonObject();
        if (isPrimitive(type)) {
            typeObj.addProperty("primitive", type);
        } else {
            typeObj.addProperty("ref", getSafeTypeName(type));
        }

        // structured enum + description from notes
        ParsedNotes pn = parseNotesForJson(notes);
        if (pn.enumRef != null) {
            // Attach enum reference alongside the base type
            typeObj.addProperty("enum", pn.enumRef); // e.g., "LinkType"
        }
        json.add("type", typeObj);

        if (pn.description != null) {
            json.addProperty("description", pn.description);
        }

        return json;
    }

    private boolean isPrimitive(String type) {
        // Extend this as needed with other primitives you encounter
        return switch (type.toLowerCase()) {
            case "byte", "bool", "boolean", "int", "float", "double", "short", "long", "string" -> true;
            default -> false;
        };
    }
}
