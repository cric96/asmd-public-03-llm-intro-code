package it.unibo.utils;

public class LlmConstants {
    private LlmConstants() {
        // Prevent instantiation
    }

    public static final String OLLAMA_BASE_URL = "http://localhost:11434";
    public static final String CHAT_MODEL_SMOLLM = "smollm2:360m";
    public static final String CHAT_MODEL_QWEN = "qwen3.5:0.8b";
    public static final String EMBEDDING_MODEL = "mxbai-embed-large";

    // Common Model Parameters
    public static final int MAX_PREDICT_TOKENS = 128;
    public static final double DEFAULT_TEMPERATURE = 1.0;
}
