package org.cloudburstmc.protocolparser;

import com.google.gson.JsonObject;

public interface JsonParsable {

    JsonObject toJson();
}
