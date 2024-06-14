package com.diskree.multilineactionbar.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
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
    public abstract TextRenderer getTextRenderer();

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
        method = "setOverlayMessage",
        at = @At(value = "HEAD")
    )
    private void setOverlayMessageInject(Text message, boolean tinted, CallbackInfo ci) {
        overlayMessageText = MultilineText.create(getTextRenderer(), message, client.getWindow().getScaledWidth() - 50);
    }

    @Redirect(
        method = "renderOverlayMessage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)I",
            ordinal = 0
        )
    )
    private int renderRedirect(
        DrawContext context,
        TextRenderer textRenderer,
        Text text,
        int x,
        int y,
        int width,
        int color
    ) {
        int linesCount = overlayMessageText.count();
        if (linesCount > 1) {
            if (linesCount % 2 == 1) {
                linesCount--;
            }
            y -= (linesCount * textRenderer.fontHeight) / 2;
        }
        overlayMessageText.drawCenterWithShadow(context, 0, y, textRenderer.fontHeight, color);
        return 0;
    }
}
