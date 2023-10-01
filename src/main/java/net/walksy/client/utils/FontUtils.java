package net.walksy.client.utils;

import net.walksy.client.WalksyClient;
import net.walksy.client.interfaces.mixin.IClient;
import net.walksy.client.interfaces.mixin.IFontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

public class FontUtils {
    public static TextRenderer createTextRenderer(Identifier fontId) {
        IClient client = (IClient) WalksyClient.getClient();
        IFontManager fontManager = (IFontManager)client.getFontManager();

        return fontManager.createTextRendererFromIdentifier(fontId);
    }

    public static TextRenderer createTextRenderer(String fontId) {
        return createTextRenderer(new Identifier(fontId));
    }
}
