package com.sap.cloud.lm.sl.cf.process.variables;

import org.immutables.value.Value;

import com.sap.cloud.lm.sl.cf.web.api.Nullable;

@Value.Immutable
public abstract class Variable<T> {

    public abstract String getName();

    @Nullable
    public abstract VariableType<T> getType();

    @Value.Default
    public SerializationStrategy getSerializationStrategy() {
        return SerializationStrategy.DIRECT;
    }

    @Value.Derived
    Serializer<T> getSerializer() {
        switch (getSerializationStrategy()) {
            case DIRECT:
                return new DirectSerializer<>();
            case JSON_STRING:
                return new JsonStringSerializer<>(getType().getTypeReference());
            case JSON_BINARY:
                return new JsonBinarySerializer<>(getType().getTypeReference());
            case ENUM:
                return new EnumSerializer<>(getType().getClassOfT());
            default:
                throw new IllegalStateException();
        }
    }

    @Nullable
    public abstract T getDefaultValue();

}
