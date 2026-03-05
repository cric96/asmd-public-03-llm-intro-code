package it.unibo.prompt.few;

import it.unibo.utils.LlmConstants;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.util.List;

public class FewShotAgentExample {
    public static void main(String[] args) {
        final ChatModel model = OllamaChatModel.builder()
            .baseUrl(LlmConstants.OLLAMA_BASE_URL)
            .logRequests(true)
            .logResponses(true)
            .modelName(LlmConstants.CHAT_MODEL_QWEN)
            .numPredict(LlmConstants.MAX_PREDICT_TOKENS)
            .build();
        final var examples = List.of(
            FewShotAgent.QuestionAnswer.from("Hi there!", "OK"),
            FewShotAgent.QuestionAnswer.from("You won a free vacation!", "SPAM"),
            FewShotAgent.QuestionAnswer.from("Urgent: Claim your prize now!", "SPAM")
        );
        final var agent = new FewShotAgent(model, examples);
        final var query = """
            Classify the following email:
            Q: Hey, how’s it going?
            A: ?
        """;
        final var response = agent.ask(query);
        System.out.println("Response: " + response);
    }
}
