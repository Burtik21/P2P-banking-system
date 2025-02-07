package P2PBank.commands;

import P2PBank.database.DatabaseManager;
import P2PBank.network.BankProxy;

public class ABCommand extends Command {
    @Override
    public String execute(String[] args) {
        if (args.length < 2) return "ER Chybí číslo účtu.";

        try {
            String[] accountParts = args[1].split("/");
            if (accountParts.length != 2) return "ER Formát čísla účtu není správný.";

            int accountNumber = Integer.parseInt(accountParts[0]);
            String bankIp = accountParts[1];

            // ✅ Pokud je IP jiná než lokální, přesměrujeme přes proxy
            if (!bankIp.equals(DatabaseManager.getLocalIpAddress())) {
                return BankProxy.forwardCommand(bankIp, String.join(" ", args));
            }

            // ✅ Jinak zjistíme zůstatek lokálně
            long balance = DatabaseManager.getBalance(accountNumber, bankIp);
            if (balance == -1) return "ER Účet neexistuje.";

            return "AB " + balance;
        } catch (Exception e) {
            return "ER Formát čísla účtu není správný.";
        }
    }
}
