package meteordevelopment.meteorclient.mixin;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSelectionList.class)
public interface AbstractSelectionListAccessor {
    @Invoker("swap")
    void meteor$swap(int currentIndex, int newIndex);
}
