package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        DoWork doWork = new DoWork();

        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validArgsNumber("init", 1, args);
                doWork.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validArgsNumber("add", 2, args);
                doWork.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":

                break;
            case "rm":

                break;
            case "log":

                break;

            case "global-log":

                break;
            case "find":

                break;
            case "status":
                validArgsNumber("status", 1, args);
                doWork.status();
                break;
        }
    }

    static void validArgsNumber(String command, int expect, String[] args) {
        if (args.length != expect) {
            throw new RuntimeException(String.format("incorrect number of args for: %s", command));
        }
    }
}
