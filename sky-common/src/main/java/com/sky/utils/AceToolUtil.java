package com.sky.utils;

import java.util.Objects;

public class AceToolUtil {
    public static Boolean isEmpty(String val) {
        if (Objects.isNull(val) || val.isEmpty()) {
            return true;
        }

        return false;
    }
}
