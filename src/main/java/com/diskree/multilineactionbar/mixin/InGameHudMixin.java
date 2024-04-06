package com.diskree.multilineactionbar.mixin;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Unique
    private MultilineText overlayMessageText;

    @Shadow
    private int scaledWidth;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "setOverlayMessage", at = @At(value = "HEAD"))
    private void setOverlayMessageInject(Text message, boolean tinted, CallbackInfo ci) {
        overlayMessageText = MultilineText.create(getTextRenderer(), message, scaledWidth - 50);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", ordinal = 0))
    private int renderRedirect(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        int linesCount = overlayMessageText.count();
        if (linesCount > 1) {
            if (linesCount % 2 == 1) {
                linesCount--;
            }
            y -= (float) (linesCount * textRenderer.fontHeight) / 2;
        }
        overlayMessageText.drawCenterWithShadow(matrices, 0, (int) y, textRenderer.fontHeight, color);
        return 0;
    }
}
