import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class WeatherAPI {

    public static void main(String[] args) {
        System.out.println("Welcome to the Java Weather API Reader!");

        Scanner input = new Scanner(System.in);

        System.out.println("Please enter your location.");
        System.out.println("1: West Bend, Wisconsin");
        System.out.println("2: Milwaukee, Wisconsin");
        System.out.println("3: Chicago, Illinois");
        System.out.print("Enter your choice: ");
        int choice = input.nextInt();

        String locationName;
        double latitude;
        double longitude;

        if (choice == 2) {
            locationName = "Milwaukee, Wisconsin";
            latitude = 43.0389;
            longitude = -87.9065;
        } else if (choice == 3) {
            locationName = "Chicago, Illinois";
            latitude = 41.8781;
            longitude = -87.6298;
        } else {
            locationName = "West Bend, Wisconsin";
            latitude = 43.4253;
            longitude = -88.1834;
        }

        // Build the URL
        String apiUrl = buildWeatherURL(latitude, longitude);

        System.out.println();
        System.out.println("Location: " + locationName);
        System.out.println("Attempting to connect to the weather API...");
        System.out.println();

        // Send the API call
        try {
            String response = getApiResponse(apiUrl);
            System.out.println();
            System.out.println("Raw API Response:");
            System.out.print(response);
            System.out.println();
            // Print as a summary
            String time = extractTextValue(response, "time");
            String temperature = extractNumberValue(response, "temperature_2m");
            String windSpeed = extractNumberValue(response, "wind_speed_10m");

            System.out.println("Simple weather summary:");
            System.out.println("Current time: " + time);
            System.out.println("Temperature: " + temperature);
            System.out.println("Wind speed: " + windSpeed);

            System.out.println();
            System.out.println("Weather data retrieved successfully!");
        } catch (IOException e) {
            System.out.println("Network or File error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("The request was interrupted.");
        } catch (Exception e) {
            System.out.println("An unknown error occurred: " + e.getMessage());
        }
    }

    // Builds the api URL
    public static String buildWeatherURL(double latitude, double longitude) {
        return "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + latitude
                + "&longitude=" + longitude
                + "&current=temperature_2m,wind_speed_10m"
                + "&temperature_unit=fahrenheit"
                + "&wind_speed_unit=mph";
    }

    public static String getApiResponse(String apiUrl) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        }

        return null;
    }

    public static String extractTextValue(String json, String key) {
        int currentIndex = json.indexOf("\"current\":{");

        if (currentIndex == -1) {
            return "Not Found";
        }

        String searchText = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchText, currentIndex);

        if (startIndex == -1) {
            return "Not Found";
        }

        startIndex = startIndex + searchText.length();
        int endIndex = json.indexOf("\"", startIndex);

        if (endIndex != -1) {
            return json.substring(startIndex, endIndex);
        }
        return "Not Found";
    }

    public static String extractNumberValue(String json, String key) {
        int currentIndex = json.indexOf("\"current\":{");

        if (currentIndex == -1) {
            return "Not Found";
        }

        String searchText = "\"" + key + "\":";
        int startIndex = json.indexOf(searchText, currentIndex);

        if (startIndex == -1) {
            return "Not Found";
        }

        startIndex = startIndex + searchText.length();
        int endIndex = startIndex;

        while (endIndex < json.length()) {
            char currentChar = json.charAt(endIndex);
            if ((currentChar >= '0' && currentChar <= '9') || currentChar == '.' || currentChar == '-') {
                endIndex++;
            } else {
                break;
            }
        }

        if (startIndex == endIndex) {
            return "Not Found";
        }

        return json.substring(startIndex, endIndex);
    }

}
