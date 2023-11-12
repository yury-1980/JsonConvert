package ru.clevertec;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class JsonConverter {

    private String json = null;

    /**
     * Определение имён полей и получение объекта поля из входящих параметров.
     *
     * @param object
     * @return строка
     */
    public String toJson(Object object) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String fieldName = fields[i].getName();
            Object fieldValue;
            try {
                fieldValue = fields[i].get(object);
            } catch (IllegalAccessException e) {
                fieldValue = null;
            }
            json.append("\"").append(fieldName).append("\":");
            json.append(toJsonFieldValue(fieldValue));
            if (i < fields.length - 1) {
                json.append(",");
            }
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Определение типа значения полей с добавлением соответствующих символов в строку.
     *
     * @param fieldValue
     * @return строка
     */
    private String toJsonFieldValue(Object fieldValue) {

        if (fieldValue == null) {
            return "null";
        } else if (fieldValue instanceof String || fieldValue instanceof UUID || fieldValue instanceof OffsetDateTime ||
                fieldValue instanceof LocalDate) {
            return "\"" + fieldValue + "\"";
        } else if (fieldValue instanceof Number || fieldValue instanceof Boolean) {
            return fieldValue.toString();
        } else if (fieldValue instanceof List<?>) {
            return listToJson((List<?>) fieldValue);
        } /*else if (fieldValue instanceof HashMap<?, ?>) {
            return hashMapToJson((HashMap<String, Integer>) fieldValue);
        }*/ else {
            return toJson(fieldValue);
        }
    }

    /**
     * Если в предыдущем методе поле содержало List, извлечение значений полей и добавление
     * в строку соответствующих символов.
     *
     * @param list
     * @return строка
     */
    private String listToJson(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < list.size(); i++) {
            json.append(toJsonFieldValue(list.get(i)));
            if (i < list.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        return json.toString();
    }

    private String hashMapToJson(HashMap<String, Integer> map) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        Iterator<? extends Map.Entry<?, ?>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iterator.next();
            String key = entry.getKey();
            Integer value = entry.getValue();
            json.append(key);
            json.append(":");
            json.append(toJsonFieldValue(value));

            if (iterator.hasNext()) {
                json.append(",");
            }
//            System.out.println("Key: " + key + ", Value: " + value);

        }
        json.append("]");

        return json.toString();
    }

