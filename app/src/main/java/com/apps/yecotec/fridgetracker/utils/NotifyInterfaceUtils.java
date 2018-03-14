package com.apps.yecotec.fridgetracker.utils;

import com.apps.yecotec.fridgetracker.data.Food;

/**
 * Created by kenruizinoue on 9/12/17.
 */

public interface NotifyInterfaceUtils {

    void onAddFood();
    void onAddSection();
    void updateUi();
    void deleteFood(Food deletedFood);

}
