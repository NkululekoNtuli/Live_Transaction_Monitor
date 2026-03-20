public record Transaction(int id, String payer, String payee, String description, PaymentMethod paymentMethod, String date, double amount, Status status) {}
