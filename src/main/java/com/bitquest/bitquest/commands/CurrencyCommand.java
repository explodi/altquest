package com.bitquest.bitquest.commands;

import com.bitquest.bitquest.BitQuest;
import com.bitquest.bitquest.User;
import com.bitquest.bitquest.Wallet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.ParseException;


public class CurrencyCommand extends CommandAction {
    private BitQuest bitQuest;

    public CurrencyCommand(BitQuest plugin) {
        bitQuest = plugin;
    }

    public boolean run(CommandSender sender, Command cmd, String label, String[] args, final Player player) {
        //CHANGE CURRENCY BY BITCOINJAKE09
		 if(cmd.getName().equalsIgnoreCase("currency")) {
			
			if((args[0].equalsIgnoreCase("emerald"))||(args[0].equalsIgnoreCase("emerald"))) {
				BitQuest.REDIS.set("currency"+player.getUniqueId().toString(), "emerald");
			player.sendMessage(ChatColor.GREEN+"Currency changed to " + BitQuest.REDIS.get("currency"+player.getUniqueId().toString()));                        

				}
			else if((args[0].equalsIgnoreCase("btc"))||(args[0].equalsIgnoreCase("bitcoin"))) {
				BitQuest.REDIS.set("currency"+player.getUniqueId().toString(), "bitcoin");
				player.sendMessage(ChatColor.GREEN+"Currency changed to " + BitQuest.REDIS.get("currency"+player.getUniqueId().toString()));
	                               						   
			} else {
                    player.sendMessage(ChatColor.RED+"There was a problem changing your currency.");
                } 

		return true; 	
		}
		return false; 	
	}
}
