package e1.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonCodec {

    private final Gson gson;

    public JsonCodec() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public <T> T decode(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public String encode(Object obj) {
        return gson.toJson(obj);
    }
}
