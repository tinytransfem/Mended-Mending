package gay.ttf.mended;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

@SuppressWarnings("unused")
public class KillingMendingAndOtherTales {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void killMending(PlayerXpEvent.PickupXp event) {
		Player player = event.getEntity();
		ExperienceOrb orb = event.getOrb();

		player.takeXpDelay = 2;
		player.take(orb, 1);
		if (orb.value > 0) {
			player.giveExperiencePoints(orb.value);
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

		Map<Enchantment, Integer> enchLeft = EnchantmentHelper.getEnchantments(left);
		Map<Enchantment, Integer> enchRight = EnchantmentHelper.getEnchantments(right);

		if (enchLeft.containsKey(Enchantments.MENDING) || enchRight.containsKey(Enchantments.MENDING)) {
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

			if (!out.hasTag()) {
				out.setTag(new CompoundTag());
			}

			Map<Enchantment, Integer> enchOutput = EnchantmentHelper.getEnchantments(out);
			enchOutput.putAll(enchRight);
			enchOutput.remove(Enchantments.MENDING);
			EnchantmentHelper.setEnchantments(enchOutput, out);

			out.setRepairCost(0);
			if(out.isDamageableItem()) {
				out.setDamageValue(0);
			}

			event.setOutput(out);
			if (event.getCost() == 0) {
				event.setCost(1);
			}
		}
	}
}
