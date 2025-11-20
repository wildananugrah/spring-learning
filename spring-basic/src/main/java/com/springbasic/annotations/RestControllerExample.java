package com.springbasic.annotations;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @RestController Annotation Example
 *
 * @RestController is a convenience annotation that combines @Controller and @ResponseBody.
 * It indicates that the class is a REST API controller that returns data (JSON/XML) instead
 * of view names.
 *
 * Key characteristics:
 * - Combines @Controller + @ResponseBody
 * - All methods automatically return data as JSON/XML
 * - No need to add @ResponseBody on each method
 * - Default choice for REST APIs
 * - Cleaner code compared to @Controller + @ResponseBody
 *
 * When to use:
 * - REST API endpoints
 * - When returning JSON/XML data
 * - Microservices
 * - Backend APIs for frontend applications
 *
 * @RestController = @Controller + @ResponseBody
 *
 * @author Spring Basic Tutorial
 */
@RestController
@RequestMapping("/api/annotations/rest")
@RequiredArgsConstructor
public class RestControllerExample {

    private final RepositoryExample repositoryExample;
    private int apiCallCount = 0;

    /**
     * Example GET endpoint
     * No @ResponseBody needed - @RestController handles it
     *
     * @return API information
     */
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        apiCallCount++;

        Map<String, Object> response = new HashMap<>();
        response.put("annotation", "@RestController");
        response.put("description", "Convenience annotation combining @Controller + @ResponseBody");
        response.put("apiCallCount", apiCallCount);
        response.put("hashCode", this.hashCode());
        response.put("repositoryInfo", repositoryExample.getRepositoryInfo());
        response.put("advantage", "No need to add @ResponseBody on each method");

        return response;
    }

    /**
     * Example POST endpoint
     * Demonstrates automatic JSON serialization/deserialization
     *
     * @param request request data
     * @return processed response
     */
    @PostMapping("/process")
    public Map<String, Object> processData(@RequestBody Map<String, Object> request) {
        apiCallCount++;

        Map<String, Object> response = new HashMap<>();
        response.put("receivedData", request);
        response.put("processedAt", System.currentTimeMillis());
        response.put("apiCallCount", apiCallCount);
        response.put("note", "Request body is automatically deserialized from JSON");

        return response;
    }

    /**
     * Example PUT endpoint
     *
     * @param id resource ID
     * @param data resource data
     * @return update result
     */
    @PutMapping("/update/{id}")
    public Map<String, Object> updateResource(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data) {

        apiCallCount++;

        Map<String, Object> response = new HashMap<>();
        response.put("action", "UPDATE");
        response.put("resourceId", id);
        response.put("updatedData", data);
        response.put("timestamp", System.currentTimeMillis());
        response.put("note", "Path variables and request body handled automatically");

        return response;
    }

    /**
     * Example DELETE endpoint
     *
     * @param id resource ID
     * @return delete result
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteResource(@PathVariable Long id) {
        apiCallCount++;

        Map<String, Object> response = new HashMap<>();
        response.put("action", "DELETE");
        response.put("resourceId", id);
        response.put("deleted", true);
        response.put("timestamp", System.currentTimeMillis());

        return response;
    }

    /**
     * Example GET endpoint with query parameters
     *
     * @param search search query
     * @param page page number
     * @param size page size
     * @return search results
     */
    @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        apiCallCount++;

        Map<String, Object> response = new HashMap<>();
        response.put("searchQuery", search);
        response.put("page", page);
        response.put("size", size);
        response.put("note", "Query parameters handled with @RequestParam");
        response.put("totalApiCalls", apiCallCount);

        return response;
    }

    /**
     * Gets API statistics
     *
     * @return API statistics
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalApiCalls", apiCallCount);
        response.put("controllerHashCode", this.hashCode());
        response.put("annotationType", "@RestController");
        response.put("benefits", new String[]{
            "No @ResponseBody needed on methods",
            "Cleaner code for REST APIs",
            "Automatic JSON serialization/deserialization",
            "Better readability",
            "Standard choice for REST APIs"
        });

        return response;
    }

    /**
     * Compares @Controller vs @RestController
     *
     * @return comparison
     */
    @GetMapping("/compare")
    public Map<String, Object> compareWithController() {
        Map<String, Object> controller = new HashMap<>();
        controller.put("annotation", "@Controller");
        controller.put("purpose", "Traditional MVC controller");
        controller.put("returns", "View names (templates)");
        controller.put("responseBody", "Required @ResponseBody for JSON");
        controller.put("useCase", "Server-side rendering with templates");

        Map<String, Object> restController = new HashMap<>();
        restController.put("annotation", "@RestController");
        restController.put("purpose", "REST API controller");
        restController.put("returns", "Data (JSON/XML)");
        restController.put("responseBody", "Built-in (no need for @ResponseBody)");
        restController.put("useCase", "REST APIs, microservices, backend for SPAs");

        Map<String, Object> response = new HashMap<>();
        response.put("controller", controller);
        response.put("restController", restController);
        response.put("recommendation", "Use @RestController for REST APIs, @Controller for view rendering");

        return response;
    }
}
