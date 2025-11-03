package meteordevelopment.meteorclient.mixin;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen.class)
public interface MultiplayerScreenAccessor {
    @Accessor("serverListWidget")
    MultiplayerServerListWidget meteor$getServerListWidget();
}
