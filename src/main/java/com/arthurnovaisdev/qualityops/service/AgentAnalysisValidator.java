package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.response.ComplaintContextResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentAnalysisResponseDTO;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AgentAnalysisValidator {

    private static final Set<String> STOP_WORDS = Set.of(
            "possivel",
            "possivelmente",
            "problema",
            "problemas",
            "confirmada",
            "confirmado",
            "causa",
            "raiz",
            "pode",
            "podem",
            "ter",
            "sido",
            "para",
            "sobre",
            "como",
            "uma",
            "linha",
            "das",
            "dos",
            "com"
    );

    public AgentAnalysisResponseDTO validate(
            AgentAnalysisResponseDTO analysis,
            ComplaintContextResponseDTO context
    ) {

        List<String> hypotheses =
                filterHypotheses(
                        safe(analysis.hypotheses()),
                        context
                );

        List<String> missingInformation =
                filterMissingInformation(
                        safe(analysis.missingInformation()),
                        context
                );

        List<String> nextSteps =
                filterNextSteps(
                        safe(analysis.nextSteps()),
                        context
                );

        boolean effectivenessNotVerified =
                context.correctiveActions() != null
                        && context.correctiveActions()
                        .stream()
                        .anyMatch(action ->
                                action.completed()
                                        && !action.effectivenessVerified()
                        );

        if (effectivenessNotVerified) {

            missingInformation =
                    addIfAbsent(
                            missingInformation,
                            "Evidências sobre a eficácia da ação corretiva concluída."
                    );

            nextSteps =
                    addIfAbsent(
                            nextSteps,
                            "Verificar a eficácia da ação corretiva concluída."
                    );
        }

        return new AgentAnalysisResponseDTO(
                limit(hypotheses),
                limit(missingInformation),
                limit(nextSteps)
        );
    }

    private List<String> filterHypotheses(
            List<String> hypotheses,
            ComplaintContextResponseDTO context
    ) {

        if (context.investigation() == null
                || context.investigation().confirmedRootCause() == null) {

            return unique(hypotheses);
        }

        String rootCause =
                context.investigation()
                        .confirmedRootCause();

        return unique(
                hypotheses.stream()
                        .filter(hypothesis ->
                                !isGenericHypothesis(hypothesis)
                        )
                        .filter(hypothesis ->
                                !similar(
                                        hypothesis,
                                        rootCause
                                )
                        )
                        .toList()
        );
    }

    private List<String> filterMissingInformation(
            List<String> items,
            ComplaintContextResponseDTO context
    ) {

        return unique(
                items.stream()
                        .filter(item ->
                                !informationAlreadyKnown(
                                        item,
                                        context
                                )
                        )
                        .toList()
        );
    }

    private boolean informationAlreadyKnown(
            String item,
            ComplaintContextResponseDTO context
    ) {

        String text = normalize(item);

        if (context.investigation() != null
                && context.investigation().confirmedRootCause() != null
                && text.contains("causa raiz")) {
            return true;
        }

        if (context.complaint() != null) {

            if (context.complaint().customerName() != null
                    && text.contains("cliente")
                    && containsGenericInformationRequest(text)) {
                return true;
            }

            if (context.complaint().productName() != null
                    && text.contains("produto")
                    && containsGenericInformationRequest(text)) {
                return true;
            }

            if (context.complaint().lotCode() != null
                    && text.contains("lote")
                    && containsGenericInformationRequest(text)) {
                return true;
            }

            if (context.complaint().status() != null
                    && text.contains("status")) {
                return true;
            }
        }

        if (context.correctiveActions() != null
                && !context.correctiveActions().isEmpty()) {

            if (containsAny(
                    text,
                    "acao corretiva",
                    "acoes corretivas",
                    "medida corretiva",
                    "medidas corretivas"
            )) {
                return true;
            }

            if (text.contains("responsavel")
                    && context.correctiveActions()
                    .stream()
                    .anyMatch(action ->
                            action.responsibleName() != null
                    )) {
                return true;
            }

            if (containsAny(
                    text,
                    "prazo",
                    "vencimento"
            )
                    && context.correctiveActions()
                    .stream()
                    .anyMatch(action ->
                            action.deadline() != null
                    )) {
                return true;
            }

            if (containsAny(
                    text,
                    "data de conclusao",
                    "quando foi concluida",
                    "quando foi concluido"
            )
                    && context.correctiveActions()
                    .stream()
                    .anyMatch(action ->
                            action.actualCompletionDate() != null
                    )) {
                return true;
            }
        }

        if (context.evidences() != null
                && !context.evidences().isEmpty()
                && containsAny(
                text,
                "nao ha evidencias",
                "sem evidencias"
        )) {
            return true;
        }

        return false;
    }

    private List<String> filterNextSteps(
            List<String> steps,
            ComplaintContextResponseDTO context
    ) {

        boolean closedComplaint =
                context.complaint() != null
                        && "CLOSED".equalsIgnoreCase(
                        String.valueOf(
                                context.complaint().status()
                        )
                );

        boolean completedInvestigation =
                context.investigation() != null
                        && "COMPLETED".equalsIgnoreCase(
                        context.investigation().status()
                );

        return unique(
                steps.stream()
                        .filter(step ->
                                !repeatsCompletedAction(
                                        step,
                                        context
                                )
                        )
                        .filter(step ->
                                !triesToRediscoverRootCause(
                                        step,
                                        context
                                )
                        )
                        .filter(step ->
                                !unnecessarilyReopensClosedCase(
                                        step,
                                        closedComplaint,
                                        completedInvestigation
                                )
                        )
                        .toList()
        );
    }

    private boolean repeatsCompletedAction(
            String step,
            ComplaintContextResponseDTO context
    ) {

        if (context.correctiveActions() == null) {
            return false;
        }

        return context.correctiveActions()
                .stream()
                .filter(action ->
                        action.completed()
                )
                .anyMatch(action ->
                        similar(
                                step,
                                action.description()
                        )
                );
    }

    private boolean triesToRediscoverRootCause(
            String step,
            ComplaintContextResponseDTO context
    ) {

        if (context.investigation() == null
                || context.investigation().confirmedRootCause() == null) {
            return false;
        }

        String text = normalize(step);

        return text.contains("causa raiz")
                && containsAny(
                text,
                "identificar",
                "descobrir",
                "confirmar",
                "investigar"
        );
    }

    private boolean unnecessarilyReopensClosedCase(
            String step,
            boolean closedComplaint,
            boolean completedInvestigation
    ) {

        if (!closedComplaint
                || !completedInvestigation) {
            return false;
        }

        String text = normalize(step);

        return containsAny(
                text,
                "contatar o cliente",
                "contactar o cliente",
                "falar com o cliente",
                "obter mais informacoes do cliente"
        );
    }

    private boolean containsGenericInformationRequest(
            String text
    ) {

        return containsAny(
                text,
                "informacao",
                "informacoes",
                "dados",
                "nome",
                "identificacao"
        );
    }

    private boolean similar(
            String first,
            String second
    ) {

        if (first == null || second == null) {
            return false;
        }

        String firstNormalized =
                normalize(first);

        String secondNormalized =
                normalize(second);

        if (firstNormalized.contains(secondNormalized)
                || secondNormalized.contains(firstNormalized)) {
            return true;
        }

        Set<String> firstWords =
                keywords(firstNormalized);

        Set<String> secondWords =
                keywords(secondNormalized);

        if (firstWords.isEmpty()
                || secondWords.isEmpty()) {
            return false;
        }

        long commonWords =
                firstWords.stream()
                        .filter(secondWords::contains)
                        .count();

        int smallestSet =
                Math.min(
                        firstWords.size(),
                        secondWords.size()
                );

        double similarity =
                (double) commonWords
                        / smallestSet;

        return similarity >= 0.5;
    }

    private Set<String> keywords(
            String text
    ) {

        return Arrays.stream(
                        text.split("\\s+")
                )
                .filter(word ->
                        word.length() > 3
                )
                .filter(word ->
                        !STOP_WORDS.contains(word)
                )
                .collect(Collectors.toSet());
    }

    private List<String> unique(
            List<String> items
    ) {

        List<String> result =
                new ArrayList<>();

        for (String item : items) {

            if (item == null
                    || item.isBlank()) {
                continue;
            }

            boolean duplicate =
                    result.stream()
                            .anyMatch(existing ->
                                    similar(
                                            existing,
                                            item
                                    )
                            );

            if (!duplicate) {
                result.add(item.trim());
            }
        }

        return result;
    }

    private List<String> addIfAbsent(
            List<String> original,
            String value
    ) {

        List<String> result =
                new ArrayList<>(original);

        boolean exists =
                result.stream()
                        .anyMatch(item ->
                                similar(
                                        item,
                                        value
                                )
                        );

        if (!exists) {
            result.add(value);
        }

        return result;
    }

    private List<String> limit(
            List<String> items
    ) {

        return items.stream()
                .limit(3)
                .toList();
    }

    private List<String> safe(
            List<String> items
    ) {

        return items == null
                ? List.of()
                : items;
    }

    private boolean containsAny(
            String text,
            String... values
    ) {

        return Arrays.stream(values)
                .anyMatch(text::contains);
    }

    private String normalize(
            String text
    ) {

        return Normalizer
                .normalize(
                        text.toLowerCase(),
                        Normalizer.Form.NFD
                )
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9 ]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean isGenericHypothesis(String hypothesis) {

        Set<String> words =
                keywords(
                        normalize(hypothesis)
                );

        Set<String> genericWords = Set.of(
                "trinca",
                "produto",
                "processo",
                "fabricacao"
        );

        return !words.isEmpty()
                && genericWords.containsAll(words);
    }
}