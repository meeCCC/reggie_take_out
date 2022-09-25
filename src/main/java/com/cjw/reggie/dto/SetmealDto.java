package com.cjw.reggie.dto;

import com.cjw.reggie.entity.Setmeal;
import com.cjw.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
