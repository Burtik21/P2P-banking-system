package P2PBank.commands;

import P2PBank.database.DatabaseManager;

import java.util.Random;

public class ACCommand extends Command {
    @Override
    public String execute(String[] args) {
        // ✅ Příkaz musí být přesně "AC", jinak vrátí chybu
        if (args.length != 1) return "ER Příkaz AC nemá žádné parametry.";

        try {
            String bankIp = DatabaseManager.getLocalIpAddress(); // 🔥 Získá IP adresu banky
            int accountNumber = generateUniqueAccountNumber();

            if (DatabaseManager.createAccount(accountNumber)) {
                return "AC " + accountNumber + "/" + bankIp;
            } else {
                return "ER Naše banka nyní neumožňuje založení nového účtu.";
            }
        } catch (Exception e) {
            return "ER Chyba při vytváření účtu.";
        }
    }

    // ✅ Generuje unikátní číslo účtu (10000 - 99999), které není v DB
    private int generateUniqueAccountNumber() {
        Random random = new Random();
        int accountNumber;

        do {
            accountNumber = random.nextInt(90000) + 10000; // Generuje číslo 10000 - 99999
        } while (DatabaseManager.accountExists(accountNumber));

        return accountNumber;
    }
}
