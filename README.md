# Large Language Model - Primer
## Practical Guide to Large Language Models Usage

This primer covers the essentials of working with Large Language Models (LLMs) for various applications. You'll learn:

1. How to programmatically interact with LLMs
2. How to leverage embeddings for text similarity search
3. How to generate text using LLMs
4. Some prompt engineering techniques
5. A simple application with an LLM in the loop

## Requirements

We'll use the `ollama` library to download and run pre-trained LLMs.
We will also leverage gemini as a reference for state of the art LLMs.

- Install the library following instructions in the [official repository](https://ollama.com/download)
- Download the required models:
```bash
# Small LLM model
ollama pull smollm2:135m

# Large embedding model
ollama pull mxbai-embed-large

# Good performing SLM model
ollama pull qwen3.5:0.8b
```

To check the available models, run:
```bash
ollama list
```
To verify the local installation, just run:
```bash
ollama run smollm2:135m
```

For this primer, we'll use:
- `smollm2:135m` - a small LLM model
- `mxbai-embed-large` - a large embedding model
- `qwen3:0.6b` - as a good performing SLM model
- A recent model from google (gemini or gemma) as gold reference (please get the gemini api from here: https://aistudio.google.com/api-keys)


Feel free to experiment with other models to compare performance.

While ollama is accessed via HTTP requests, we'll use [langchain4j](https://github.com/langchain4j/langchain4j) to simplify interactions. We'll also use the `smile` library for visualizations and mathematical operations.

## Project Structure

The project is organized into three sections:

- **`it.unibo.basics`**: Core LLM interaction classes
    - `EmbeddingBaseExample`: Demonstrates embedding-based text similarity search
    - `EmbeddingVisualizationAndSearch`: Shows embedding visualization and search techniques
    - `TextGenerationExample`: Covers text generation with LLMs

- **`it.unibo.chat`**: Contains examples of agent-to-agent interactions

- **`it.unibo.prompt`**: Demonstrates prompt engineering techniques to enhance LLM performance

## Advanced materials

For the curious, here is an example where we create a "semi-autonomous" pipeline for code generation: https://github.com/nicolasfara/experiments-2025-acm-iot-ac-llm.
