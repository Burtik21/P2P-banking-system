package P2PBank.commands;

import P2PBank.database.DatabaseManager;

public class BNCommand extends Command {
    @Override
    public String execute(String[] args) {
        int clientCount = DatabaseManager.getClientCount();
        return "BN " + clientCount;
    }
}
