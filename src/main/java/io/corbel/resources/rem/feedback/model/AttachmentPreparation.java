package io.corbel.resources.rem.feedback.model;

/**
 * @author Samuel Martin
 */
public class AttachmentPreparation {
    private String attachmentName;
    private byte[] attachmentContent;

    public AttachmentPreparation(String attachmentName, byte[] attachmentContent) {
        this.attachmentName = attachmentName;
        this.attachmentContent = attachmentContent;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public byte[] getAttachmentContent() {
        return attachmentContent;
    }
}
