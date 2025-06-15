package io.extact.msa.spring.rms.console.service.adapter.remote.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange
public interface RmsApplicationClient {


    // ----------------------------------------------------- LoginConsoleServce

    @PostExchange("/login")
    ResponseEntity<UserClientResponse> login(@RequestBody LoginClientRequest request);


    // ----------------------------------------------------- AdminConsoleServce

    @PostExchange("/admin/items")
    ItemClientResponse addItem(@RequestBody ItemAddClientRequest request);

    @GetExchange("/admin/users")
    List<UserClientResponse> getAllUsers();

    @PostExchange("/admin/users")
    UserClientResponse addUser(@RequestBody UserAddClientRequest request);

    @PutExchange("/admin/users")
    UserClientResponse updateUser(@RequestBody UserUpdateClientRequest request);


    // ----------------------------------------------------- MemberConsoleServce

    @GetExchange("/member/items")
    List<ItemClientResponse> getAllItems();

    @GetExchange("/member/reservations")
    List<ReserveItemClientResponse> findReservationByItemIdAndFromDate(
            @RequestParam("item-id") int itemId,
            @RequestParam("from-date") LocalDate fromDate);

    @GetExchange("/member/reservations/own")
    List<ReserveItemClientResponse> getOwnReservations();

    @PostExchange("/member/reservations")
    ReserveItemClientResponse reserveItem(@RequestBody ReserveItemClientRequest request);

    @DeleteExchange("/member/reservations/{reservationId}")
    void cancelReservation(@PathVariable int reservationId);
}
