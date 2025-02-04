package P2PBank.commands;

import java.util.HashMap;

public class CommandHolder {
    private static final HashMap<String, Command> commands = new HashMap<>();

    static {
        commands.put("BC", new BCCommand());
        commands.put("AC", new ACCommand());
        commands.put("AD", new ADCommand());
        commands.put("AW", new AWCommand());
        commands.put("AB", new ABCommand());
        commands.put("AR", new ARCommand());
        commands.put("BA", new BACommand());
        commands.put("BN", new BNCommand());
    }

    public static Command getCommand(String command) {
        return commands.getOrDefault(command, null);
    }
}
