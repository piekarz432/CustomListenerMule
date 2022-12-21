package org.mule.extension.cdc.listener;

import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

@Sources(CdcListenerSource.class)
@ConnectionProviders(CdcListenerConnectionProvider.class)
public class CdcListenerConfiguration {
}