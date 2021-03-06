package com.sap.cloud.lm.sl.cf.core.liquibase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.sap.cloud.lm.sl.cf.core.model.CloudTarget;
import com.sap.cloud.lm.sl.cf.core.util.ConfigurationEntriesUtil;

public class TransformFilterColumnTest {

    private final TransformFilterColumn transformFilterColumn = new TransformFilterColumn();

    @Test
    public void testSplitTargetSpaceValue() {

        CloudTarget targetSpace = ConfigurationEntriesUtil.splitTargetSpaceValue("org space");
        assertEquals("org", targetSpace.getOrganizationName());
        assertEquals("space", targetSpace.getSpaceName());

        targetSpace = ConfigurationEntriesUtil.splitTargetSpaceValue("orgspace");
        assertEquals("", targetSpace.getOrganizationName());
        assertEquals("orgspace", targetSpace.getSpaceName());

        targetSpace = ConfigurationEntriesUtil.splitTargetSpaceValue("org test space sap");
        assertEquals("org", targetSpace.getOrganizationName());
        assertEquals("test space sap", targetSpace.getSpaceName());

        targetSpace = ConfigurationEntriesUtil.splitTargetSpaceValue("");
        assertEquals("", targetSpace.getOrganizationName());
        assertEquals("", targetSpace.getSpaceName());
    }

    @Test
    public void testTransformData() {

        Map<Long, String> retrievedData = new HashMap<>();
        retrievedData.put(1L, "{\"requiredContent\":{\"type\":\"com.acme.plugin\"},\"targetSpace\":\"org space\"}");
        retrievedData.put(2L, "{\"requiredContent\":{\"type\":\"com.acme.plugin\"},\"targetSpace\":\"orgspace\"}");
        retrievedData.put(3L, "{\"requiredContent\":{\"type\":\"com.acme.plugin\"},\"targetSpace\":\"org test space sap\"}");

        Map<Long, String> transformedData = transformFilterColumn.transformData(retrievedData);
        assertEquals("{\"requiredContent\":{\"type\":\"com.acme.plugin\"},\"targetSpace\":{\"org\":\"org\",\"space\":\"space\"}}",
                     transformedData.get(1L));

        transformedData = transformFilterColumn.transformData(retrievedData);
        assertEquals("{\"requiredContent\":{\"type\":\"com.acme.plugin\"},\"targetSpace\":{\"org\":\"\",\"space\":\"orgspace\"}}",
                     transformedData.get(2L));

        transformedData = transformFilterColumn.transformData(retrievedData);
        assertEquals("{\"requiredContent\":{\"type\":\"com.acme.plugin\"},\"targetSpace\":{\"org\":\"org\",\"space\":\"test space sap\"}}",
                     transformedData.get(3L));
    }

    @Test
    public void testTransformDataEmptyContent() {

        Map<Long, String> retrievedData = new HashMap<>();
        Map<Long, String> transformedData = null;

        retrievedData.put(1L, "{\"requiredContent\":{\"type\":\"com.acme.plugin\"}}");
        transformedData = transformFilterColumn.transformData(retrievedData);
        assertTrue(transformedData.isEmpty());

        retrievedData.put(1L, "{}");
        transformedData = transformFilterColumn.transformData(retrievedData);
        assertTrue(transformedData.isEmpty());

        retrievedData.put(1L, "");
        transformedData = transformFilterColumn.transformData(retrievedData);
        assertTrue(transformedData.isEmpty());
    }
}
