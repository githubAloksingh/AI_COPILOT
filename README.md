# AI Work Copilot

AI Work Copilot is an end-to-end Generative AI & RAG assistant tailored for software development lifecycle (SDLC) teams. It transforms unstructured requirements, triages defects, generates test cases, drafts release notes, and formulates daily sprint updates using real team knowledge stored in Chroma Vector DB and MySQL.

---

## 🌟 Tech Stack

- **Backend**: Java 17, Spring Boot 3.3.4, Spring Data JPA, Flyway (MySQL 8.4)
- **Vector DB / RAG**: Chroma (No Docker required), Local Embedding / Google GenAI Embedding (`text-embedding-004`)
- **AI Model**: Google Gemini 2.5 Flash Lite (`gemini-2.5-flash-lite`)
- **Frontend**: Angular 22+, Standalone Components, SCSS, HttpClient, TypeScript
- **Parsing**: Apache PDFBox 3, Apache POI (DOCX), OpenCSV (CSV), Jackson (JSON), UTF-8 Stream Parser (TXT/MD)

---

## 🚀 Prerequisites

1. **Java 17**: Ensure Java 17 is installed.
2. **Node.js**: (Node.js 20+ or 25).
3. **MySQL 8.4**: Running locally on port 3306.
4. **Python 3.10+**: For Chroma vector store.
5. **Gemini API Key**: From Google AI Studio.

---

## 🛠️ Step-by-Step Setup

### 1. Database Setup (MySQL)

Create the database:

```sql
CREATE DATABASE IF NOT EXISTS ai_work_copilot;
```

### 2. Start Chroma Vector DB (NO DOCKER)

Install and run Chroma standalone using Python:

```bash
pip install chromadb
chroma run --path ./chroma_data --port 8000
```

_Chroma will run on `http://localhost:8000`._

### 3. Environment Variables

Copy `.env.example` to `.env` or export environment variables:

```bash
export DATABASE_URL=jdbc:mysql://localhost:3306/ai_work_copilot?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
export DATABASE_USERNAME=root
export DATABASE_PASSWORD=your_mysql_password_here
export GEMINI_API_KEY=your_actual_gemini_api_key_here
export GEMINI_MODEL=gemini-2.5-flash-lite
export CHROMA_URL=http://localhost:8000
```

### 4. Run Spring Boot Backend

```bash
cd backend
./start-backend.sh
```

_The backend starts at `http://localhost:8080` (Flyway will automatically run all migrations V1-V9)._

### 5. Run Angular Frontend

```bash
cd frontend
./start-frontend.sh
```

_The frontend will open at `http://localhost:4200`._

---

## 📂 Testing with Knowledge Base Data

Your knowledge base documents and CSVs are located in `AI Work_Copilot/FINAL_KNOWLEDGE_BASE/`:

- `AI Work_Copilot/FINAL_KNOWLEDGE_BASE/gold_evaluation_master_projects_1_6.csv`
- `AI Work_Copilot/FINAL_KNOWLEDGE_BASE/step18_category_summary.csv`
- `AI Work_Copilot/FINAL_KNOWLEDGE_BASE/step18_reranker_evaluation_results.csv`
- `AI Work_Copilot/FINAL_KNOWLEDGE_BASE/PRANAY_HANDOFF.md`
- `AI Work_Copilot/FINAL_KNOWLEDGE_BASE/README.md`

### End-to-End Workflow:

1. **Knowledge Base (`/knowledge-base`)**:
   - Click **Upload Document** and choose any file from `AI Work_Copilot/FINAL_KNOWLEDGE_BASE/` (e.g. `gold_evaluation_master_projects_1_6.csv`).
   - The CSV/Document parser parses the records, chunks them, generates embeddings, and saves them to Chroma.
2. **Requirement Assistant (`/requirements`)**:
   - Enter a title and problem (e.g. _Employee Leave Management_), click **Generate Requirement**.
   - Gemini retrieves relevant context from Chroma and returns a structured user story, acceptance criteria, assumptions, and edge cases.
3. **Test Generator (`/test-generator`)**:
   - Provide requirement details and choose Positive/Negative/Edge test types.
   - Generates structured test cases with preconditions, steps, and expected results.
4. **Defect Triage (`/defect-triage`)**:
   - Paste a bug description and stack trace.
   - The system retrieves similar historical defects from your CSV/documents in Chroma and outputs probable root causes and suggested fixes.
5. **Release Notes & Daily Status (`/release-notes`)**:
   - Formulate changelogs, new features, and daily standup notes.
6. **Audit & History (`/audit-history`) & Dashboard (`/dashboard`)**:
   - Every AI call logs execution time, sources, status, and input/output for full auditability.
   - Acceptance rates are dynamically calculated from user feedback.

---

## 🔒 Guardrails & Prompt Injection Protection

All ingested documents are treated as untrusted reference material. Built-in guardrails prevent malicious documents from altering system prompts or executing unwanted instructions.

# AI_COPILOT
