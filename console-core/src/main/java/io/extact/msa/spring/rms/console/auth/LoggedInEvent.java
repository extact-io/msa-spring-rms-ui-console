package io.extact.msa.spring.rms.console.auth;

import io.extact.msa.spring.platform.fw.domain.model.DomainEvent;
import lombok.Builder;

@Builder
public record LoggedInEvent(
        String userId,
        String roleName,
        String bearerToken,
        String loginId,
        String userName) implements DomainEvent {
}
