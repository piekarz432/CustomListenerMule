package org.mule.extension.cdc.listener;

import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

@Sources(ListenerSource.class)
@ConnectionProviders(ListenerConnectionProvider.class)
public class ListenerConfiguration {}
