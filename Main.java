import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    private static JTextField display; // Ana ekran
    private static JLabel previousOperation; // Önceki işlem ekranı
    private static String currentOperator = ""; // Son kullanılan operatör
    private static double result = 0; // Hesaplama sonucu
    private static boolean isNewInput = true; // Yeni giriş kontrolü

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hesap Makinesi");
        frame.setSize(400, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Pencereyi ekranın ortasında aç

        // Koyu mod renkleri ayarları
        Color backgroundColor = new Color(30, 30, 30);
        Color textColor = new Color(255, 255, 255);
        Color secondaryTextColor = new Color(180, 180, 180);
        Color buttonColor = new Color(50, 50, 50);
        Color buttonHoverColor = new Color(70, 70, 70);

        // Ana panel
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(backgroundColor);

        // Önceki işlem ekranı
        previousOperation = new JLabel(" ");
        previousOperation.setFont(new Font("Arial", Font.ITALIC, 16));
        previousOperation.setForeground(secondaryTextColor);
        previousOperation.setHorizontalAlignment(SwingConstants.RIGHT);
        previousOperation.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        // Ana ekran
        display = new JTextField("0");
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 32));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBackground(backgroundColor);
        display.setForeground(textColor);
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Buton paneli
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 10, 10));
        buttonPanel.setBackground(backgroundColor);

        // Hesap makinesi butonları
        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "C", "+",
                "(-)", "=" // Negatif sayılar için buton eklendi
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 24));
            button.setBackground(buttonColor);
            button.setForeground(textColor);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Hover efekti ekle
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(buttonHoverColor);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(buttonColor);
                }
            });

            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        // Ana panel düzenlemesi
        panel.add(previousOperation, BorderLayout.NORTH);
        panel.add(display, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(panel);
        frame.setVisible(true);
    }

    private static class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            // Sayı butonları ve nokta
            if (command.matches("\\d") || command.equals(".")) {
                if (isNewInput) {
                    display.setText(command); // Yeni giriş ise ekranı temizle
                    isNewInput = false;
                } else {
                    display.setText(display.getText() + command); // Mevcut değere ekle
                }
            }
            // Temizle butonu
            else if (command.equals("C")) {
                display.setText("0");
                previousOperation.setText(" ");
                result = 0;
                currentOperator = "";
                isNewInput = true;
            }
            // Eşittir butonu
            else if (command.equals("=")) {
                try {
                    calculate(Double.parseDouble(display.getText()));
                    previousOperation.setText(formatPreviousOperation(display.getText()));
                    display.setText(formatResult(result)); // Sonucu ekrana yaz
                } catch (ArithmeticException ex) {
                    display.setText("Hata: " + ex.getMessage());
                }
                currentOperator = ""; // Operatörü sıfırla
                isNewInput = true;
            }
            // Negatif butonu
            else if (command.equals("(-)")) {
                if (!display.getText().equals("0")) {
                    display.setText(display.getText().startsWith("-")
                            ? display.getText().substring(1)
                            : "-" + display.getText());
                }
            }
            // Operatörler
            else {
                if (!isNewInput) {
                    calculate(Double.parseDouble(display.getText())); // Mevcut sonucu hesapla
                }
                previousOperation.setText(formatPreviousOperation(display.getText()));
                currentOperator = command; // Yeni operatörü ata
                isNewInput = true; // Yeni giriş için bekle
            }
        }
    }

    private static void calculate(double input) {
        // Operatöre göre işlemleri gerçekleştir
        switch (currentOperator) {
            case "+":
                result += input;
                break;
            case "-":
                result -= input;
                break;
            case "*":
                result *= input;
                break;
            case "/":
                if (input != 0) {
                    result /= input;
                } else {
                    throw new ArithmeticException("Sıfıra bölünemez");
                }
                break;
            default:
                result = input; // İlk sayı olarak ata
                break;
        }
    }

    private static String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result); // Tam sayı için
        } else {
            return String.format("%.2f", result); // Ondalıklı sayı için
        }
    }

    private static String formatPreviousOperation(String input) {
        return String.format("%s %s", result, currentOperator.isEmpty() ? "" : currentOperator + " " + input);
    }
}