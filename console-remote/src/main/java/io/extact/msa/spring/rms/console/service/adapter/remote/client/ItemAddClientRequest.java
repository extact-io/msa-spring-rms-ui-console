package io.extact.msa.spring.rms.console.service.adapter.remote.client;

import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;

public record ItemAddClientRequest(
        String serialNo,
        String itemName) {

    public static ItemAddClientRequest from(ItemConsoleModel model) {
        return new ItemAddClientRequest(
                model.serialNo(),
                model.itemName());
    }
}
