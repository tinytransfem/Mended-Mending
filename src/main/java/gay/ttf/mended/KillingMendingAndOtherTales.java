package gay.ttf.mended;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = MendedMending.MOD_ID)
public class KillingMendingAndOtherTales {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void killMending(PlayerXpEvent.PickupXp event) {
		Player player = event.getEntity();
		ExperienceOrb orb = event.getOrb();

		player.takeXpDelay = 2;
		player.take(orb, 1);
		if (orb.getValue() > 0) {
			player.giveExperiencePoints(orb.getValue());
		}

		orb.discard();
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();
		ItemStack out = event.getOutput();

		if (out.isEmpty() && (left.isEmpty() || right.isEmpty())) {
			return;
		}

		boolean isMended = false;


		ItemEnchantments enchLeft = EnchantmentHelper.getEnchantmentsForCrafting(left);
		ItemEnchantments enchRight = EnchantmentHelper.getEnchantmentsForCrafting(right);

		if (enchLeft.entrySet().stream().anyMatch(x -> x.getKey().getKey() == Enchantments.MENDING) ||
				enchRight.entrySet().stream().anyMatch(x -> x.getKey().getKey() == Enchantments.MENDING)) {
			if (left.getItem() == right.getItem()) {
				isMended = true;
			}

			if (right.getItem() == Items.ENCHANTED_BOOK) {
				isMended = true;
			}
		}

		if (isMended) {
			if (out.isEmpty()) {
				out = left.copy();
			}

			ItemEnchantments.Mutable enchOutput = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(out));
			enchRight.entrySet().forEach(ench -> enchOutput.set(ench.getKey(), ench.getIntValue()));
			enchOutput.removeIf(ench -> ench.getKey() == Enchantments.MENDING);
			EnchantmentHelper.setEnchantments(out, enchOutput.toImmutable());

			out.set(DataComponents.REPAIR_COST, 0);
			if(out.isDamageableItem()) {
				out.setDamageValue(0);
			}

			event.setOutput(out);
			event.setCost(1);
		}
	}
}
