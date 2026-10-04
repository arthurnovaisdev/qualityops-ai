package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.response.agent.AgentExecutionResponseDTO;
import com.arthurnovaisdev.qualityops.entity.AgentExecution;
import com.arthurnovaisdev.qualityops.entity.Complaint;
import com.arthurnovaisdev.qualityops.entity.User;
import com.arthurnovaisdev.qualityops.enums.AgentExecutionStatus;
import com.arthurnovaisdev.qualityops.enums.AgentOperationType;
import com.arthurnovaisdev.qualityops.repository.AgentExecutionRepository;
import com.arthurnovaisdev.qualityops.repository.ComplaintRepository;
import com.arthurnovaisdev.qualityops.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgentAuditService {

    private final AgentExecutionRepository agentExecutionRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    @Value("${spring.ai.ollama.chat.model}")
    private String model;

    public void register(
            UUID complaintId,
            AgentOperationType operation,
            AgentExecutionStatus status,
            String userMessage,
            String output,
            long durationMs
    ) {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Complaint complaint =
                complaintRepository.findById(complaintId)
                        .orElseThrow();

        AgentExecution execution =
                AgentExecution.builder()
                        .complaint(complaint)
                        .requestedBy(user)
                        .operation(operation)
                        .status(status)
                        .userMessage(userMessage)
                        .output(output)
                        .model(model)
                        .durationMs(durationMs)
                        .build();

        agentExecutionRepository.save(execution);
    }

    @Transactional(readOnly = true)
    public List<AgentExecutionResponseDTO> findRecent() {

        return agentExecutionRepository
                .findTop100ByOrderByCreatedAtDesc()
                .stream()
                .map(execution ->
                        new AgentExecutionResponseDTO(
                                execution.getId(),

                                execution.getComplaint().getId(),
                                execution.getComplaint().getTitle(),

                                execution.getRequestedBy().getId(),
                                execution.getRequestedBy().getName(),

                                execution.getOperation().name(),
                                execution.getStatus().name(),

                                execution.getUserMessage(),
                                execution.getOutput(),
                                execution.getModel(),
                                execution.getDurationMs(),
                                execution.getCreatedAt()
                        )
                )
                .toList();
    }
}