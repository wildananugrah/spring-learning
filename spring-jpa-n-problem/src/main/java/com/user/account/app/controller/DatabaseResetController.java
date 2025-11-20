package com.user.account.app.controller;

import com.user.account.app.service.DatabaseResetService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/database")
public class DatabaseResetController {

    private final DatabaseResetService resetService;

    public DatabaseResetController(DatabaseResetService resetService) {
        this.resetService = resetService;
    }

    /**
     * Reset auto-increment sequences only (data remains)
     */
    @PostMapping("/reset-sequences")
    public Map<String, String> resetSequences() {
        resetService.resetAutoIncrementSequences();

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Auto-increment sequences reset to 1");
        response.put("warning", "Existing data kept, but new records will start from ID 1 (may cause conflicts)");
        return response;
    }

    /**
     * Delete all data and reset sequences
     */
    @PostMapping("/reset-all")
    public Map<String, String> resetDatabase() {
        resetService.resetDatabase();

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Database reset complete");
        response.put("info", "All data deleted and sequences reset to 1");
        return response;
    }

    /**
     * Truncate tables and reset sequences (faster)
     */
    @PostMapping("/truncate")
    public Map<String, String> truncateAndReset() {
        resetService.truncateAndReset();

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Tables truncated and sequences reset");
        response.put("info", "Fastest way to clean database and reset IDs");
        return response;
    }

    /**
     * Show current sequence values
     */
    @GetMapping("/sequence-info")
    public Map<String, Object> getSequenceInfo() {
        resetService.showCurrentSequenceValues();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Check console for sequence values");
        response.put("info", "Current sequence values displayed in server console");
        return response;
    }

    /**
     * Reset specific sequence to specific value
     */
    @PostMapping("/reset-sequence/{tableName}/{columnName}/{startValue}")
    public Map<String, String> resetSpecificSequence(
            @PathVariable String tableName,
            @PathVariable String columnName,
            @PathVariable long startValue) {

        resetService.resetSequenceToValue(tableName, columnName, startValue);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Sequence " + tableName + "_" + columnName + "_seq reset to " + startValue);
        return response;
    }
}
