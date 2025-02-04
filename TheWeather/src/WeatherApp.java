import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherApp extends Application {
    private static final String API_KEY = "818107cfe1dbc8e91dacbaebcbf9bee0";
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=" + API_KEY;

    @Override
    public void start(Stage primaryStage) {
        // Установка иконки окна
        primaryStage.getIcons().add(new Image("file:icon.jpg")); // Укажите путь к вашему изображению

        // Заголовок
        Label titleLabel = new Label("ПОГОДА");
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);

        // Поле ввода города
        TextField cityField = new TextField();
        cityField.setPromptText("Введите город");
        cityField.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-border-color: white;");
        cityField.setMaxWidth(250);
        cityField.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Кнопка запроса погоды
        Button searchButton = new Button("УЗНАТЬ ПОГОДУ");
        searchButton.setStyle("-fx-background-color: #666; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12px 24px;");
        searchButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Метка информации
        Label infoLabel = new Label("ИНФОРМАЦИЯ");
        infoLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 22));
        infoLabel.setTextFill(Color.RED);

        Label weatherLabel = new Label();
        weatherLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        weatherLabel.setTextFill(Color.WHITE);

        searchButton.setOnAction(event -> {
            String city = cityField.getText();
            if (!city.isEmpty()) {
                String weatherInfo = getWeather(city);
                weatherLabel.setText(weatherInfo);
            }
        });

        // Размещение элементов
        VBox vbox = new VBox(15, titleLabel, cityField, searchButton, infoLabel, weatherLabel);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: linear-gradient(to bottom, #111, #222);");

        Scene scene = new Scene(vbox, 400, 550);
        primaryStage.setTitle("ПОГОДА");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String getWeather(String city) {
        try {
            URL url = new URL(String.format(WEATHER_URL, city));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return "Такого города не существует";
            }

            Scanner scanner = new Scanner(url.openStream());
            StringBuilder inline = new StringBuilder();
            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject jsonObject = new JSONObject(inline.toString());
            double temp = jsonObject.getJSONObject("main").getDouble("temp");
            double feelsLike = jsonObject.getJSONObject("main").getDouble("feels_like");
            double tempMin = jsonObject.getJSONObject("main").getDouble("temp_min");
            double tempMax = jsonObject.getJSONObject("main").getDouble("temp_max");
            int pressure = jsonObject.getJSONObject("main").getInt("pressure");
            String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

            return String.format(
                    " Температура: %.1f°C\n" +
                            " Ощущается: %.1f°C\n" +
                            " Максимум: %.1f°C\n" +
                            " Минимум: %.1f°C\n" +
                            " Давление: %d hPa\n" +
                            " Небо: %s",
                    temp, feelsLike, tempMax, tempMin, pressure, description
            );
        } catch (IOException e) {
            return "Ошибка подключения";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
