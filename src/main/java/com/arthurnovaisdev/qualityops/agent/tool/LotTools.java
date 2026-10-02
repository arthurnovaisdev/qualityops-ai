package com.arthurnovaisdev.qualityops.agent.tool;

import com.arthurnovaisdev.qualityops.dto.response.LotResponseDTO;
import com.arthurnovaisdev.qualityops.service.LotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LotTools {

    private final LotService lotService;

    @Tool(
            description = """
                    Busca informações de um lote pelo seu código.
                    """
    )
    public LotResponseDTO getLotInformation(String lotCode) {

        log.info(
                "AI TOOL -> getLotInformation | lotCode={}",
                lotCode
        );

        return lotService.findByCode(lotCode);
    }
}