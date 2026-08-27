# AI Work Copilot - Knowledge Base

## Owner

Pranay Gupta

## Responsibility

Data and Ingestion

This package contains the searchable knowledge base prepared for the AI Work Copilot SDLC workflow.

## Purpose

The knowledge base supports retrieval of information from internal SDLC data including:

- Requirements
- Test cases
- Defects
- Logs
- Release information

## Knowledge Base Statistics

- Projects: 6
- Documents: 964
- Chunks: 972
- Unique chunk IDs: 972
- Empty chunks: 0
- FAISS vectors: 972
- Embedding dimension: 384
- Embedding model: sentence-transformers/paraphrase-MiniLM-L3-v2

## Document Distribution

| Document Type | Chunks |
|---|---:|
| Log | 324 |
| Test Case | 300 |
| Defect | 230 |
| Requirement | 96 |
| Release | 22 |
| **Total** | **972** |

## Files

### vector_index.faiss

FAISS vector index containing 972 vectors.

The vector dimension is 384.

### kb_embeddings_projects_1_6.csv

Knowledge-base metadata and chunk information.

It contains:

- Chunk IDs
- Document IDs
- Project IDs
- Document types
- Chunk content
- Source information
- Provenance
- Embedding information
- Release metadata
- Other metadata fields

### gold_evaluation_master_projects_1_6.csv

Gold evaluation dataset containing 60 evaluation records.

### step18_reranker_evaluation_results.csv

Results from the reranker evaluation.

### step18_category_summary.csv

Category-level evaluation results from Step 18.

## Retrieval Evaluation

The latest Step 18 evaluation produced:

- Hit@1: 66.67%
- Hit@5: 86.67%
- MRR: 0.7456

Baseline Step 15 results:

- Hit@1: 63.33%
- Hit@5: 86.67%
- MRR: 0.7239

The reranker therefore improved Hit@1 and MRR.

## Validation

The knowledge base passed the final Step 20 validation.

Validated items:

- Required files present
- Metadata loaded successfully
- Chunk IDs are unique
- No empty chunk content
- FAISS index loads successfully
- FAISS vector count matches metadata records
- Embedding dimensions match
- Embedding model metadata is present

## Known Retrieval Limitation

The Release Notes category contains some Gold Evaluation questions whose expected information is not present in the current knowledge-base content.

For example, the Gold evaluation references concepts such as calculator/datetime and web research/source references, while the corresponding release chunks do not contain those terms.

These cases should not be artificially added to the knowledge base without corresponding source data.

## Handoff

The knowledge base can be consumed by the backend/RAG layer.

The expected retrieval flow is:

User Query
    |
    v
Query Embedding
    |
    v
FAISS Search
    |
    v
Retrieve Relevant Chunks
    |
    v
Reranking
    |
    v
Context for AI Generation

## Owner Scope

This package represents the Data and Ingestion responsibility:

1. Collect SDLC data
2. Clean and prepare text
3. Create chunks
4. Generate embeddings
5. Create searchable vector index
6. Validate retrieval
7. Evaluate retrieval quality
8. Provide the searchable knowledge base for the team

Backend APIs, Angular frontend, authentication, and other application components are outside this package.