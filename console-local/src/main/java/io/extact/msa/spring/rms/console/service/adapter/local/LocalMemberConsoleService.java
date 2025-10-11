package io.extact.msa.spring.rms.console.service.adapter.local;

import java.time.LocalDate;
import java.util.List;

import io.extact.msa.spring.rms.application.member.ReserveItemCommand;
import io.extact.msa.spring.rms.application.member.ReserveItemQueryCondition;
import io.extact.msa.spring.rms.application.member.ReserveItemService;
import io.extact.msa.spring.rms.console.service.MemberConsoleService;
import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;
import io.extact.msa.spring.rms.console.service.model.MemberReservationConsoleModel;
import io.extact.msa.spring.rms.domain.item.model.ItemId;
import io.extact.msa.spring.rms.domain.reservation.model.ReservationId;
import io.extact.msa.spring.rms.domain.reservation.model.ReservationPeriod;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Observed(name = "rms-console-local", contextualName = "MemberConsoleService")
public class LocalMemberConsoleService implements MemberConsoleService {

    private final ReserveItemService service;

    @Override
    public List<ItemConsoleModel> getAllItems() {
        return service
                .getItemAll()
                .stream()
                .map(ModelConverter::fromItemView)
                .toList();
    }

    @Override
    public List<MemberReservationConsoleModel> findReservationByItemIdAndFromDate(int itemId, LocalDate fromDate) {
        ReserveItemQueryCondition cond = ReserveItemQueryCondition.builder()
                .from(fromDate)
                .itemId(itemId)
                .build();
        return service
                .findReservationByCondition(cond)
                .stream()
                .map(ModelConverter::fromReservationComposeModel)
                .toList();
    }

    @Override
    public List<MemberReservationConsoleModel> getOwnReservations() {
        return service
                .getOwnReservations()
                .stream()
                .map(ModelConverter::fromReservationComposeModel)
                .toList();
    }

    @Override
    public MemberReservationConsoleModel reserveItem(MemberReservationConsoleModel model) {
        ReserveItemCommand command = ReserveItemCommand.builder()
                .period(new ReservationPeriod(model.fromDateTime(), model.toDateTime()))
                .note(model.note())
                .itemId(new ItemId(model.itemId()))
                .build();
        return service.reserve(command).transform(ModelConverter::fromReservationComposeModel);
    }

    @Override
    public void cancelReservation(int reservationId) {
        service.cancel(new ReservationId(reservationId));
    }
}
