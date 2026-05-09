package thaumcraft.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public final class AspectTooltipComponent implements TooltipComponent {
    private final List<Entry> entries;

    public AspectTooltipComponent(AspectList aspects) {
        List<Entry> sorted = new ArrayList<>();
        if (aspects != null) {
            for (Aspect aspect : aspects.getAspectsSortedByAmount()) {
                int amount = aspects.getAmount(aspect);
                if (aspect != null && amount > 0) {
                    sorted.add(new Entry(aspect, amount));
                }
            }
        }

        this.entries = List.copyOf(sorted);
    }

    public List<Entry> entries() {
        return entries;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public record Entry(Aspect aspect, int amount) {
    }
}
