package me.acorn.mixin;

import me.acorn.Acorn;
import me.melinoe.features.impl.ClickGUIModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Melinoe's ClickGUI builds one panel per registered {@link me.melinoe.features.Category}
 * in its object initializer, but {@code Panel}'s constructor throws if that category has no
 * entry in {@link ClickGUIModule#getPanelSetting()} (Melinoe only seeds positions for its own
 * built-in categories). Acorn registers its category via {@code Category.custom("Acorn")}, so we
 * just need to seed the panel position before the GUI is opened.
 *
 * <p>This injects at the head of {@code ClickGUIModule.onEnable()}, which runs immediately before
 * {@code mc.setScreen(ClickGUI)} triggers the panel build — after config has loaded, so the seeded
 * entry isn't wiped by {@code MapSetting.read()}. {@code putIfAbsent} keeps any saved position.
 */
@Mixin(value = ClickGUIModule.class, remap = false)
public class ClickGUIMixin {

    @Inject(method = "onEnable", at = @At("HEAD"), remap = false)
    private void acorn$ensurePanel(CallbackInfo ci) {
        String categoryName = Acorn.INSTANCE.getCATEGORY().getName();

        // Place the Acorn panel after Melinoe's four built-in panels (Combat/Visual/Tracking/Misc).
        ClickGUIModule.INSTANCE.getPanelSetting().putIfAbsent(
            categoryName,
            new ClickGUIModule.PanelData(10f + 260f * 4, 10f, true)
        );
    }
}
