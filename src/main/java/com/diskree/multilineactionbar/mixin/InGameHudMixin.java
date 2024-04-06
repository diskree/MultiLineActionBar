package com.diskree.multilineactionbar.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Unique
    private final List<String> overlayMessageTextLines = Lists.newArrayList();

    @Shadow
    private int scaledWidth;

    @Shadow
    public abstract TextRenderer getFontRenderer();

    @Inject(method = "setOverlayMessage(Ljava/lang/String;Z)V", at = @At(value = "HEAD"))
    private void setOverlayMessageInject(String string, boolean bl, CallbackInfo ci) {
        overlayMessageTextLines.clear();
        overlayMessageTextLines.addAll(getFontRenderer().wrapStringToWidthAsList(string, scaledWidth - 50));
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFI)I", ordinal = 0))
    private int renderRedirect(TextRenderer textRenderer, String text, float x, float y, int color) {
        int linesCount = overlayMessageTextLines.size();
        if (linesCount > 1) {
            if (linesCount % 2 == 1) {
                linesCount--;
            }
            y -= (float) (linesCount * textRenderer.fontHeight) / 2;
        }
        for (String line : overlayMessageTextLines) {
            textRenderer.draw(line, -textRenderer.getStringWidth(line) / 2f, y, color);
            y += textRenderer.fontHeight;
        }
        return 0;
    }
}
