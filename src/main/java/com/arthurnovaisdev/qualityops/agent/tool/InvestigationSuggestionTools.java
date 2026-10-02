package com.arthurnovaisdev.qualityops.agent.tool;

import com.arthurnovaisdev.qualityops.dto.request.InvestigationSuggestionRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.InvestigationSuggestionResponseDTO;
import com.arthurnovaisdev.qualityops.service.InvestigationSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvestigationSuggestionTools {

    private final InvestigationSuggestionService suggestionService;

    @Tool(
            description = """
        Registra uma sugestão de investigação para revisão humana.
        Use esta ferramenta quando, após analisar uma reclamação,
        quiser registrar hipóteses, informações ausentes,
        perguntas ou possíveis próximos passos.

        Esta ferramenta NÃO confirma causa raiz,
        NÃO altera status e NÃO conclui investigações.
        """
    )
    public InvestigationSuggestionResponseDTO createInvestigationSuggestion(
            UUID complaintId,
            String suggestion
    ) {
        InvestigationSuggestionRequestDTO dto =
                new InvestigationSuggestionRequestDTO(
                        complaintId,
                        suggestion
                );

        return suggestionService.create(dto);
    }
}