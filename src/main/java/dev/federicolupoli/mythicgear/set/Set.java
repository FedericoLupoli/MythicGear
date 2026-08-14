package dev.federicolupoli.mythicgear.set;

import java.util.List;

public record Set(String id, String name, List<String> pieces, double maxHealthBonus) {
}
