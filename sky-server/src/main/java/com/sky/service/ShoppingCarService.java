package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCarService {
    void addToShoppingCar(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> showShoppingCar();

    void clearShoppingCar();
}
