package com.arthurnovaisdev.qualityops.agent.tool;

import com.arthurnovaisdev.qualityops.dto.response.CorrectiveActionResponseDTO;
import com.arthurnovaisdev.qualityops.service.CorrectiveActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CorrectiveActionTools {

    private final CorrectiveActionService correctiveActionService;

    @Tool(
            description = """
                    Busca as ações corretivas relacionadas a uma investigação.
                    """
    )
    public List<CorrectiveActionResponseDTO> getCorrectiveActions(
            UUID investigationId
    ) {

        log.info(
                "AI TOOL -> getCorrectiveActions | investigationId={}",
                investigationId
        );

        return correctiveActionService.findByInvestigation(
                investigationId
        );
    }
}