package io.extact.msa.spring.rms.console.service.model;

import io.extact.msa.spring.platform.core.generic.Transformable;
import lombok.Builder;

@Builder
public record ItemConsoleModel(
        Integer id,
        String serialNo,
        String itemName) implements Transformable {
}
