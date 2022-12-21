package org.mule.extension.cdc.attributes;

public class HttpRequestAttributes {

    private final String channel;
    private final String type;

    public HttpRequestAttributes(String channel, String type) {
        this.channel = channel;
        this.type = type;
    }

    public String getChannel() {
        return channel;
    }

    public String getType() {
        return type;
    }
}