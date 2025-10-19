package io.extact.msa.spring.rms.console.screen;

import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;
import io.extact.msa.spring.rms.console.service.model.MemberReservationConsoleModel;
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;

public interface ModelFormatter<T> {

    String format(T model);

    static class RentalItemFormatter implements ModelFormatter<ItemConsoleModel> {
        @Override
        public String format(ItemConsoleModel item) {
            return String.format("[%s]%s シリアル番号：%s",
                    item.id(),
                    item.itemName(),
                    item.serialNo());
        }
    }

    static class ReservationFormatter implements ModelFormatter<MemberReservationConsoleModel> {
        @Override
        public String format(MemberReservationConsoleModel model) {
            return String.format("[%s] %s - %s %s %s",
                    model.id(),
                    ClientConstants.DATETIME_FORMAT.format(model.fromDateTime()),
                    ClientConstants.DATETIME_FORMAT.format(model.toDateTime()),
                    model.itemName(),
                    model.note());
        }
    }

    static class UserAccountFormatter implements ModelFormatter<UserConsoleModel> {
        @Override
        public String format(UserConsoleModel user) {
            return String.format("[%s] %s/%s %s %s",
                    user.id(),
                    user.loginId(),
                    user.password(),
                    user.userName(),
                    user.userType());
        }
    }
}
