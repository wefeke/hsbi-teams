package com.example.application;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class DoubleToLongConverter implements Converter<Double, Long> {

    @Override
    public Result<Long> convertToModel(Double fieldValue, ValueContext context) {
        if (fieldValue == null) {
            return Result.ok(null);
        }

        long longValue = fieldValue.longValue();
        if ((double) longValue != fieldValue) {
            return Result.error("Matrikelnummer muss eine Ganzzahl sein");
        }

        return Result.ok(longValue);
    }

    @Override
    public Double convertToPresentation(Long modelValue, ValueContext context) {
        return modelValue == null ? null : modelValue.doubleValue();
    }
}

