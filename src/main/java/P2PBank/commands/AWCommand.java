package P2PBank.commands;

import P2PBank.server.BankDatabase;

public class AWCommand extends Command {
    @Override
    public String execute(String[] args) {
        if (args.length < 3) return "ER Chybí parametry.";

        try {
            int accountNumber = Integer.parseInt(args[1].split("/")[0]);
            long amount = Long.parseLong(args[2]);

            if (amount < 0) return "ER Částka musí být kladná.";

            if (BankDatabase.withdraw(accountNumber, amount)) {
                return "AW";
            } else {
                return "ER Není dostatek finančních prostředků nebo účet neexistuje.";
            }
        } catch (Exception e) {
            return "ER Formát čísla účtu nebo částky není správný.";
        }
    }
}
