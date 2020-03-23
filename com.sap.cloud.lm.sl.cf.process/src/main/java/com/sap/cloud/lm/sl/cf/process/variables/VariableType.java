package com.sap.cloud.lm.sl.cf.process.variables;

import java.lang.reflect.Type;

import com.fasterxml.jackson.core.type.TypeReference;

public class VariableType<T> {

    private final TypeReference<T> typeReference;
    private final Class<T> classOfT;

    private VariableType(TypeReference<T> typeReference) {
        this(typeReference, null);
    }

    private VariableType(TypeReference<T> typeReference, Class<T> classOfT) {
        this.typeReference = typeReference;
        this.classOfT = classOfT;
    }

    TypeReference<T> getTypeReference() {
        return typeReference;
    }

    Class<T> getClassOfT() {
        return classOfT;
    }

    public static <T> VariableType<T> of(TypeReference<T> typeReference) {
        return new VariableType<T>(typeReference);
    }

    public static <T> VariableType<T> of(Class<T> classOfT) {
        return new VariableType<T>(typeReference(classOfT), classOfT);
    }

    private static <T> TypeReference<T> typeReference(Class<T> classOfT) {
        if (classOfT == null) {
            throw new NullPointerException();
        }
        return new TypeReference<T>() {
            @Override
            public Type getType() {
                return classOfT;
            }
        };
    }

}
