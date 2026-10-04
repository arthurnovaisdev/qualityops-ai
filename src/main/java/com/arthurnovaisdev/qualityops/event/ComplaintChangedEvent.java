package com.arthurnovaisdev.qualityops.event;

import java.util.UUID;

public record ComplaintChangedEvent(
        UUID complaintId
) {}