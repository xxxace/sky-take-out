package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.utils.AceToolUtil;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        dishService.saveWithFlavor(dishDTO);
        deleteRedisCacheByCategoryId(dishDTO.getCategoryId());
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.queryPage(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result deleteBatch(@RequestParam List<Long> ids) {
        if (ids.size() == 0) {
            return Result.error(MessageConstant.ID_IS_NULL);
        }
        dishService.deleteBatch(ids);
        deleteAllRedisKeys();
        return Result.error(MessageConstant.ID_IS_NULL);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("更新菜品数据")
    public Result update(@RequestBody DishDTO dishDTO) {
        dishService.updateWithFlavor(dishDTO);
        deleteAllRedisKeys();
        return Result.success();
    }

    @PutMapping("/status/{status}")
    @ApiOperation("设置商品起售停售")
    private Result<String> setStatus(@PathVariable Integer status, Long id) {
        dishService.setStatus(status, id);
        deleteAllRedisKeys();
        return Result.success("设置成功");
    }

    @GetMapping("/list")
    @ApiOperation("根据分类ID查询菜品")
    private Result<List<Dish>> list(Long categoryId) {
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    private void deleteAllRedisKeys() {
        // 直接移除所有dish_父类id缓存
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
    }

    private void deleteRedisCacheByCategoryId(Long categoryId) {
        String redisCategoryKey = "dish_" + categoryId;
        redisTemplate.delete(redisCategoryKey);
    }
}
