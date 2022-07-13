package org.mule.extension.cdc.listener;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

@Xml(prefix = "listener")
@Extension(name = "CDC")
@Configurations(ListenerConfiguration.class)
public class ListenerExtension {
}
