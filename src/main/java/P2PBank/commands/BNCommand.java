package P2PBank.commands;

import P2PBank.server.BankDatabase;

public class BNCommand extends Command {
    @Override
    public String execute(String[] args) {
        int clientCount = BankDatabase.getClientCount();
        return "BN " + clientCount;
    }
}
