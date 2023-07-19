package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCar")
@Slf4j
@Api(tags = "购物车管理")
public class ShoppingCarController {

    @Autowired
    private ShoppingCarService shoppingCarService;

    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCarService.addToShoppingCar(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("获取用户购物车数据")
    public Result<List<ShoppingCart>> list(){
        shoppingCarService.showShoppingCar();
        return Result.success();
    }

    @DeleteMapping("/clear")
    @ApiOperation("清空购物车")
    public Result delete(){
        shoppingCarService.clearShoppingCar();
        return Result.success();
    }
}
