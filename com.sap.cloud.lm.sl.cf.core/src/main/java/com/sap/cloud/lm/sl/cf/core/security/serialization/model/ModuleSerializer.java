package com.sap.cloud.lm.sl.cf.core.security.serialization.model;

import com.sap.cloud.lm.sl.cf.core.security.serialization.SecureJsonSerializer;
import com.sap.cloud.lm.sl.cf.core.security.serialization.SecureSerializerConfiguration;
import com.sap.cloud.lm.sl.cf.core.security.serialization.masking.ModuleMasker;
import com.sap.cloud.lm.sl.mta.model.Module;

public class ModuleSerializer extends SecureJsonSerializer {

    final ModuleMasker masker = new ModuleMasker();

    public ModuleSerializer(SecureSerializerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String serialize(Object object) {
        Module clonedModule = Module.copyOf((Module) object);
        masker.mask(clonedModule);
        return super.serialize(clonedModule);
    }
}
