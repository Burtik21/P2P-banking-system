package P2PBank.commands;

import P2PBank.server.BankDatabase;

public class ABCommand extends Command {
    @Override
    public String execute(String[] args) {
        if (args.length < 2) return "ER Chybí číslo účtu.";

        try {
            int accountNumber = Integer.parseInt(args[1].split("/")[0]);
            long balance = BankDatabase.getBalance(accountNumber);
            if (balance == -1) return "ER Účet neexistuje.";

            return "AB " + balance;
        } catch (Exception e) {
            return "ER Formát čísla účtu není správný.";
        }
    }
}
