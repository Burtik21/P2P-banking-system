package P2PBank.commands;

import P2PBank.server.BankDatabase;

import java.util.Random;

public class ACCommand extends Command {
    @Override
    public String execute(String[] args) {
        int accountNumber = new Random().nextInt(90000) + 10000;
        if (BankDatabase.createAccount(accountNumber)) {
            return "AC " + accountNumber;
        }
        return "ER Naše banka nyní neumožňuje založení nového účtu.";
    }
}
