package org.smee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Wikimedia recent change event message
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WRCMessage implements StreamMessage, Serializable {
    private String id;
    private String data;
    private String label;

    public void clear() {
        data = null;
        label = null;
        id = null;
    }

    public boolean isEmpty() {
        return data == null && label == null && id == null;
    }

    public boolean isReady() {
        return data != null && label != null && id != null;
    }
}
