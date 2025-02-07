package P2PBank.commands;

import P2PBank.database.DatabaseManager;

public class BACommand extends Command {
    @Override
    public String execute(String[] args) {
        long totalAmount = DatabaseManager.getTotalBalance();
        return "BA " + totalAmount;
    }
}
