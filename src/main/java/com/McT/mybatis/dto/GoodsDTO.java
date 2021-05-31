package com.McT.mybatis.dto;

import com.McT.mybatis.entity.Category;
import com.McT.mybatis.entity.Goods;

public class GoodsDTO {
    private Goods goods = new Goods();
    private Category category = new Category();
    private String test;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
