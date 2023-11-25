package emu.lunarcore.command.commands;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ItemExcel;
import emu.lunarcore.game.enums.ItemMainType;
import emu.lunarcore.game.enums.ItemRarity;
import emu.lunarcore.game.enums.ItemSubType;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;

@Command(label = "giveall", aliases = {"ga"}, permission = "player.give", desc = "/giveall {materials | avatars | lightcones | relics}. Gives the targeted player items.")
public class GiveAllCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        // Check target
        if (args.getTarget() == null) {
            this.sendMessage(sender, "Error: Targeted player not found or offline");
            return;
        }
        
        String type = args.get(0).toLowerCase();
        
        switch (type) {
            case "m", "materials", "mats" -> {
                List<GameItem> items = new ArrayList<>();
                
                // Character/Relic/Lightcone upgrade materials
                for (ItemExcel excel : GameData.getItemExcelMap().values()) {
                    int purpose = excel.getPurposeType();
                    if ((purpose >= 1 && purpose <= 7) || purpose == 10) {
                        items.add(new GameItem(excel, 1000));
                    }
                }
                
                // Credits
                items.add(new GameItem(2, 50_000_000));
                
                // Add to target's inventory
                args.getTarget().getInventory().addItems(items, true);
                
                // Send message
                this.sendMessage(sender, "Giving " + args.getTarget().getName() + " " + items.size() + " items");
            }
            case "lc", "lightcones" -> {
                // Get lightcones
                List<GameItem> items = GameData.getItemExcelMap().values()
                        .stream()
                        .filter(ItemExcel::isEquipment)
                        .map(excel -> new GameItem(excel, 1))
                        .toList();
                
                // Add to target's inventory
                args.getTarget().getInventory().addItems(items, true);
                
                // Send message
                this.sendMessage(sender, "Giving " + args.getTarget().getName() + " " + items.size() + " light cones");
            }
            case "r", "relics" -> {
                // Get relics
                List<GameItem> items = GameData.getItemExcelMap().values()
                        .stream()
                        .filter(excel -> excel.isRelic() && excel.getRarity() == ItemRarity.SuperRare)
                        .map(excel -> new GameItem(excel, 1))
                        .toList();
                
                // Add to target's inventory
                args.getTarget().getInventory().addItems(items, true);
                
                // Send message
                this.sendMessage(sender, "Giving " + args.getTarget().getName() + " " + items.size() + " relics");
            }
            case "a", "characters", "avatars" -> {
                // All avatars and their eidolons
                for (ItemExcel excel : GameData.getItemExcelMap().values()) {
                    if (excel.getItemMainType() == ItemMainType.AvatarCard) {
                        args.getTarget().getInventory().addItem(excel, 1);
                    } else if (excel.getItemSubType() == ItemSubType.Eidolon) {
                        args.getTarget().getInventory().addItem(excel, 6);
                    }
                }
                
                // Send message
                this.sendMessage(sender, "Giving " + args.getTarget().getName() + " all avatars");
            }
        }
    }

}
