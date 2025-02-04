package P2PBank.commands;

import P2PBank.server.BankDatabase;

public class ARCommand extends Command {
    @Override
    public String execute(String[] args) {
        if (args.length < 2) return "ER Chybí číslo účtu.";

        try {
            int accountNumber = Integer.parseInt(args[1].split("/")[0]);

            if (BankDatabase.removeAccount(accountNumber)) {
                return "AR";
            } else {
                return "ER Nelze smazat bankovní účet na kterém jsou finance.";
            }
        } catch (Exception e) {
            return "ER Formát čísla účtu není správný.";
        }
    }
}
