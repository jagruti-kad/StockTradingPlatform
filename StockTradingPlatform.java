import java.io.*;
import java.util.*;

// Class representing a Stock
class Stock {
    private String name;
    private double price;

    public Stock(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void updatePrice(double newPrice) {
        this.price = newPrice;
    }

    @Override
    public String toString() {
        return name + " - " + String.format("%.2f", price);
    }
}

// Class representing the user's portfolio
class Portfolio {
    private Map<String, Integer> ownedStocks;
    private double balance;

    public Portfolio() {
        ownedStocks = new HashMap<>();
        balance = 10000.0; // starting balance
        loadPortfolio();
    }

    public void buyStock(Stock stock, int quantity) {
        double totalCost = stock.getPrice() * quantity;
        if (totalCost > balance) {
            System.out.println("Not enough balance to buy " + quantity + " shares of " + stock.getName());
            return;
        }
        balance -= totalCost;
        ownedStocks.put(stock.getName(), ownedStocks.getOrDefault(stock.getName(), 0) + quantity);
        System.out.println("Bought " + quantity + " shares of " + stock.getName());
        savePortfolio();
    }

    public void sellStock(Stock stock, int quantity) {
        if (!ownedStocks.containsKey(stock.getName()) || ownedStocks.get(stock.getName()) < quantity) {
            System.out.println("You don't own enough shares of " + stock.getName());
            return;
        }
        double totalGain = stock.getPrice() * quantity;
        balance += totalGain;
        ownedStocks.put(stock.getName(), ownedStocks.get(stock.getName()) - quantity);
        if (ownedStocks.get(stock.getName()) == 0) {
            ownedStocks.remove(stock.getName());
        }
        System.out.println("Sold " + quantity + " shares of " + stock.getName());
        savePortfolio();
    }

    public void viewPortfolio(Map<String, Stock> market) {
        System.out.println("\nPortfolio Summary:");
        if (ownedStocks.isEmpty()) {
            System.out.println("No stocks owned.");
        } else {
            double totalValue = 0;
            for (String name : ownedStocks.keySet()) {
                int qty = ownedStocks.get(name);
                double stockValue = qty * market.get(name).getPrice();
                totalValue += stockValue;
                System.out.println(name + " - " + qty + " shares - " + String.format("%.2f", stockValue));
            }
            System.out.println("Total Stock Value: " + String.format("%.2f", totalValue));
        }
        System.out.println("Cash Balance: " + String.format("%.2f", balance));
        System.out.println("Total Portfolio Value: " + String.format("%.2f", totalValue() + balance));
    }

    public double totalValue() {
        return balance;
    }

    // Save portfolio to file
    private void savePortfolio() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("portfolio.txt"))) {
            bw.write(balance + "\n");
            for (String stock : ownedStocks.keySet()) {
                bw.write(stock + "," + ownedStocks.get(stock) + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving portfolio: " + e.getMessage());
        }
    }

    // Load portfolio from file
    private void loadPortfolio() {
        File file = new File("portfolio.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            balance = Double.parseDouble(br.readLine());
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                ownedStocks.put(parts[0], Integer.parseInt(parts[1]));
            }
        } catch (IOException e) {
            System.out.println("Error loading portfolio: " + e.getMessage());
        }
    }
}

// Main class
public class StockTradingPlatform {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Map<String, Stock> market = new HashMap<>();
        market.put("TCS", new Stock("TCS", 3500));
        market.put("INFY", new Stock("INFY", 1550));
        market.put("RELIANCE", new Stock("RELIANCE", 2600));
        market.put("WIPRO", new Stock("WIPRO", 450));
        market.put("HDFC", new Stock("HDFC", 1700));

        Portfolio portfolio = new Portfolio();
        Random random = new Random();
        int choice;

        do {
            System.out.println("\n===== STOCK TRADING PLATFORM =====");
            System.out.println("1. View Market Data");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\nCurrent Market Data:");
                    for (Stock s : market.values()) {
                        // simulate small random price change
                        double newPrice = s.getPrice() + (random.nextDouble() * 100 - 50);
                        s.updatePrice(Math.max(newPrice, 1));
                        System.out.println(s);
                    }
                    break;

                case 2:
                    System.out.print("Enter stock name to buy: ");
                    String buyName = sc.next().toUpperCase();
                    if (!market.containsKey(buyName)) {
                        System.out.println("Stock not found!");
                        break;
                    }
                    System.out.print("Enter quantity: ");
                    int buyQty = sc.nextInt();
                    portfolio.buyStock(market.get(buyName), buyQty);
                    break;

                case 3:
                    System.out.print("Enter stock name to sell: ");
                    String sellName = sc.next().toUpperCase();
                    if (!market.containsKey(sellName)) {
                        System.out.println("Stock not found!");
                        break;
                    }
                    System.out.print("Enter quantity: ");
                    int sellQty = sc.nextInt();
                    portfolio.sellStock(market.get(sellName), sellQty);
                    break;

                case 4:
                    portfolio.viewPortfolio(market);
                    break;

                case 5:
                    System.out.println("Exiting. Portfolio saved successfully!");
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 5);
        sc.close();
    }
}
