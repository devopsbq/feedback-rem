package com.bq.corbel.resources.rem.feedback.model;

/**
 * @author Samuel Martin
 */
public class AttachmentPreparation {
    private String name;
    private byte[] content;

    public AttachmentPreparation(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }
}
