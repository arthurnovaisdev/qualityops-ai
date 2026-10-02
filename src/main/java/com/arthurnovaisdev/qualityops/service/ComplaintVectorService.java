package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.response.ComplaintResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComplaintVectorService {

    private final VectorStore vectorStore;
    private final ComplaintService complaintService;

    public void indexComplaint(UUID complaintId) {

        ComplaintResponseDTO complaint =
                complaintService.findById(complaintId);

        String content = """
                Título: %s
                Descrição: %s
                Produto: %s
                Lote: %s
                """.formatted(
                complaint.title(),
                complaint.description(),
                complaint.productName(),
                complaint.lotCode()
        );

        Document document = Document.builder()
                .id(complaint.id().toString())
                .text(content)
                .metadata(Map.of(
                        "complaintId", complaint.id().toString(),
                        "type", "complaint"
                ))
                .build();

        vectorStore.add(
                List.of(document)
        );
    }

    public void indexAllComplaints() {

        List<ComplaintResponseDTO> complaints =
                complaintService.findAll();

        for (ComplaintResponseDTO complaint : complaints) {

            String content = """
                Título: %s
                Descrição: %s
                Produto: %s
                Lote: %s
                """.formatted(
                    complaint.title(),
                    complaint.description(),
                    complaint.productName(),
                    complaint.lotCode() != null
                            ? complaint.lotCode()
                            : "Não informado"
            );

            Document document = Document.builder()
                    .id(complaint.id().toString())
                    .text(content)
                    .metadata(Map.of(
                            "complaintId", complaint.id().toString(),
                            "type", "complaint"
                    ))
                    .build();

            vectorStore.add(
                    List.of(document)
            );
        }
    }

    public List<ComplaintResponseDTO> searchSimilarComplaints(
            UUID complaintId
    ) {

        ComplaintResponseDTO complaint =
                complaintService.findById(complaintId);

        String query = """
            Título: %s
            Descrição: %s
            Produto: %s
            Lote: %s
            """.formatted(
                complaint.title(),
                complaint.description(),
                complaint.productName(),
                complaint.lotCode() != null
                        ? complaint.lotCode()
                        : "Não informado"
        );

        List<Document> documents =
                vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(query)
                                .topK(5)
                                .similarityThreshold(0.70)
                                .filterExpression(
                                        "type == 'complaint'"
                                )
                                .build()
                );

        return documents.stream()
                .map(document ->
                        UUID.fromString(
                                document.getMetadata()
                                        .get("complaintId")
                                        .toString()
                        )
                )
                .filter(id ->
                        !id.equals(complaintId)
                )
                .map(complaintService::findById)
                .toList();
    }
}