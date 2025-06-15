package io.extact.msa.spring.rms.console.service.adapter.remote.client;

import java.time.LocalDateTime;

import io.extact.msa.spring.rms.console.service.model.MemberReservationConsoleModel;

public record ReserveItemClientResponse(
        int id,
        LocalDateTime fromDateTime,
        LocalDateTime toDateTime,
        String note,
        int itemId,
        String serialNo,
        String itemName,
        int reserverId) {

    public MemberReservationConsoleModel toModel() {
        return MemberReservationConsoleModel.builder()
                .id(id)
                .fromDateTime(fromDateTime)
                .toDateTime(toDateTime)
                .note(note)
                .itemId(itemId)
                .serialNo(serialNo)
                .itemName(itemName)
                .reserverId(reserverId)
                .build();
    }
}
