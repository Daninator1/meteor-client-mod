package meteordevelopment.meteorclient.mixin;

import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.gui.widget.EntryListWidget.class)
public interface EntryListWidgetInvoker {
    @Invoker("swapEntriesOnPositions")
    void meteor$swapEntriesOnPositions(int pos1, int pos2);
}
