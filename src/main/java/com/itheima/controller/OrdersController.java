package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.dto.OrdersDto;
import com.itheima.entity.Orders;
import com.itheima.service.AddressBookService;
import com.itheima.service.OrderDetailService;
import com.itheima.service.OrdersService;
import com.itheima.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("δΈεζε");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        ordersService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @GetMapping("/page")
    @Transactional
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> pageDto = new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        queryWrapper.gt(StringUtils.isNotEmpty(beginTime), Orders::getOrderTime, beginTime);
        queryWrapper.lt(StringUtils.isNotEmpty(endTime), Orders::getOrderTime, endTime);
        //ζ·»ε ζεΊζ‘δ»Ά(ζ Ήζ?ζ΄ζ°ζΆι΄ιεΊζεΊ)
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo, queryWrapper);
        //ε°εΆι€δΊrecordsδΈ­ηεε­ε€εΆε°pageDtoδΈ­
        BeanUtils.copyProperties(pageInfo, pageDto, "records");

        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> ordersDtoList = new ArrayList<>();
        for (Orders record : records) {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(record, ordersDto);
            ordersDto.setUserName(ordersDto.getConsignee());
            ordersDtoList.add(ordersDto);
        }
        pageDto.setRecords(ordersDtoList);
        return R.success(pageDto);
    }

    @PutMapping
    public R<String> update(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return R.success("ζδ½ζε");
    }
}
