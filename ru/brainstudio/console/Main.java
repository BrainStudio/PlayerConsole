package ru.brainstudio.console;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;



import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
   Logger log;
   List<String> spys = new ArrayList();
   List<String> ignores = new ArrayList();
   String UseMsg;
   String OnMsg;
   String OffMsg;
   String ReloadMsg;

   public void onEnable() {
      this.log = this.getLogger();
      this.saveDefaultConfig();
      FileConfiguration config = this.getConfig();
      this.ignores = config.getStringList("ignore");
      this.UseMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.UseCmd"));
      this.OnMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.ConsoleOn"));
      this.OffMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.ConsoleOff"));
      this.ReloadMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.ReloadCmd"));
      Bukkit.getPluginManager().registerEvents(this, this);
      this.log.info("Плагин включен!");
   }

   public void onDisable() {
      this.log.info("Плагин выключен!!");
   }

   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
      if (sender instanceof Player) {
         Player p = (Player)sender;
         if (cmd.getName().equalsIgnoreCase("console") && args.length > 0) {
            if (args[0].equalsIgnoreCase("on")) {
               if (!p.hasPermission("console.use")) {
                  return false;
               }

               if (!this.spys.contains(p.getName())) {
                  this.spys.add(p.getName());
               }

               p.sendMessage(this.OnMsg);
            } else if (args[0].equalsIgnoreCase("off")) {
               if (!p.hasPermission("console.use")) {
                  return false;
               }

               if (this.spys.contains(p.getName())) {
                  this.spys.remove(p.getName());
               }

               p.sendMessage(this.OffMsg);
            } else if (args[0].equalsIgnoreCase("reload")) {
               if (!p.hasPermission("console.reload")) {
                  return false;
               }
               p.sendMessage(this.ReloadMsg);

               FileConfiguration config = this.getConfig();
               this.ignores = config.getStringList("ignore");
               this.UseMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.UseCmd"));
               this.OnMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.ConsoleOn"));
               this.OffMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.ConsoleOff"));
               this.ReloadMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.ReloadCmd"));
            }

            return true;
         }
      }

      return false;
   }
   @EventHandler
   public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
      if (event.getPlayer().hasPermission("console.spy")) {
         if (this.ignores.contains(event.getMessage().split(" ")[0])) {
            return;
         }

         Iterator var3 = this.spys.iterator();

         while(var3.hasNext()) {
            String name = (String)var3.next();
            if (Bukkit.getPlayer(name) != null && !name.equalsIgnoreCase(event.getPlayer().getName())) {
               Bukkit.getPlayer(name).sendMessage(this.UseMsg.replaceAll("%player%", event.getPlayer().getName()).replaceAll("%command%", event.getMessage()));
            }
         }
      }

   }
}
