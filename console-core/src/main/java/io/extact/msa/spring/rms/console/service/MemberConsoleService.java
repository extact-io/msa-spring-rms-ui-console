package io.extact.msa.spring.rms.console.service;

import java.time.LocalDate;
import java.util.List;

import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;
import io.extact.msa.spring.rms.console.service.model.MemberReservationConsoleModel;

public interface MemberConsoleService {

    List<ItemConsoleModel> getAllItems();

    List<MemberReservationConsoleModel> findReservationByItemIdAndFromDate(int itemId, LocalDate fromDate);

    List<MemberReservationConsoleModel> getOwnReservations();

    MemberReservationConsoleModel reserveItem(MemberReservationConsoleModel model);

    void cancelReservation(int reservationId);
}
