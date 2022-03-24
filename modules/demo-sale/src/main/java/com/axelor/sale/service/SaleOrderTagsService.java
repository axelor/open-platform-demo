package com.axelor.sale.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.db.JpaSecurity;
import com.axelor.db.Query;
import com.axelor.inject.Beans;
import com.axelor.meta.CallMethod;
import com.axelor.sale.db.Order;
import com.axelor.sale.db.OrderStatus;

import java.util.Arrays;
import java.util.List;

public class SaleOrderTagsService {

    @CallMethod
    public String countOrdersMenuTag() {
        return countOrders(Arrays.asList(OrderStatus.values()));
    }

    @CallMethod
    public String countOrdersMenuTag(String status) {
        OrderStatus orderStatus = null;
        try {
            orderStatus = OrderStatus.valueOf(status);
        } catch(Exception e) {
            return null;
        }

        return countOrders(List.of(orderStatus));
    }

    private String countOrders(List<OrderStatus> status) {
        User user = AuthUtils.getUser();
        if (user == null
                || !Beans.get(JpaSecurity.class).isPermitted(JpaSecurity.CAN_READ, Order.class)) {
            return null;
        }
        return String.valueOf(Query.of(Order.class).filter("self.status IN (?1)", status).count());
    }
}
