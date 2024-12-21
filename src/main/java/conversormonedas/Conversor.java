package conversormonedas;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.Scanner;

public class Conversor {
    private static final String API_KEY = "e16b99b72ce059315cc3a17f";
    private static final String BASE_URL = "https://api.exchangerate-api.com/v4/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bienvenido al Conversor de Monedas");

        System.out.print("Ingrese el monto a convertir: ");
        double amount = scanner.nextDouble();

        System.out.print("Ingrese la moneda de origen (ARS, BOB, BRL, CLP, COP, USD): ");
        String fromCurrency = scanner.next().toUpperCase();

        System.out.print("Ingrese la moneda de destino (ARS, BOB, BRL, CLP, COP, USD): ");
        String toCurrency = scanner.next().toUpperCase();

        if (!isValidCurrency(fromCurrency) || !isValidCurrency(toCurrency)) {
            System.out.println("Moneda no válida. Solo se permiten las siguientes monedas: ARS, BOB, BRL, CLP, COP, USD.");
            return;
        }

        double result = convertCurrency(fromCurrency, toCurrency, amount);
        if (result != 0) {
            System.out.println("Resultado: " + result);
        } else {
            System.out.println("Hubo un error en la conversión.");
        }
    }

    private static boolean isValidCurrency(String currency) {
        return currency.equals("ARS") || currency.equals("BOB") || currency.equals("BRL") ||
               currency.equals("CLP") || currency.equals("COP") || currency.equals("USD");
    }

    private static double convertCurrency(String fromCurrency, String toCurrency, double amount) {
        OkHttpClient client = new OkHttpClient();

        String url = BASE_URL + fromCurrency + "?apikey=" + API_KEY;

        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();

                Gson gson = new Gson();
                ExchangeRateApiResponse exchangeRate = gson.fromJson(jsonResponse, ExchangeRateApiResponse.class);

                double fromRate = exchangeRate.rates.get(fromCurrency);
                double toRate = exchangeRate.rates.get(toCurrency);

                double result = amount * (toRate / fromRate);

                return result;
            } else {
                System.out.println("Error en la solicitud: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    class ExchangeRateApiResponse {
        public Rates rates;

        class Rates {
            public double ARS;
            public double BOB;
            public double BRL;
            public double CLP;
            public double COP;
            public double USD;

            public double get(String currency) {
                switch (currency) {
                    case "ARS": return ARS;
                    case "BOB": return BOB;
                    case "BRL": return BRL;
                    case "CLP": return CLP;
                    case "COP": return COP;
                    case "USD": return USD;
                    default: return 0;
                }
            }
        }
    }
}