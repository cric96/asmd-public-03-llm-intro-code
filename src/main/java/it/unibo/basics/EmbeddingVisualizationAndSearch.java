package it.unibo.basics;

import it.unibo.utils.LlmConstants;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import it.unibo.utils.Vector;
import smile.manifold.TSNE;
import smile.plot.swing.ScatterPlot;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class EmbeddingVisualizationAndSearch {
    private static final int ELEMENTS_TO_SHOW = 5;
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        List<String> datasetFromResource;
        try {
            datasetFromResource = Files.readAllLines(Path.of(
                Objects.requireNonNull(
                    Thread.currentThread().getContextClassLoader().getResource("dataset.txt")
                ).toURI()
            ));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to read dataset", e);
        }
        // Embedding model definition
        final EmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
            .baseUrl(LlmConstants.OLLAMA_BASE_URL)
            .modelName(LlmConstants.EMBEDDING_MODEL)
            .logRequests(true)
            .logResponses(true)
            .build(); // Assume this is implemented
        // Effectively embed the dataset
        final List<Vector> datasetEmbeddings = datasetFromResource.stream()
            .map(embeddingModel::embed).map(response -> response.content().vector())
            .map(Vector::fromFloatArray)
            .toList();
        // Query examples
        final Vector questionOnSpace = Vector.fromFloatArray(
            embeddingModel.embed("Where is Jupyter?").content().vector()
        );
        Vector questionOnAnime = Vector.fromFloatArray(
            embeddingModel.embed("Give more info about Naruto!").content().vector()
        );
        // Visualization with t-SNE
        final double[][] allEmbeddings = Stream.concat(Stream.of(questionOnSpace, questionOnAnime), datasetEmbeddings.stream())
            .map(Vector::getData).toArray(double[][]::new);
        final var tsneFlatten = TSNE.fit(allEmbeddings);
        final int[] labels = Stream.concat(Stream.of(1, 2), Stream.generate(() -> 0).limit(datasetEmbeddings.size()))
            .mapToInt(Integer::intValue).toArray();
        final var plot = ScatterPlot.of(tsneFlatten.coordinates(), labels, 'x');
        final var canvas = plot.canvas();
        canvas.window().setVisible(true);
        // Search for closest embeddings
        final var findClosestToNaruto = findNClosest(questionOnAnime, datasetEmbeddings, ELEMENTS_TO_SHOW);
        final var findClosestToJupyter = findNClosest(questionOnSpace, datasetEmbeddings, ELEMENTS_TO_SHOW);
        System.out.println("Closest to Naruto: " + findClosestToNaruto.stream().map(datasetFromResource::get).toList());
        System.out.println("Closest to Jupyter: " + findClosestToJupyter.stream().map(datasetFromResource::get).toList());
    }

    private static List<Integer> findNClosest(Vector question, List<Vector> dataset, int howMany) {
        var indexes = Stream.iterate(0, i -> i + 1).limit(dataset.size()).toList();
        return indexes.stream()
            .sorted(Comparator
                .<Integer>comparingDouble(a -> dataset.get(a).cosineSimilarity(question))
                .reversed()
            )
            .limit(howMany)
            .toList();
    }
}
