package net.walksy.client.mixin.botch;

import java.util.Map;

import net.walksy.client.interfaces.mixin.IFontManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

@Mixin(FontManager.class)
public class FontManagerMixin implements IFontManager {
    @Shadow
    @Final
    private Map<Identifier, FontStorage> fontStorages;

    @Shadow
    private Map<Identifier, Identifier> idOverrides;

    @Shadow
    @Final
    private FontStorage missingStorage;

    @Override
    public TextRenderer createTextRendererFromIdentifier(Identifier ident) {
        return new TextRenderer(id -> this.fontStorages.getOrDefault(ident, this.missingStorage), true);
    }
}