//----------------------------------//

    private Map<String, Object> parseJson(String jsonString) {
        Map<String, Object> result = new LinkedHashMap<>();
        json = jsonString;

        // Удаление пробелов из строки JSON
        json = json.replaceAll("\\s", "");

        if (json.startsWith("{")) {
            json = json.substring(1); // Удаление "{"
        } else {
            throw new IllegalArgumentException("Некорректный формат JSON");

        }

        while (!json.isEmpty()) {
            // При окончании json
            if (json.startsWith("}") && json.length() >= 2) {
                json = json.substring(1);
                return result;
            } else if (json.startsWith("}")) {
                json = "";
                return result;
            }

            String keyValueStr = null;

            // Выбор части строки до ","
            int commaIndex = json.indexOf(",");
            if (commaIndex != -1) {
                keyValueStr = json.substring(0, commaIndex);
            } else {
                keyValueStr = json;
            }

            // Разделение элементов JSON по ":"
            String[] keyValue = new String[2];
            keyValue[0] = keyValueStr.substring(0, keyValueStr.indexOf(":"));
            keyValue[1] = keyValueStr.substring(keyValueStr.indexOf(":") + 1);

            // Удаление " " " из key
            String key = keyValue[0].replaceAll("\"", "");
            String value = keyValue[1];

            if (value.startsWith("{")) {

                // Удаляем JSON до "{"
                int i = json.indexOf("{");
                json = json.substring(i);

                // Вложенный объект
                result.put(key, parseJson(json));
            } else if (value.startsWith("[")) {

                // Удаляем JSON до "["
                int i = json.indexOf("[");
                json = json.substring(i);

                String substring = json.substring(json.indexOf("["), json.indexOf(","));

                if (substring.contains(":")) {
                    // Массив Objects
                    result.put(key, parseObjectToList(json));
                } else result.put(key, parsePrimitiveToList(json));

                if (json.startsWith("}")) {
                    json = json.substring(1);
                    return result;

                } else if (json.startsWith(",")) {
                    json = json.substring(1);
                }

                // Если содержится "}", выбираем значения до него.
            } else if (value.contains("}")) {
                String v = value.substring(0, value.indexOf("}"));
                result.put(key, parseValue(v));

                // Удаляем значенияе и "}"
                json = json.substring(json.indexOf("}") + 1);

                if (json.startsWith(",")) { // Если первая "," то, удаляем её.
                    json = json.substring(1);
                }
                return result;
            } else {
                // Простое значение
                result.put(key, parseValue(value));

                // Удаляем JSON по ","
                json = json.substring(commaIndex + 1);
            }
        }

        return result;
    }

    private Object parseValue(String value) {
        if (value.contains("\"")) {
            return value;
        } else if (value.equals("true") || value.equals("false")) {
            return value;
        } else if (value.contains(".")) {
            return value;
        } else {
            return value;
        }
    }

    private List<Object> parsePrimitiveToList(String jsonString) {

        List<Object> stringList = new ArrayList<>();
        json = jsonString;
        // Обрезаем первую "["
        json = json.substring(1);

        if (json.startsWith("]")) {
            return stringList;
        }

        while (!json.startsWith("}")) {

            // Если это строки
            if (json.substring(0, json.indexOf("]")).contains("\"")) {
                String value = json.substring(0, json.indexOf("\"", 1));
                stringList.add(value);
                json = json.substring(json.indexOf("\"", 1) + 1);
            } else {
                if (json.substring(0, json.indexOf("]")).contains(",")) {
                    String value = json.substring(0, json.indexOf(","));
                    stringList.add(value);
                    json = json.substring(json.indexOf(","));
                } else { // Если это последнее значение
                    stringList.add(json.substring(0, json.indexOf("]")));
                    json = json.substring(json.indexOf("]"));
                }
            }

            if (json.startsWith(",")) {
                json = json.substring(1);
            } else if (json.startsWith("]")) {
                json = json.substring(1);

                return stringList;
            }
        }
        return stringList;
    }

    private List<Map<String, Object>> parseObjectToList(String jsonString) {

        List<Map<String, Object>> stringList = new ArrayList<>();
        json = jsonString;

        // Обрезаем первую "["
        json = json.substring(1);

        while (!json.isEmpty()) {

            if (json.startsWith("{")) {
                stringList.add(parseJson(json));
            } else if (json.startsWith("]")) {
                json = json.substring(1);

                return stringList;
            }
        }

        return stringList;
    }

    private String removingQuotesAndToString(Object fieldValue) {
        String str = fieldValue.toString();
        str = str.substring(1, str.length() - 1);
        return str;
    }

    public <T> T jsonToObject(String json, Class<T> typeClass) throws Exception {

        Map<String, Object> jsonMap = parseJson(json);
        return createObject(jsonMap, typeClass);
    }

    private <T> T createObject(Map<String, Object> jsonMap, Class<T> typeClass) throws Exception {

        T myObject = typeClass.getDeclaredConstructor().newInstance();

        Field[] fields = typeClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            log.info(String.valueOf(fieldType));

            if (jsonMap.containsKey(fieldName)) {
                Object fieldValue = jsonMap.get(fieldName);

                String str = removingQuotesAndToString(fieldValue);

                if (fieldType == UUID.class) {
                    field.set(myObject, UUID.fromString(str));
                    continue;
                }

                if (fieldType == LocalDate.class) {
                    field.set(myObject, LocalDate.parse(str));
                    continue;
                }

                if (fieldType == String.class) {
                    field.set(myObject, str);
                    continue;
                }

                if (fieldType == OffsetDateTime.class) {
                    field.set(myObject, OffsetDateTime.parse(str));
                    continue;
                }

               /* if (fieldValue instanceof Map) {
                    // Рекурсивное создание вложенного объекта
                    fieldValue = createObject((Map<String, Object>) fieldValue, field.getType());
                } else*/ if (fieldValue instanceof List) {
                    // Рекурсивное создание списка объектов
                    fieldValue = createList((List<Object>) fieldValue, getGenericType(field));
                    field.set(myObject, fieldValue);
                }

                if (fieldType == Double.class) {
                    log.info(myObject.toString());
                    field.set(myObject, Double.valueOf(fieldValue.toString()));
                }
            }
        }

        return myObject;
    }

    private Class<?> getGenericType(Field field) {
        // Получение обобщенного типа для списка
        return (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    private <T> List<T> createList(List<Object> list, Class<T> elementType) throws Exception {
        List<T> resultList = new ArrayList<>();

        for (Object item : list) {

            if (item instanceof Map) {
                // Рекурсивное создание объекта из элемента списка
                T element = createObject((Map<String, Object>) item, elementType);
                resultList.add(element);
            }
        }

        return resultList;
    }
}