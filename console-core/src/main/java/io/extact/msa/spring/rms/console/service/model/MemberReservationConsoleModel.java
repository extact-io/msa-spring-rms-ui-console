package io.extact.msa.spring.rms.console.service.model;

import java.time.LocalDateTime;

import io.extact.msa.spring.platform.core.generic.Transformable;
import lombok.Builder;

/**
 * 会員機能向けの予約モデル。
 * 会員機能では他人の予約は扱わないので必要な情報をフラットに持つようにしている。
 */
@Builder
public record MemberReservationConsoleModel(
        Integer id,
        LocalDateTime fromDateTime,
        LocalDateTime toDateTime,
        String note,
        int itemId,
        String serialNo,
        String itemName,
        int reserverId) implements Transformable {
}
