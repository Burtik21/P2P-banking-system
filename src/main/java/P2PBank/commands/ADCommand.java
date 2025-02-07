package P2PBank.commands;

import P2PBank.database.DatabaseManager;
import P2PBank.network.BankProxy;

public class ADCommand extends Command {
    @Override
    public String execute(String[] args) {
        if (args.length < 3) return "ER Chybí parametry.";

        try {
            String[] accountParts = args[1].split("/");
            if (accountParts.length != 2) return "ER Formát čísla účtu není správný.";

            int accountNumber = Integer.parseInt(accountParts[0]);
            String bankIp = accountParts[1];
            long amount = Long.parseLong(args[2]);

            if (amount < 0) return "ER Částka musí být kladná.";

            // ✅ Pokud je IP jiná než lokální, přesměrujeme přes proxy
            if (!bankIp.equals(DatabaseManager.getLocalIpAddress())) {
                return BankProxy.forwardCommand(bankIp, String.join(" ", args));
            }

            // ✅ Jinak provedeme vklad lokálně
            if (DatabaseManager.deposit(accountNumber, bankIp, amount)) {
                return "AD";
            } else {
                return "ER Účet neexistuje.";
            }
        } catch (Exception e) {
            return "ER Formát čísla účtu nebo částky není správný.";
        }
    }
}
