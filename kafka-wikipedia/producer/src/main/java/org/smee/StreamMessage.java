package org.smee;

public interface StreamMessage {
    String getId();

    String getLabel();

    String getData();

    boolean isReady();

    void clear();
}
