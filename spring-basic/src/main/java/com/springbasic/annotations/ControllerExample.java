package com.springbasic.annotations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Controller Annotation Example
 *
 * @Controller is a specialization of @Component annotation.
 * It indicates that the class is a Spring MVC controller that handles web requests.
 *
 * Key characteristics:
 * - Semantic specialization of @Component
 * - Indicates web presentation layer
 * - Auto-detected through component scanning
 * - Works with @RequestMapping and related annotations
 * - Returns view names (templates) by default
 * - Requires @ResponseBody for REST responses
 *
 * When to use:
 * - Traditional Spring MVC controllers
 * - When returning view templates (Thymeleaf, JSP, etc.)
 * - When mixing view rendering and REST endpoints
 *
 * Difference from @RestController:
 * - @Controller returns view names (HTML templates)
 * - @RestController = @Controller + @ResponseBody (returns data as JSON/XML)
 *
 * Note: For REST APIs, prefer @RestController over @Controller + @ResponseBody
 *
 * @author Spring Basic Tutorial
 */
@Controller
@RequestMapping("/api/annotations/controller")
@RequiredArgsConstructor
public class ControllerExample {

    private final ServiceExample serviceExample;
    private int requestCount = 0;

    /**
     * Example endpoint returning JSON data
     * Requires @ResponseBody to return data instead of view name
     *
     * @return controller information
     */
    @GetMapping("/info")
    @ResponseBody  // Required to return JSON data instead of view name
    public Map<String, Object> getInfo() {
        requestCount++;

        Map<String, Object> response = new HashMap<>();
        response.put("annotation", "@Controller");
        response.put("description", "Spring MVC controller for web requests");
        response.put("requestCount", requestCount);
        response.put("hashCode", this.hashCode());
        response.put("serviceInfo", serviceExample.getServiceInfo());
        response.put("note", "@ResponseBody is required to return JSON. " +
            "Without it, Spring would look for a view template named 'info'");

        return response;
    }

    /**
     * Example showing traditional controller behavior
     * This would return a view name (e.g., "welcome.html" template)
     *
     * Note: This is commented out because we don't have view templates
     * In a real application with Thymeleaf, this would work:
     *
     * @GetMapping("/welcome")
     * public String showWelcomePage(Model model) {
     *     model.addAttribute("message", "Welcome!");
     *     return "welcome";  // Returns view name, not JSON
     * }
     *
     * @return view name
     */
    @GetMapping("/view-example")
    @ResponseBody
    public Map<String, String> viewExample() {
        return Map.of(
            "explanation", "In a traditional @Controller, methods return view names (String)",
            "example", "return 'welcome' would render welcome.html template",
            "difference", "@RestController doesn't need @ResponseBody - it always returns data",
            "currentBehavior", "@ResponseBody makes this @Controller endpoint return JSON like @RestController"
        );
    }

    /**
     * Gets controller statistics
     *
     * @return controller statistics
     */
    @GetMapping("/stats")
    @ResponseBody
    public Map<String, Object> getStats() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalRequests", requestCount);
        response.put("controllerHashCode", this.hashCode());
        response.put("annotationType", "@Controller (with @ResponseBody)");
        response.put("recommendation", "Use @RestController for REST APIs instead of @Controller + @ResponseBody");

        return response;
    }
}
