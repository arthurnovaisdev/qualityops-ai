package com.arthurnovaisdev.qualityops.agent.tool;

import com.arthurnovaisdev.qualityops.dto.response.ComplaintResponseDTO;
import com.arthurnovaisdev.qualityops.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComplaintSearchTools {

    private final ComplaintService complaintService;

    @Tool(
            description = """
                    Busca reclamações anteriores semelhantes à reclamação informada.
                    Receba apenas o UUID da reclamação.
                    O próprio backend utiliza os dados reais dela para realizar a busca.
                    """
    )
    public List<ComplaintResponseDTO> searchSimilarComplaintsByComplaintId(
            UUID complaintId
    ) {

        log.info(
                "AI TOOL -> searchSimilarComplaintsByComplaintId | complaintId={}",
                complaintId
        );

        return complaintService.searchSimilarByComplaintId(
                complaintId
        );
    }
}