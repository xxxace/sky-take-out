package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCarMapper;
import com.sky.service.ShoppingCarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCarImpl implements ShoppingCarService {

    @Autowired
    private ShoppingCarMapper shoppingCarMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    public void addToShoppingCar(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCarMapper.list(shoppingCart);

        // 如果已经存在
        if (list != null && list.size() > 0) {
            ShoppingCart item = list.get(0);
            item.setNumber(item.getNumber() + 1);
            shoppingCarMapper.updateNumber(item);
        } else {
            // 菜品
            Long dishId = shoppingCartDTO.getDishId();
            // 套餐
            Long setmealId = shoppingCart.getSetmealId();
            if (dishId != null) {
                Dish dish = dishMapper.queryById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else if (setmealId != null) {
                Setmeal setmeal = setmealMapper.queryById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            } else {
                throw new ShoppingCartBusinessException("缺少菜品id或套餐id");
            }

            // 设置默认值
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCarMapper.insert(shoppingCart);
        }
    }

    public List<ShoppingCart> showShoppingCar() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> shoppingCartList = shoppingCarMapper.list(shoppingCart);
        return shoppingCartList;
    }

    public void clearShoppingCar() {
        Long userId = BaseContext.getCurrentId();
        shoppingCarMapper.deleteByUserId(userId);
    }
}
