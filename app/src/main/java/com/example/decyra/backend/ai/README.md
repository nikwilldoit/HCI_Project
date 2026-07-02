# AI Agent Architecture

The application implements a **Retrieval-Augmented Generation (RAG)** pipeline with **semantic routing** and **multi-stage re-ranking** to improve both response quality and system efficiency.

## Pipeline Overview

1. The client sends an **HTTP request** to the API server.
2. A **binary classifier (semantic router)** determines whether the query requires **Retrieval-Augmented Generation (RAG)** or can be answered using the existing conversation context.
3. If **RAG** is required:

   * The user query is converted into vector embeddings using the **OpenAI Embeddings API**.
   * The embedding is used to search the **Pinecone Vector Database**.
   * Pinecone retrieves the **top-20 most similar documents** based on **cosine similarity** between the query embedding and the stored vectors.
   * The retrieved documents are passed to the **Cohere Re-ranker**, which selects the **top-5 most relevant documents**.
   * The selected documents, together with their metadata and the user's query, are provided as context to the LLM.
4. If **RAG** is **not** required:

   * The LLM receives only the **last six messages** of the conversation retrieved from **Firebase**, allowing it to answer using the recent chat context without performing vector search.
5. The **LLM** generates a context-aware response while following the predefined system instructions.
6. The generated response is stored in **Firebase** and returned to the user's interface. Under normal operating conditions, the complete pipeline responds in **about 10 seconds** *(assuming a stable internet connection)*.

## Performance Optimizations

The pipeline includes two optimization mechanisms that significantly improve both performance and reliability:

* **Semantic Routing:** Before executing the RAG pipeline, a lightweight binary classifier determines whether document retrieval is actually required. For conversational follow-up questions, the system skips the embedding generation, vector search, and re-ranking stages, reducing latency, API calls, and network bandwidth. This also minimizes the risk of request timeouts caused by unnecessary data transfers.

* **Multi-stage Re-ranking:** Instead of sending all retrieved documents to the LLM, only the **top-5** most relevant results selected by the **Cohere Re-ranker** are included in the prompt. This reduces the number of input tokens, lowers inference time and cost, and improves the relevance of the context provided to the language model.

## Technologies

| Component             | Purpose                                                                        |
| --------------------- | ------------------------------------------------------------------------------ |
| **OpenAI Embeddings** | Converts user queries into vector representations for semantic search.         |
| **Pinecone**          | Performs semantic similarity search over the vector database.                  |
| **Cohere Re-ranker**  | Re-ranks retrieved documents to improve retrieval relevance before generation. |
| **OpenAI LLM**        | Generates the final context-aware response.                                    |
| **Firebase**          | Stores conversation history and generated responses.                           |

## System Architecture Diagram
<p align="center">
  <img width="604" alt="AI Response Pipeline" src="https://github.com/user-attachments/assets/47e0acef-208a-40b8-ada6-c99acba2bf36" />
</p>

## Code Structure

```text
ai/
├── callbacks/
│   ├── ChatCallback.java
│   ├── EmbeddingCallback.java
│   ├── MessagesCallback.java
│   ├── PineconeCallback.java
│   ├── RerankCallback.java
│   └── RouterCallback.java
├── clients/
│   ├── CohereClient.java
│   ├── OpenAIChatClient.java
│   ├── PineconeClient.java
│   └── RouterClient.java
└── indexers/
    ├── PineconeIndexerCareer.java
    ├── PineconeIndexerErasmus.java
    └── PineconeIndexerMaster.java
```

