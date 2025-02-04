package P2PBank.commands;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class BCCommand extends Command {
    @Override
    public String execute(String[] args) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            return "BC " + ip;
        } catch (UnknownHostException e) {
            return "ER Nelze zjistit IP adresu.";
        }
    }
}
