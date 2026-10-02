package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.response.ComplaintContextResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.InvestigationSuggestionResponseDTO;
import com.arthurnovaisdev.qualityops.exception.AgentSuggestionConflictException;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AgentSuggestionValidator {

    public void validate(
            String suggestion,
            ComplaintContextResponseDTO context,
            List<InvestigationSuggestionResponseDTO> existingSuggestions
    ) {


        boolean duplicatesCompletedAction =
                context.correctiveActions()
                        .stream()
                        .filter(action -> action.completed())
                        .anyMatch(action ->
                                similar(
                                        suggestion,
                                        action.description()
                                )
                        );

        if (duplicatesCompletedAction) {
            throw new AgentSuggestionConflictException(
                    "A sugestão gerada repete uma ação corretiva já concluída."
            );
        }

        boolean duplicatesExistingSuggestion =
                existingSuggestions
                        .stream()
                        .anyMatch(existing ->
                                similar(
                                        suggestion,
                                        existing.suggestion()
                                )
                        );

        if (duplicatesExistingSuggestion) {
            throw new AgentSuggestionConflictException(
                    "A sugestão gerada é semelhante a uma sugestão já existente."
            );
        }
    }

    private boolean similar(
            String first,
            String second
    ) {

        Set<String> firstWords =
                keywords(first);

        Set<String> secondWords =
                keywords(second);

        if (firstWords.isEmpty()
                || secondWords.isEmpty()) {
            return false;
        }

        long common =
                firstWords.stream()
                        .filter(secondWords::contains)
                        .count();

        int smallest =
                Math.min(
                        firstWords.size(),
                        secondWords.size()
                );

        return (double) common / smallest >= 0.5;
    }

    private Set<String> keywords(String text) {

        String normalized =
                Normalizer
                        .normalize(
                                text.toLowerCase(),
                                Normalizer.Form.NFD
                        )
                        .replaceAll("\\p{M}", "")
                        .replaceAll("[^a-z0-9 ]", "");

        return Arrays.stream(
                        normalized.split("\\s+")
                )
                .filter(word -> word.length() > 3)
                .collect(Collectors.toSet());
    }
}