package io.extact.msa.spring.rms.console.service.adapter.remote;

import java.time.LocalDate;
import java.util.List;

import io.extact.msa.spring.rms.console.service.MemberConsoleService;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.ItemClientResponse;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.ReserveItemClientRequest;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.ReserveItemClientResponse;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.RmsApplicationClient;
import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;
import io.extact.msa.spring.rms.console.service.model.MemberReservationConsoleModel;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Observed(name = "rms-console-remote")
public class RemoteMemberConsoleService implements MemberConsoleService {

    private final RmsApplicationClient client;

    @Override
    public List<ItemConsoleModel> getAllItems() {
        return client
                .getAllItems()
                .stream()
                .map(ItemClientResponse::toModel)
                .toList();
    }

    @Override
    public List<MemberReservationConsoleModel> findReservationByItemIdAndFromDate(int itemId, LocalDate fromDate) {
        return client
                .findReservationByItemIdAndFromDate(itemId, fromDate)
                .stream()
                .map(ReserveItemClientResponse::toModel)
                .toList();
    }

    @Override
    public List<MemberReservationConsoleModel> getOwnReservations() {
        return client
                .getOwnReservations()
                .stream()
                .map(ReserveItemClientResponse::toModel)
                .toList();
    }

    @Override
    public MemberReservationConsoleModel reserveItem(MemberReservationConsoleModel model) {
        return client.reserveItem(model.transform(ReserveItemClientRequest::from)).toModel();
    }

    @Override
    public void cancelReservation(int reservationId) {
        client.cancelReservation(reservationId);
    }
}
