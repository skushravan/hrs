package com.hotel.management.controller;

import com.hotel.management.entity.InventoryItem;
import com.hotel.management.entity.InventoryTransaction;
import com.hotel.management.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public String list(Model model) {
        List<InventoryItem> items = inventoryService.getAllItems();
        model.addAttribute("items", items);
        model.addAttribute("lowStockItems", inventoryService.getLowStockItems());
        return "inventory";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("item", new InventoryItem());
        return "inventory/new";
    }

    @PostMapping
    public String create(@ModelAttribute InventoryItem item, RedirectAttributes ra) {
        try {
            InventoryItem saved = inventoryService.createItem(item);
            ra.addFlashAttribute("success", "Item '" + saved.getName() + "' created");
            return "redirect:/inventory";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/inventory/new";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to create item: " + e.getMessage());
            return "redirect:/inventory/new";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            var itemOpt = inventoryService.getItemById(id);
            if (itemOpt.isEmpty()) {
                ra.addFlashAttribute("error", "Item not found");
                return "redirect:/inventory";
            }
            InventoryItem item = itemOpt.get();
            List<InventoryTransaction> tx = inventoryService.getItemTransactions(id);
            model.addAttribute("item", item);
            model.addAttribute("transactions", tx);
            return "inventory/details";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to load item: " + e.getMessage());
            return "redirect:/inventory";
        }
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute InventoryItem item, RedirectAttributes ra) {
        try {
            InventoryItem saved = inventoryService.updateItem(id, item);
            ra.addFlashAttribute("success", "Item '" + saved.getName() + "' updated");
            return "redirect:/inventory";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/inventory/" + id;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to update item: " + e.getMessage());
            return "redirect:/inventory/" + id;
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            inventoryService.deleteItem(id);
            ra.addFlashAttribute("success", "Item deleted");
            return "redirect:/inventory";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/inventory";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to delete item: " + e.getMessage());
            return "redirect:/inventory";
        }
    }

    @PostMapping("/{id}/tx")
    public String createTransaction(@PathVariable Long id,
                                    @RequestParam InventoryTransaction.Type type,
                                    @RequestParam int quantity,
                                    @RequestParam(required = false) String note,
                                    @RequestParam(required = false) String createdBy,
                                    RedirectAttributes ra) {
        try {
            inventoryService.recordTransaction(id, type, quantity, note, createdBy);
            ra.addFlashAttribute("success", "Transaction recorded");
            return "redirect:/inventory/" + id;
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/inventory/" + id;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to record transaction: " + e.getMessage());
            return "redirect:/inventory/" + id;
        }
    }

    @GetMapping("/low-stock")
    public String lowStock(Model model) {
        model.addAttribute("items", inventoryService.getLowStockItems());
        model.addAttribute("lowStockOnly", true);
        return "inventory";
    }
}


