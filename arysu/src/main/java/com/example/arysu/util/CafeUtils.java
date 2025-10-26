package com.example.arysu.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CafeUtils {

    public static BigDecimal calcularDescuento(BigDecimal precio, int porcentaje) {
        return precio.multiply(BigDecimal.valueOf(1 - porcentaje / 100.0));
    }

    public static String formatearPrecio(BigDecimal precio) {
        return "S/ " + precio.setScale(2, RoundingMode.HALF_UP);
    }
}
