import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class Exchange_rate extends JFrame {
    private JTextField amountField;
    private JComboBox<String> fromCurrencyCombo;
    private JComboBox<String> toCurrencyCombo;
    private JLabel resultLabel;

    // مفتاح API من Open Exchange Rates
    private final String API_KEY = "YOUR_API_KEY_HERE"; // استبدل بمفتاح API الخاص بك
    private final String API_URL = "https://openexchangerates.org/api/latest.json?app_id=" + API_KEY;

    public Exchange_rate() {
        setTitle("محول العملات مع API");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // إنشاء مكونات الواجهة
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel amountLabel = new JLabel("المبلغ:");
        amountField = new JTextField(10);

        JLabel fromCurrencyLabel = new JLabel("من العملة:");
        String[] currencies = {"USD", "EUR", "GBP", "JPY", "AUD"}; // يمكن إضافة المزيد من العملات
        fromCurrencyCombo = new JComboBox<>(currencies);

        JLabel toCurrencyLabel = new JLabel("إلى العملة:");
        toCurrencyCombo = new JComboBox<>(currencies);

        JButton convertButton = new JButton("تحويل");
        resultLabel = new JLabel("النتيجة ستظهر هنا");

        // إضافة المكونات إلى اللوحة
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(fromCurrencyLabel);
        panel.add(fromCurrencyCombo);
        panel.add(toCurrencyLabel);
        panel.add(toCurrencyCombo);
        panel.add(convertButton);
        panel.add(resultLabel);

        // إضافة اللوحة إلى النافذة
        add(panel);

        // إضافة حدث الزر
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });
    }

    private void convertCurrency() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String fromCurrency = (String) fromCurrencyCombo.getSelectedItem();
            String toCurrency = (String) toCurrencyCombo.getSelectedItem();

            // جلب أسعار الصرف من API
            JSONObject exchangeRates = getExchangeRates();

            if (exchangeRates != null) {
                // التحقق من وجود العملات في الرد
                if (exchangeRates.has(fromCurrency) && exchangeRates.has(toCurrency)) {
                    double fromRate = exchangeRates.getDouble(fromCurrency);
                    double toRate = exchangeRates.getDouble(toCurrency);

                    // حساب النتيجة
                    double result = (amount / fromRate) * toRate;

                    // عرض النتيجة
                    resultLabel.setText(String.format("%.2f %s = %.2f %s", amount, fromCurrency, result, toCurrency));
                } else {
                    resultLabel.setText("العملة المحددة غير مدعومة.");
                }
            } else {
                resultLabel.setText("فشل في جلب أسعار الصرف.");
            }
        } catch (NumberFormatException ex) {
            resultLabel.setText("الرجاء إدخال مبلغ صحيح.");
        } catch (Exception ex) {
            resultLabel.setText("حدث خطأ: " + ex.getMessage());
        }
    }

    private JSONObject getExchangeRates() {
        try {
            // إنشاء اتصال HTTP
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // قراءة الرد
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // طباعة الرد للتأكد من صحته
            System.out.println("API Response: " + response.toString());

            // تحليل JSON
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONObject("rates");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Exchange_rate().setVisible(true);
            }
        });
    }
}