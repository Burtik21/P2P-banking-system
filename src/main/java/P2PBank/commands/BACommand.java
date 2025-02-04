package P2PBank.commands;

import P2PBank.server.BankDatabase;

public class BACommand extends Command {
    @Override
    public String execute(String[] args) {
        long totalAmount = BankDatabase.getTotalBalance();
        return "BA " + totalAmount;
    }
}
