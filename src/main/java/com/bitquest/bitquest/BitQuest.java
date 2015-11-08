package com.bitquest.bitquest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.util.List;


/**
 * Created by explodi on 11/1/15.
 */



public class BitQuest extends JavaPlugin {
    // Connecting to REDIS
    // Look for Environment variables on hostname and port, otherwise defaults to localhost:6379
    public final static String redisHost=System.getenv("REDIS_1_PORT_6379_TCP_ADDR")!=null ? System.getenv("REDIS_1_PORT_6379_TCP_ADDR") : "localhost";
    public final static Integer redisPort=System.getenv("REDIS_1_PORT_6379_TCP_PORT")!=null ? Integer.parseInt(System.getenv("REDIS_1_PORT_6379_TCP_PORT")) : 6379;
    public final static Jedis REDIS=new Jedis(redisHost,redisPort);

    @Override
    public void onEnable() {
        log("BitQuest starting...");
        log("REDIS is redis://"+redisHost+":"+redisPort);
        // registers listener classes
        getServer().getPluginManager().registerEvents(new BlockEvents(this),this);
        getServer().getPluginManager().registerEvents(new EntityEvents(this),this);

        // loads config file. If it doesn't exist, creates it.
        // get plugin config
        getDataFolder().mkdir();
        if(!new java.io.File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
    }
    public void log(String msg) {
        Bukkit.getLogger().info(msg);
    }

    public JsonObject areaForLocation(Location location) {
        List<String> areas=REDIS.lrange("areas",0,-1);
        for(String areaJSON : areas) {
            Gson gson = new Gson();
            JsonObject area=new JsonParser().parse(areaJSON).getAsJsonObject();
            int x=area.get("x").getAsInt();
            int z=area.get("z").getAsInt();
            int size=area.get("size").getAsInt();
            if(location.getX()>(x-size) && location.getX()<(x+size)&&location.getZ()>(z-size)&&location.getZ()>(z+size)) {
                return area;
            }
        }
        return null;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // we don't allow server commands (yet?)
        if(sender instanceof Player) {
            Player player=(Player) sender;
            // command to create abu new area
            if (cmd.getName().equalsIgnoreCase("addarea")) { // If the player typed /basic then do the following...
                // doSomething
                if(args[0]!=null && args[1]!=null) {
                    // first, check that arg[1] (size) is an integer
                    try {
                        int size=Integer.parseInt(args[1]);
                        if(size>0&&size<512) {
                            // ensure that arg[0] is alphanumeric and 2 characters minimum, 16 max
                            if(args[0].matches("^.*[^a-zA-Z0-9 ].*$") && args[0].length()>1 && args[0].length()<17) {
                                // write the new area to REDIS
                                JsonObject areaJSON=new JsonObject();

                                areaJSON.addProperty("size",size);
                                areaJSON.addProperty("owner",player.getUniqueId().toString());
                                areaJSON.addProperty("name",args[0]);
                                areaJSON.addProperty("x",player.getLocation().getX());
                                areaJSON.addProperty("z",player.getLocation().getZ());
                                REDIS.lpush("areas",areaJSON.toString());
                                player.sendMessage(ChatColor.GREEN+"Area '"+args[0]+"' created.");
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            // TODO: Explain the maximum and minimum value of the land size
                            return false;
                        }
                    } catch(Exception e) {
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Please specify area name and size");
                    return false;
                }
            } //If this has happened the function will return true.
            // If this hasn't happened the value of false will be returned.
        } else {
            sender.sendMessage(ChatColor.RED+"This command is for players only");
        }

        return false;
    }
}

