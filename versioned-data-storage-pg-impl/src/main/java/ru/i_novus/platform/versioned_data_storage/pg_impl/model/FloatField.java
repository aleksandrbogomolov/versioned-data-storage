package ru.i_novus.platform.versioned_data_storage.pg_impl.model;

import ru.i_novus.platform.datastorage.temporal.model.Field;
import ru.i_novus.platform.datastorage.temporal.model.FieldValue;
import ru.i_novus.platform.datastorage.temporal.model.value.FloatFieldValue;

import java.math.BigDecimal;

/**
 * @author lgalimova
 * @since 23.03.2018
 */
public class FloatField extends Field<Number> {
    public static final String TYPE = "numeric";

    public FloatField(String name) {
        super(name);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public FieldValue valueOf(Number value) {
        return new FloatFieldValue(getName(), new BigDecimal(value.toString()));
    }
}
