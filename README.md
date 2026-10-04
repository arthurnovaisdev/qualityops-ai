# QualityOps AI Backend

Backend API for **QualityOps AI**, a quality management system focused on complaints, investigations, corrective actions, and AI-assisted analysis.

## Tech Stack

- Java 25
- Spring Boot 4.1.1
- Spring Security
- JWT Authentication
- Spring Data JPA
- PostgreSQL 16
- pgvector
- Spring AI
- Ollama
- Maven
- Docker

## Main Features

- User authentication and role-based access control
- Customer, product, and lot management
- Complaint management
- Evidence registration
- Investigation workflow
- Corrective action tracking
- AI-assisted complaint analysis
- AI-generated investigation suggestions with human review
- Semantic search for similar complaints using embeddings and pgvector
- AI execution audit and traceability
- Rate limiting and prompt injection protection

## AI Architecture

The AI acts only as an assistant.

It can:

- Suggest investigation hypotheses
- Identify missing information
- Recommend next steps
- Find semantically similar complaints

It cannot independently:

- Confirm root causes
- Change business statuses
- Close investigations
- Close complaints
- Write directly to the database

Critical decisions remain under human control.
