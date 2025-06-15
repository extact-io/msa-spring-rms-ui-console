package io.extact.msa.spring.rms.console.service.adapter.remote.client;

import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;

public record ItemClientResponse(
        Integer id,
        String serialNo,
        String itemName) {

    public ItemConsoleModel toModel() {
        return ItemConsoleModel.builder()
                .id(this.id)
                .serialNo(this.serialNo)
                .itemName(this.itemName)
                .build();
    }
}
