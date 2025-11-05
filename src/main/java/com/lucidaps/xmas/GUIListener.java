package com.lucidaps.xmas;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        
        // Check if it's our custom GUI
        if (title.contains("Weihnachtsbaum Status")) {
            event.setCancelled(true); // Prevent moving items
        }
    }
}
