package dev.federicolupoli.mythicgear.command;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import dev.federicolupoli.mythicgear.set.Set;

public final class MythicGearCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final MythicGear plugin;

    public MythicGearCommand(MythicGear plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            usage(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "give" -> handleGive(sender, args);
            case "giveset" -> handleGiveset(sender, args);
            case "list" -> handleList(sender);
            case "reload" -> handleReload(sender);
            default -> usage(sender);
        }
        return true;
    }

    private void usage(CommandSender sender) {
        sender.sendMessage(MM.deserialize("<red>Uso: /mythicgear <give|giveset|list|reload>"));
    }

    private Player resolveTarget(CommandSender sender, String[] args, int index, String usage) {
        if (args.length > index) {
            Player target = Bukkit.getPlayer(args[index]);
            if (target == null) {
                sender.sendMessage(MM.deserialize("<red>Giocatore non trovato: <white>" + args[index]));
                return null;
            }
            return target;
        }
        if (sender instanceof Player player) {
            return player;
        }
        sender.sendMessage(MM.deserialize("<red>Specifica un giocatore: " + usage));
        return null;
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
        Player target = resolveTarget(sender, args, 2, "/mythicgear give <item> <giocatore>");
        if (target == null) {
            return;
        }
        ItemStack item = plugin.getRegistry().create(spec.id());
        Map<Integer, ItemStack> leftover = target.getInventory().addItem(item);
        for (ItemStack stack : leftover.values()) {
            target.getWorld().dropItemNaturally(target.getLocation(), stack);
        }
        sender.sendMessage(MM.deserialize("<green>Dato <white>" + spec.id() + " <green>a <white>" + target.getName()));
    }

    private void handleGiveset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(MM.deserialize("<red>Uso: /mythicgear giveset <set> [giocatore]"));
            return;
        }
        Set set = plugin.getSetRegistry().get(args[1].toLowerCase());
        if (set == null) {
            sender.sendMessage(MM.deserialize("<red>Set non trovato. Disponibili: <white>"
                    + String.join(", ", plugin.getSetRegistry().all().keySet())));
            return;
        }
        Player target = resolveTarget(sender, args, 2, "/mythicgear giveset <set> <giocatore>");
        if (target == null) {
            return;
        }
        int given = 0;
        List<String> order = new ArrayList<>(set.pieces());
        for (String itemId : plugin.getRegistry().ids()) {
            ItemSpec spec = plugin.getRegistry().get(itemId);
            if (spec != null && set.id().equals(spec.set()) && !order.contains(itemId)) {
                order.add(itemId);
            }
        }
        for (String piece : order) {
            ItemStack item = plugin.getRegistry().create(piece);
            if (item == null) {
                plugin.getLogger().warning("Piece '" + piece + "' of set '" + set.id() + "' missing from items.yml");
                continue;
            }
            Map<Integer, ItemStack> leftover = target.getInventory().addItem(item);
            for (ItemStack stack : leftover.values()) {
                target.getWorld().dropItemNaturally(target.getLocation(), stack);
            }
            given++;
        }
        sender.sendMessage(MM.deserialize("<green>Dato il set <aqua>" + set.name() + " <green>(" + given
                + " pezzi) a <white>" + target.getName()));
    }

    private void handleList(CommandSender sender) {
        Map<String, List<ItemSpec>> bySet = new LinkedHashMap<>();
        List<ItemSpec> unset = new ArrayList<>();
        for (String id : plugin.getRegistry().ids()) {
            ItemSpec spec = plugin.getRegistry().get(id);
            if (spec.set() != null) {
                bySet.computeIfAbsent(spec.set(), k -> new ArrayList<>()).add(spec);
            } else {
                unset.add(spec);
            }
        }
        for (Set set : plugin.getSetRegistry().all().values()) {
            List<ItemSpec> pieces = new ArrayList<>();
            for (String piece : set.pieces()) {
                ItemSpec spec = plugin.getRegistry().get(piece);
                if (spec != null) {
                    pieces.add(spec);
                }
            }
            List<ItemSpec> leftovers = bySet.remove(set.id());
            if (leftovers != null) {
                for (ItemSpec spec : leftovers) {
                    if (!pieces.contains(spec)) {
                        pieces.add(spec);
                    }
                }
            }
            if (pieces.isEmpty()) {
                continue;
            }
            sender.sendMessage(MM.deserialize("<gold>Set <aqua>" + set.name()));
            for (ItemSpec piece : pieces) {
                String display = piece.name() != null ? MM.stripTags(piece.name()) : piece.id();
                sender.sendMessage(MM.deserialize("<gray>• <white>" + display + " <dark_gray>[" + piece.id() + "]"));
            }
        }
        List<ItemSpec> others = new ArrayList<>(unset);
        for (List<ItemSpec> list : bySet.values()) {
            others.addAll(list);
        }
        if (!others.isEmpty()) {
            sender.sendMessage(MM.deserialize("<gold>Senza set:"));
            for (ItemSpec piece : others) {
                String display = piece.name() != null ? MM.stripTags(piece.name()) : piece.id();
                sender.sendMessage(MM.deserialize("<gray>• <white>" + display + " <dark_gray>[" + piece.id() + "]"));
            }
        }
    }

    private void handleReload(CommandSender sender) {
        plugin.reload();
        sender.sendMessage(MM.deserialize("<green>MythicGear ricaricato: items, sets, effetti e ricette."));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.addAll(List.of("give", "giveset", "list", "reload"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give")) {
                suggestions.addAll(plugin.getRegistry().ids());
            } else if (args[0].equalsIgnoreCase("giveset")) {
                suggestions.addAll(plugin.getSetRegistry().all().keySet());
            }
        } else if (args.length == 3
                && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("giveset"))) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
        }
        String prefix = args[args.length - 1].toLowerCase();
        return suggestions.stream().filter(s -> s.toLowerCase().startsWith(prefix)).toList();
    }
}
