package io.extact.msa.spring.rms.console.service.adapter.remote.client;

import lombok.Builder;

@Builder
public record LoginClientRequest(
        String loginId,
        String password) {
}
