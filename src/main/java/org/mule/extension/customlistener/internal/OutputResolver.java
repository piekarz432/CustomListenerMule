package org.mule.extension.customlistener.internal;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.metadata.resolving.OutputStaticTypeResolver;

import static org.mule.metadata.api.model.MetadataFormat.JAVA;

public class OutputResolver extends OutputStaticTypeResolver {

    @Override
    public String getCategoryName() {
        return "Metadata";
    }

    @Override
    public MetadataType getStaticMetadata() {
        return new BaseTypeBuilder(JAVA).anyType().build();
    }
}
