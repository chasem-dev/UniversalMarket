package com.xwaffle.universalmarket.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by chase on 7/8/2016.
 */
public abstract class BasicCommand implements CommandCallable {

	public Text desc = Text.of("Description");
	public Text help = Text.of("Help!");
	public Text usage = Text.of("/command <params>");
	public String perm;

	public BasicCommand(String permission, String description, String usage) {
		this.perm = permission;
		this.desc = Text.of(description);
		this.usage = Text.of(usage);
		this.help = Text.of(description);
	}

	public abstract CommandResult process(CommandSource source, String arguments) throws CommandException;

	public boolean testPermission(CommandSource source) {
		if (!source.hasPermission(perm)) {
//			source.sendMessage(Text.of(TextColors.RED, "You do not have the permission: " + perm));
			return false;
		}
		return true;
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition)
			throws CommandException {
		Collection<Player> list = Sponge.getServer().getOnlinePlayers();

		List<String> tabList = new ArrayList<>();

		for (Player player : list) {
			tabList.add(player.getName());
		}
		return tabList;
	}

	public Optional<Text> getShortDescription(CommandSource commandSource) {
		return Optional.of(desc);
	}

	public Optional<Text> getHelp(CommandSource commandSource) {
		return Optional.of(help);
	}

	public Text getUsage(CommandSource commandSource) {
		return usage;
	}

}
