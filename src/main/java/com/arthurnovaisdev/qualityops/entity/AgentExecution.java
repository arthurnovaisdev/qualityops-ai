package com.arthurnovaisdev.qualityops.entity;

import com.arthurnovaisdev.qualityops.enums.AgentExecutionStatus;
import com.arthurnovaisdev.qualityops.enums.AgentOperationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agent_executions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentOperationType operation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentExecutionStatus status;

    @Column(length = 2000)
    private String userMessage;

    @Column(columnDefinition = "TEXT")
    private String output;

    @Column(nullable = false)
    private String model;

    private Long durationMs;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}