package io.extact.msa.spring.rms.console.service.adapter.local;

import io.extact.msa.spring.rms.application.support.ReservationComposeModel;
import io.extact.msa.spring.rms.console.service.model.ConsoleUserType;
import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;
import io.extact.msa.spring.rms.console.service.model.MemberReservationConsoleModel;
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;
import io.extact.msa.spring.rms.domain.item.model.ItemModelView;
import io.extact.msa.spring.rms.domain.user.model.UserModelView;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelConverter {

    static ItemConsoleModel fromItemView(ItemModelView view) {
        return ItemConsoleModel.builder()
                .id(view.getId().id())
                .serialNo(view.getSerialNo())
                .itemName(view.getItemName())
                .build();
    }

    static UserConsoleModel fromUserView(UserModelView view) {
        return UserConsoleModel.builder()
                .id(view.getId().id())
                .loginId(view.getLoginId())
                .password(view.getPassword())
                .userName(view.getProfile().getUserName())
                .phoneNumber(view.getProfile().getPhoneNumber())
                .contact(view.getProfile().getContact())
                .userType(ConsoleUserType.valueOf(view.getUserType().name()))
                .build();
    }

    static MemberReservationConsoleModel fromReservationComposeModel(ReservationComposeModel model) {
        return MemberReservationConsoleModel.builder()
                .id(model.reservation().getId().id())
                .fromDateTime(model.reservation().getPeriod().getFrom())
                .toDateTime(model.reservation().getPeriod().getTo())
                .note(model.reservation().getNote())
                .itemId(model.reservation().getItemId().id())
                .serialNo(model.rentalItem().getSerialNo())
                .itemName(model.rentalItem().getItemName())
                .reserverId(model.reservation().getReserverId().id())
                .build();
    }
}
