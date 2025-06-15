package io.extact.msa.spring.rms.console.service.adapter.remote.client;

import java.time.LocalDateTime;

import io.extact.msa.spring.rms.console.service.model.MemberReservationConsoleModel;

public record ReserveItemClientRequest(
        LocalDateTime fromDateTime,
        LocalDateTime toDateTime,
        String note,
        Integer itemId) {

    public static ReserveItemClientRequest from(MemberReservationConsoleModel model) {
        return new ReserveItemClientRequest(
                model.fromDateTime(),
                model.toDateTime(),
                model.note(),
                model.itemId());
    }
}
