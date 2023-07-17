package com.sky.controller.user;

import com.sky.constant.RedisConstant;
import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺管理")
@Slf4j
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("设置店铺的营业状态")
    public Result setStatus(@PathVariable Integer status) {
        if (status == StatusConstant.ENABLE) {
            redisTemplate.opsForValue().set(RedisConstant.SHOP_STATUS, status);
        } else if (status == StatusConstant.DISABLE) {
            redisTemplate.opsForValue().set(RedisConstant.SHOP_STATUS, status);
        } else {
            return Result.error("参数status接收到未知值");
        }

        return Result.success("设置成功");
    }

    @GetMapping("/status")
    @ApiOperation("店铺营业状态查询")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(RedisConstant.SHOP_STATUS);
        return Result.success(status);
    }
}
