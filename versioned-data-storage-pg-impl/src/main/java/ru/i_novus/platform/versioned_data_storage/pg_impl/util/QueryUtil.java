package ru.i_novus.platform.versioned_data_storage.pg_impl.util;

import net.n2oapp.criteria.api.Criteria;
import org.apache.commons.lang.StringUtils;
import ru.i_novus.platform.datastorage.temporal.model.Field;
import ru.i_novus.platform.datastorage.temporal.model.FieldValue;
import ru.i_novus.platform.datastorage.temporal.model.value.*;
import ru.i_novus.platform.versioned_data_storage.pg_impl.model.*;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lgalimova
 * @since 21.03.2018
 */
public class QueryUtil {

    public static List<RowValue> convertToRowValue(List<Field> fields, List<Object[]> data) {
        List<RowValue> resultData = new ArrayList<>(data.size());
        for (Object objects : data) {
            RowValue rowValue = new LongRowValue();
            if (objects instanceof Object[]) {
                Object[] row = (Object[]) objects;
                for (int i = 0; i < row.length; i++) {
                    rowValue.getFieldValues().add(getFieldValue(fields.get(i), row[i]));
                }
            } else {
                rowValue.getFieldValues().add(getFieldValue(fields.get(0), objects));
            }
            resultData.add(rowValue);
        }
        return resultData;
    }

    public static FieldValue getFieldValue(Field field, Object value) {
        FieldValue fieldValue = null;
        String name = field.getName();
        if (field instanceof BooleanField) {
            fieldValue = new BooleanFieldValue(name, (Boolean) value);
        } else if (field instanceof DateField) {
            fieldValue = new DateFieldValue(name, (Date) value);
        } else if (field instanceof FloatField) {
            fieldValue = new FloatFieldValue(name, (Number) value);
        } else if (field instanceof IntegerField) {
            fieldValue = new IntegerFieldValue(name, (Number) value);
        } else if (field instanceof ReferenceField && field.getName().contains("->>")) {
//            ReferenceField referenceField = (ReferenceField) field;
//            ReferenceField newReferenceField = new ReferenceField(formatJsonbAttrValueForMapping(referenceField.getName()));
//            fieldValue = new ReferenceFieldValue(name, value.toString());
        } else {
            fieldValue = new StringFieldValue(name, value != null ? value.toString() : null);
        }
        return fieldValue;
    }

    public static String generateSqlQuery(String alias, List<Field> fields) {
        return fields.stream().map(field -> {
            String query = formatFieldForQuery(field.getName(), alias);
            if (field instanceof TreeField) {
                query += "\\:\\:text";
            }
            if (field.getName().contains("->>"))
                return query + " as " + addEscapeCharacters(alias + formatJsonbAttrValueForMapping(field.getName()));
            else
                return query + " as " + addEscapeCharacters(alias + field.getName());
        }).collect(Collectors.joining(", "));
    }

    public static String formatFieldForQuery(String field, String alias) {
        alias = alias + ".";
        if (field.contains("->>")) {
            String[] queryParts = field.split("->>");
            return alias + addEscapeCharacters(queryParts[0]) + "->>" + queryParts[1];
        } else {
            return alias + addEscapeCharacters(field);
        }
    }

    public static String addEscapeCharacters(String source) {
        return "\"" + source + "\"";
    }

    public static String formatJsonbAttrValueForMapping(String field) {
        if (!field.contains("->>"))
            return field;
        else {
            String[] parts = field.split("->>");
            return parts[0] + "." + StringUtils.strip(parts[1], "'").toUpperCase();
        }
    }

    public static String getSequenceName(String table) {
        return addEscapeCharacters(table + "_SYS_RECORDID_seq");
    }

    public static Date truncateDateTo(Date date, ChronoUnit unit) {
        return Date.from(date.toInstant().truncatedTo(unit));
    }

    public static int getOffset(Criteria criteria) {
        if (criteria != null) {
            if (criteria.getPage() <= 0 || criteria.getSize() <= 0)
                throw new IllegalStateException("Criteria page and size should be greater than zero");
            return (criteria.getPage() - 1) * criteria.getSize();
        }
        return 0;
    }

    public static boolean isCompatibleTypes(String oldDataType, String newDataType) {
        if (ReferenceField.TYPE.equals(newDataType) || ListField.TYPE.equals(newDataType) || ReferenceField.TYPE.equals(oldDataType) || ListField.TYPE.equals(oldDataType)) {
            return false;
        }
        if (oldDataType.equals(newDataType) || StringField.TYPE.equals(newDataType)) {
            return true;
        }
        if ((StringField.TYPE.equals(oldDataType) || IntegerStringField.TYPE.equals(oldDataType))
                && (IntegerField.TYPE.equals(newDataType)) || IntegerStringField.TYPE.equals(newDataType)) {
            return true;
        }
        return false;
    }

    public static Field getField(String name, String type) {
        switch (type) {
            case BooleanField.TYPE:
                return new BooleanField(name);
            case DateField.TYPE:
                return new DateField(name);
            case FloatField.TYPE:
                return new FloatField(name);
            case IntegerField.TYPE:
                return new IntegerField(name);
            case ReferenceField.TYPE:
                return new ReferenceField(name);
            default:
                return new StringField(name);
        }

    }
}
