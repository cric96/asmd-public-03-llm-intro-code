# Exercise: AI-Powered Interactive Story (MVC)

Build an interactive text-adventure game where an LLM generates the narrative, questions, and
choices dynamically. The player makes decisions that affect their stats until the story ends.

## Exercise 1 — StoryResponse + JsonCodec (`e1.model` + `e1.engine`)

**Goal:** structured data exchange between your code and the LLM using Gson.

1. In `e1.model`, define `StoryResponse` — a record with: `narrative`, `question`, `choices`, `updatedPlayer`, `gameOver`.
2. In `e1.engine`, create `JsonCodec` wrapping Gson with `encode(Object)` and `decode(String, Class<T>)`.
3. Test:
   - Round-trip `Player` through encode → decode
   - Deserialize a JSON string to `StoryResponse` and check fields
   - Malformed JSON throws an exception

**SOLID:** Single Responsibility — codec only handles serialization.

---

## Exercise 2 — StoryPrompt (`e1.prompt`)

**Goal:** make each prompt a self-contained object.

1. In `e1.prompt`, define `StoryPrompt` interface with a single method `String toPromptString()`.
2. Implement `BeginPrompt(Player, String setting)` — formats the opening-scene prompt.
3. Implement `AdvancePrompt(Player, String previousQuestion, String chosenAction)` — formats the continuation prompt.
4. Both must instruct the LLM to respond with the `StoryResponse` JSON schema.
5. Test: verify each prompt contains the expected player name, stats, setting/choice.

**SOLID:** Open/Closed — new prompt types (e.g. `SummaryPrompt`) can be added without changing the engine.

---

## Exercise 3 — StoryView (`e1.view`)

**Goal:** decouple presentation from the model.

1. In `e1.view`, define `StoryView` interface:
   - `showBeat(String narrative, Player player, String question, List<String> choices)`
   - `showGameOver(Player player)`
   - `int readChoice(int numChoices)`
2. Implement `ConsoleStoryView` using `System.out` + `Scanner`.

**SOLID:** Interface Segregation — the view exposes only what the controller needs.

---

## Exercise 4 — StoryImpl (`e1.model`)

**Goal:** implement the `Story` interface using a `StoryEngine`.

1. Constructor takes `StoryEngine`, initial `Player`, and `setting`.
   On construction, calls `engine.request(new BeginPrompt(...))` to get the first beat.
2. `makeDecision(int)` creates an `AdvancePrompt` and calls `engine.request(...)`.
3. Enforce: `IllegalStateException` if game is over, `IllegalArgumentException` for bad index.
4. Test with a **mocked** `StoryEngine`:
   - Opening beat loads on construction
   - State advances correctly
   - Game-over / bad-index are rejected

**SOLID:** SRP (state only, no content generation), DIP (depends on `StoryEngine` interface).

---

## Exercise 5 — LLMStoryEngine (`e1.engine`)

**Goal:** implement `StoryEngine` backed by a ChatModel.

1. Single method: `StoryResponse request(StoryPrompt prompt)`.
2. Calls `prompt.toPromptString()`, sends to `ChatModel.chat(...)`.
3. **Extract JSON** from the response (handle markdown fences, preamble text).
4. Decode via `JsonCodec`. Retry up to N times, then throw `StoryEngineException`.
5. Test with a **mocked** `ChatModel`:
   - Valid JSON parses correctly
   - JSON inside ` ```json ` fencing is extracted
   - Retries on garbage, then succeeds
   - Throws after all retries exhausted
   - `extractJson` utility works on edge cases

**Key pattern:** the engine is prompt-agnostic — it doesn't know about begin vs. advance.

---

## Bonus — Extend and Refactor

- **Inventory system:** add `List<String> inventory` to `Player`, update prompts.
- **Story memory:** add a `ContextPrompt` that summarises the story so far for long-term coherence.
- **Persistence:** save/load game state to disk using `JsonCodec`.
- **Alternative view:** implement a Swing/web `StoryView` — model and engine stay unchanged.


