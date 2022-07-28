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
        if (args.length == 0) {
            System.out.println("You don't input command");
            System.exit(0);
        }

        String firstArg = args[0];
        DoWork doWork = new DoWork();

        switch(firstArg) {
            case "destroy":
                Repository.destroy();
                break;
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
                try {
                    validArgsNumber("commit", 2, args);
                } catch (Exception e) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                doWork.commit(args[1]);
                break;
            case "rm":
                validArgsNumber("rm", 2, args);
                doWork.rm(args[1]);
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
