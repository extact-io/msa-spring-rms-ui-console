package io.extact.msa.spring.rms.console.auth;

import io.extact.msa.spring.platform.fw.domain.event.DomainEvent;
import lombok.Builder;

@Builder
public record UserUpdatedEvent(
        String userId,
        String roleName,
        String userName) implements DomainEvent {
}
