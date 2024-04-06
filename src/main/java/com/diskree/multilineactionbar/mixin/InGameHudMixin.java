package com.diskree.multilineactionbar.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
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
    private final List<StringRenderable> overlayMessageTextLines = Lists.newArrayList();

    @Shadow
    private int scaledWidth;

    @Shadow
    public abstract TextRenderer getFontRenderer();

    @Inject(method = "setOverlayMessage", at = @At(value = "HEAD"))
    private void setOverlayMessageInject(Text message, boolean tinted, CallbackInfo ci) {
        overlayMessageTextLines.clear();
        overlayMessageTextLines.addAll(getFontRenderer().wrapLines(message, scaledWidth - 50));
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/StringRenderable;FFI)I", ordinal = 0))
    private int renderRedirect(TextRenderer textRenderer, MatrixStack matrices, StringRenderable text, float x, float y, int color) {
        int linesCount = overlayMessageTextLines.size();
        if (linesCount > 1) {
            if (linesCount % 2 == 1) {
                linesCount--;
            }
            y -= (float) (linesCount * textRenderer.fontHeight) / 2;
        }
        for (StringRenderable stringRenderable : this.overlayMessageTextLines) {
            textRenderer.draw(matrices, stringRenderable, -textRenderer.getWidth(stringRenderable) / 2f, y, color);
            y += textRenderer.fontHeight;
        }
        return 0;
    }
}
