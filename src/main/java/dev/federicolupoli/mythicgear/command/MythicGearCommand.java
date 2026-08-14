package dev.federicolupoli.mythicgear.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.federicolupoli.mythicgear.MythicGear;
import dev.federicolupoli.mythicgear.item.ItemSpec;

public final class MythicGearCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final MythicGear plugin;

    public MythicGearCommand(MythicGear plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MM.deserialize("<red>Uso: /mythicgear <give|list>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "give" -> handleGive(sender, args);
            case "list" -> handleList(sender);
            default -> sender.sendMessage(MM.deserialize("<red>Uso: /mythicgear <give|list>"));
        }
        return true;
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(MM.deserialize("<red>Uso: /mythicgear give <item> [giocatore]"));
            return;
        }
        ItemSpec spec = plugin.getRegistry().get(args[1].toLowerCase());
        if (spec == null) {
            sender.sendMessage(MM.deserialize("<red>Item non trovato. Disponibili: <white>"
                    + String.join(", ", plugin.getRegistry().ids())));
            return;
        }
        Player target;
        if (args.length >= 3) {
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(MM.deserialize("<red>Giocatore non trovato: <white>" + args[2]));
                return;
            }
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            sender.sendMessage(MM.deserialize("<red>Specifica un giocatore: /mythicgear give <item> <giocatore>"));
            return;
        }

        ItemStack item = plugin.getRegistry().create(spec.id());
        Map<Integer, ItemStack> leftover = target.getInventory().addItem(item);
        for (ItemStack stack : leftover.values()) {
            target.getWorld().dropItemNaturally(target.getLocation(), stack);
        }
        sender.sendMessage(MM.deserialize("<green>Dato <white>" + spec.id() + " <green>a <white>" + target.getName()));
    }

    private void handleList(CommandSender sender) {
        sender.sendMessage(MM.deserialize("<gold>Item MythicGear: <white>"
                + String.join(", ", plugin.getRegistry().ids())));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.addAll(List.of("give", "list"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            suggestions.addAll(plugin.getRegistry().ids());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
        }
        String prefix = args[args.length - 1].toLowerCase();
        return suggestions.stream().filter(s -> s.toLowerCase().startsWith(prefix)).toList();
    }
}
