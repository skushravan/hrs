package com.hotel.management.controller;

import com.hotel.management.entity.RestaurantTable;
import com.hotel.management.enums.TableStatus;
import com.hotel.management.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for handling table-related web requests
 */
@Controller
@RequestMapping("/tables")
public class TableController {

    @Autowired
    private TableService tableService;

    /**
     * Display all tables
     * @param model the model to pass data to the view
     * @return the tables list view
     */
    @GetMapping
    public String getAllTables(Model model) {
        try {
            List<RestaurantTable> tables = tableService.getAllTables();
            model.addAttribute("tables", tables);
            model.addAttribute("tableStatuses", TableStatus.values());
            
            // Add counts for each status
            model.addAttribute("availableCount", tableService.countTablesByStatus(TableStatus.AVAILABLE));
            model.addAttribute("occupiedCount", tableService.countTablesByStatus(TableStatus.OCCUPIED));
            model.addAttribute("reservedCount", tableService.countTablesByStatus(TableStatus.RESERVED));
            
            return "tables/list";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load tables: " + e.getMessage());
            return "tables/list";
        }
    }

    /**
     * Display the new table form
     * @param model the model to pass data to the view
     * @return the new table form view
     */
    @GetMapping("/new")
    public String showNewTableForm(Model model) {
        try {
            RestaurantTable table = new RestaurantTable();
            model.addAttribute("table", table);
            model.addAttribute("tableStatuses", TableStatus.values());
            return "tables/new";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load table form: " + e.getMessage());
            return "tables/new";
        }
    }

    /**
     * Handle form submission to create a new table
     * @param table the table data from the form
     * @param redirectAttributes attributes for redirect
     * @return redirect to tables list
     */
    @PostMapping
    public String createTable(@ModelAttribute RestaurantTable table, 
                            RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (table.getTableNumber() == null || table.getTableNumber().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Table number is required");
                return "redirect:/tables/new";
            }

            if (table.getCapacity() == null || table.getCapacity() < 1) {
                redirectAttributes.addFlashAttribute("error", "Capacity must be at least 1");
                return "redirect:/tables/new";
            }

            // Create the table
            RestaurantTable savedTable = tableService.createTable(table);
            
            redirectAttributes.addFlashAttribute("success", 
                "Table " + savedTable.getTableNumber() + " created successfully");
            
            return "redirect:/tables";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tables/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create table: " + e.getMessage());
            return "redirect:/tables/new";
        }
    }

    /**
     * Display tables by status
     * @param status the status to filter by
     * @param model the model to pass data to the view
     * @return the tables list view
     */
    @GetMapping("/status/{status}")
    public String getTablesByStatus(@PathVariable TableStatus status, Model model) {
        try {
            List<RestaurantTable> tables = tableService.getTablesByStatus(status);
            model.addAttribute("tables", tables);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("tableStatuses", TableStatus.values());
            
            // Add counts for each status
            model.addAttribute("availableCount", tableService.countTablesByStatus(TableStatus.AVAILABLE));
            model.addAttribute("occupiedCount", tableService.countTablesByStatus(TableStatus.OCCUPIED));
            model.addAttribute("reservedCount", tableService.countTablesByStatus(TableStatus.RESERVED));
            
            return "tables/list";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load tables by status: " + e.getMessage());
            return "redirect:/tables";
        }
    }

    /**
     * Update table status
     * @param id the table ID
     * @param status the new status
     * @param redirectAttributes attributes for redirect
     * @return redirect to tables list
     */
    @PostMapping("/{id}/status")
    public String updateTableStatus(@PathVariable Long id, 
                                  @RequestParam TableStatus status,
                                  RedirectAttributes redirectAttributes) {
        try {
            RestaurantTable updatedTable = tableService.updateTableStatus(id, status);
            
            redirectAttributes.addFlashAttribute("success", 
                "Table " + updatedTable.getTableNumber() + " status updated to " + status.getDisplayName());
            
            return "redirect:/tables";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tables";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update table status: " + e.getMessage());
            return "redirect:/tables";
        }
    }

    /**
     * Display table details
     * @param id the table ID
     * @param model the model to pass data to the view
     * @return the table details view
     */
    @GetMapping("/{id}")
    public String getTableDetails(@PathVariable Long id, Model model) {
        try {
            var tableOpt = tableService.getTableById(id);
            if (tableOpt.isEmpty()) {
                model.addAttribute("error", "Table not found");
                return "redirect:/tables";
            }
            
            model.addAttribute("table", tableOpt.get());
            model.addAttribute("tableStatuses", TableStatus.values());
            
            return "tables/details";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load table details: " + e.getMessage());
            return "redirect:/tables";
        }
    }

    /**
     * Get available tables (AJAX endpoint)
     * @return list of available tables
     */
    @GetMapping("/available")
    @ResponseBody
    public List<RestaurantTable> getAvailableTables() {
        try {
            return tableService.getAvailableTables();
        } catch (Exception e) {
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get available tables for a specific party size (AJAX endpoint)
     * @param partySize the party size
     * @return list of available tables with sufficient capacity
     */
    @GetMapping("/available-for-party")
    @ResponseBody
    public List<RestaurantTable> getAvailableTablesForParty(@RequestParam Integer partySize) {
        try {
            if (partySize == null || partySize < 1) {
                return List.of();
            }
            return tableService.getAvailableTablesForParty(partySize);
        } catch (Exception e) {
            return List.of(); // Return empty list on error
        }
    }
}
