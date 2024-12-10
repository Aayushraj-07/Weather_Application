package com.weather_app.controller;

import com.weather_app.model.WeatherResponse; // Import the WeatherResponse model to parse API response
import org.springframework.beans.factory.annotation.Value; // For injecting values from application.properties
import org.springframework.stereotype.Controller; // Marks this class as a Spring MVC controller
import org.springframework.ui.Model; // Used for adding attributes to the view
import org.springframework.web.bind.annotation.GetMapping; // Marks methods for HTTP GET requests
import org.springframework.web.bind.annotation.RequestParam; // Handles HTTP request parameters
import org.springframework.web.client.RestTemplate; // Makes HTTP requests to the weather API
import org.springframework.http.HttpStatus; // To handle HTTP status codes
import org.springframework.ui.ModelMap; // For adding error messages in the model
import org.springframework.web.bind.annotation.ExceptionHandler; // To handle exceptions

@Controller
public class WeatherController {

    @Value("${api.key}") // Injects the API key from application.properties
    private String apiKey;

    // Handles the route for the homepage (index page)
    @GetMapping("/abc")
    public String getIndex() {
        return "index"; // Returns the index page when the user navigates to "/abc"
    }

    // Handles the route for the weather page, takes the city name as a request parameter
    @GetMapping("/weather")
    public String getWeather(@RequestParam("city") String city, Model model) {
        try {
            // Constructs the URL for the weather API call, using the city and API key
            String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appId=" + apiKey + "&units=metric";

            // Creates a RestTemplate instance to send a GET request to the API
            RestTemplate restTemplate = new RestTemplate();

            // Sends the request and maps the response to a WeatherResponse object
            WeatherResponse weatherResponse = restTemplate.getForObject(url, WeatherResponse.class);

            // Checks if the response is not null (valid city)
            if (weatherResponse != null) {
                // Adds weather details as model attributes to be displayed in the view
                model.addAttribute("city", weatherResponse.getName()); // City name
                model.addAttribute("country", weatherResponse.getSys().getCountry()); // Country name
                model.addAttribute("weatherDescription", weatherResponse.getWeather().get(0).getDescription()); // Weather description
                model.addAttribute("temperature", weatherResponse.getMain().getTemp()); // Temperature in Celsius
                model.addAttribute("humidity", weatherResponse.getMain().getHumidity()); // Humidity percentage
                model.addAttribute("windSpeed", weatherResponse.getWind().getSpeed()); // Wind speed
                // Sets the weather icon class to be used in the front-end
                String weatherIcon = "wi wi-owm-" + weatherResponse.getWeather().get(0).getId();
                model.addAttribute("weatherIcon", weatherIcon);
            } else {
                // If no valid data is found, add an error message to the model
                model.addAttribute("error", "City not found");
            }

        } catch (Exception e) {
            // If an exception occurs, add an error message to the model
            model.addAttribute("error", "enter valid city name: " + e.getMessage());
        }

        // Returns the weather page to display the weather details
        return "weather";
    }

    // Global exception handler for handling specific exceptions, e.g., API request failure
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, ModelMap model) {
        model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
        return "error"; // Returns a generic error page
    }
}
