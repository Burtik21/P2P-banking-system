package P2PBank.commands;

import P2PBank.database.DatabaseManager;

public class ARCommand extends Command {
    @Override
    public String execute(String[] args) {
        if (args.length < 2) return "ER Chybí číslo účtu.";

        try {
            String[] accountParts = args[1].split("/");
            if (accountParts.length != 2) return "ER Formát čísla účtu není správný.";

            int accountNumber = Integer.parseInt(accountParts[0]);
            String bankIp = accountParts[1];

            if (DatabaseManager.removeAccount(accountNumber, bankIp)) {
                return "AR";
            } else {
                return "ER Nelze smazat bankovní účet na kterém jsou finance.";
            }
        } catch (Exception e) {
            return "ER Formát čísla účtu není správný.";
        }
    }
}
