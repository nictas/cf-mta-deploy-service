package com.sap.cloud.lm.sl.cf.process.variables;

public class EnumSerializer<T> implements Serializer<T> {

    private final Class<T> classOfT;

    public EnumSerializer(Class<T> classOfT) {
        this.classOfT = classOfT;
    }

    @Override
    public Object serialize(T value) {
        return value.toString();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public T deserialize(Object serializedValue) {
        return (T) Enum.valueOf((Class) classOfT, (String) serializedValue);
    }

}
