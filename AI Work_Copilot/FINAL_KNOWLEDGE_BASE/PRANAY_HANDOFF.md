# Pranay Gupta - AI Work Copilot Handoff

## 1. Responsibility

My main responsibility was Data and Ingestion.

The goal was to prepare the project data and create a searchable knowledge base that can be used by the AI Work Copilot.

I also worked on the shared AI retrieval and orchestration prototype.

## 2. Knowledge Base

The final knowledge base contains:

- 972 searchable chunks
- 384-dimensional embeddings
- FAISS vector index
- Metadata for each chunk
- Project, document type and source information

Supported document types:

- Requirements
- Test Cases
- Defects
- Logs
- Release Notes

The knowledge base represents 6 projects.

## 3. Data Processing

The project data was cleaned and converted into searchable chunks.

Each chunk contains information such as:

- Chunk ID
- Project ID
- Document type
- Content
- Source record
- Metadata
- Related requirements/test cases/defects
- Release information where applicable

Empty and invalid chunks were removed/validated.

## 4. Embeddings and FAISS

Embedding model:

sentence-transformers/paraphrase-MiniLM-L3-v2

Embedding dimension:

384

The embeddings were stored in a FAISS vector index.

Final FAISS index:

vector_index.faiss

Metadata:

kb_embeddings_projects_1_6.csv

The vector count and metadata count were validated to match:

972 vectors = 972 metadata records.

## 5. Retrieval and Reranking

The current RAG prototype follows this flow:

User Question
→ Question Classification
→ Query Embedding
→ FAISS Retrieval
→ Top 30 Candidates
→ Reranking
→ Top 5 Sources
→ Grounded Prompt
→ Gemini
→ Structured Answer

The reranker improves the ordering of the initial FAISS results before they are passed to the LLM.

## 6. Guardrails

The AI is instructed to use only the retrieved project context.

It should not invent:

- Requirements
- Defects
- Test Cases
- Projects
- Other project facts

If the available context is insufficient, the system should say so.

The answer also includes source chunk IDs so that the retrieved information can be traced back to the knowledge base.

## 7. Evaluation

The retrieval/reranking pipeline was evaluated using the gold evaluation dataset.

Step 18 results:

- Hit@1: 66.67%
- Hit@5: 86.67%
- MRR: 0.7456

These results show that the majority of expected records are being retrieved within the top results.

## 8. Final Knowledge Base Files

The final folder contains:

- vector_index.faiss
- kb_embeddings_projects_1_6.csv
- gold_evaluation_master_projects_1_6.csv
- step18_reranker_evaluation_results.csv
- step18_category_summary.csv
- README.md

## 9. RAG Prototype

The working prototype demonstrates:

- Question classification
- FAISS retrieval
- Reranking
- Grounded RAG prompting
- Gemini answer generation
- Source attribution

The prototype can answer questions about requirements, defects, test cases and other project information.

## 10. Integration

The knowledge base and retrieval pipeline can now be integrated into the backend APIs.

The backend can use the knowledge base for:

- Requirement analysis
- Test-case generation
- Defect triage
- Release documentation

The frontend can consume the backend APIs and display the generated responses and sources.

## 11. Known Limitation

Some highly specific queries, particularly certain release-note queries, may not retrieve the expected record in the top results.

This is a retrieval-quality limitation rather than a missing knowledge-base validation step.

## 12. Handoff Status

Data ingestion and knowledge-base preparation are complete.

The retrieval and reranking prototype is working and evaluated.

The next stage is backend integration with the project APIs.