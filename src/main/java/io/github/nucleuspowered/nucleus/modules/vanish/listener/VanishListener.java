/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.vanish.listener;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.internal.ListenerBase;
import io.github.nucleuspowered.nucleus.internal.interfaces.Reloadable;
import io.github.nucleuspowered.nucleus.modules.vanish.commands.VanishCommand;
import io.github.nucleuspowered.nucleus.modules.vanish.config.VanishConfig;
import io.github.nucleuspowered.nucleus.modules.vanish.config.VanishConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.vanish.datamodules.VanishUserDataModule;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class VanishListener extends ListenerBase implements Reloadable {

    private VanishConfig vanishConfig = new VanishConfig();

    private final String permission = getPermisisonHandlerFor(VanishCommand.class).getPermissionWithSuffix("persist");

    @Listener
    public void onLogin(ClientConnectionEvent.Join event, @Root Player player) {
        VanishUserDataModule service = Nucleus.getNucleus().getUserDataManager().getUnchecked(player).get(VanishUserDataModule.class);
        if (service.isVanished()) {
            if (!player.hasPermission(this.permission)) {
                // No permission, no vanish.
                service.setVanished(false);
                return;
            } else if (vanishConfig.isSuppressMessagesOnVanish()) {
                event.setMessageCancelled(true);
            }

            player.offer(Keys.VANISH, true);
            player.offer(Keys.VANISH_IGNORES_COLLISION, true);
            player.offer(Keys.VANISH_PREVENTS_TARGETING, true);
            player.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("vanish.login"));
        }
    }

    @Listener
    public void onQuit(ClientConnectionEvent.Disconnect event, @Root Player player) {
        player.get(Keys.VANISH).ifPresent(x -> {
            if (x) {
                Nucleus.getNucleus().getUserDataManager().getUnchecked(player).get(VanishUserDataModule.class).setVanished(true);
                if (vanishConfig.isSuppressMessagesOnVanish()) {
                    event.setMessageCancelled(true);
                }
            }
        });
    }

    @Override public void onReload() throws Exception {
        this.vanishConfig = getServiceUnchecked(VanishConfigAdapter.class).getNodeOrDefault();
    }
}
