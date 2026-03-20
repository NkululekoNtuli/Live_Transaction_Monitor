import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.time.LocalDate;
import java.util.ArrayList;

public class TransactionApi {
    private final Javalin server;

    public TransactionApi() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        this.server = Javalin.create( javalinConfig -> {
                    javalinConfig.jsonMapper(new JavalinJackson(mapper));}).
                get("/transactions", ctx -> {
                ArrayList<Transaction> transactions = getTransactions();
                ctx.json(transactions);
            });
    }

    public static void main(String[] args) {
        TransactionApi server = new TransactionApi();
        server.start(8081);
    }

    public void start(int port) {
        this.server.start(port);
    }

    public ArrayList<Transaction> getTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add( new Transaction(0, "John Doh", "Nkululeko Ntuli", "Portrait",
                PaymentMethod.PayShap, LocalDate.of(2026, 2, 5).toString(), 500.00, Status.Success));
        transactions.add( new Transaction(1, "Nkululeko Ntuli", "Boston Dynamics", "Atlas Robot",
                PaymentMethod.Credit, LocalDate.of(2026, 2, 5).toString(), 20000.00, Status.Success));
        transactions.add( new Transaction(2, "Thabo Mokoena", "Tesla", "Model S",
                PaymentMethod.Credit, LocalDate.of(2026, 1, 15).toString(), 85000.00, Status.Success));
        transactions.add( new Transaction(3, "Ayanda Dlamini", "Apple", "MacBook Pro",
                PaymentMethod.ApplePay, LocalDate.of(2026, 2, 10).toString(), 32000.00, Status.Success));
        transactions.add( new Transaction(4, "Lerato Khumalo", "Samsung", "Galaxy S25",
                PaymentMethod.GooglePay, LocalDate.of(2026, 3, 2).toString(), 18000.00, Status.Pending));
        transactions.add( new Transaction(5, "Sipho Nkosi", "Sony", "PlayStation 5",
                PaymentMethod.PayShap, LocalDate.of(2026, 1, 25).toString(), 12000.00, Status.Success));
        transactions.add( new Transaction(6, "Zanele Ncube", "Nike", "Air Max Sneakers",
                PaymentMethod.Debit, LocalDate.of(2026, 2, 18).toString(), 2500.00, Status.Failed));
        transactions.add( new Transaction(7, "Kabelo Sithole", "Dell", "XPS 15 Laptop",
                PaymentMethod.Credit, LocalDate.of(2026, 3, 5).toString(), 40000.00, Status.Success));
        transactions.add( new Transaction(8, "Nomsa Mthembu", "Amazon", "Kindle Paperwhite",
                PaymentMethod.Debit, LocalDate.of(2026, 1, 30).toString(), 3000.00, Status.Success));
        transactions.add( new Transaction(9, "Standard Bank", "Mavuma Enterprise", "Donation Expense",
                PaymentMethod.ApplePay, LocalDate.of(2026, 2, 22).toString(), 2000000.00, Status.Pending));

        return transactions;
    }
}
