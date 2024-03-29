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
                //
                validArgsNumber("init", 1, args);
                doWork.init();
                break;
            case "add":
                //
                validArgsNumber("add", 2, args);
                doWork.add(args[1]);
                break;
            case "commit":
                if (args.length < 2) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                } else {
                    doWork.commit(args[1]);
                }
                break;
            case "rm":
                validArgsNumber("rm", 2, args);
                doWork.rm(args[1]);
                break;
            case "log":
                validArgsNumber("log", 1, args);
                doWork.log();
                break;
            case "global-log":
                validArgsNumber("global-log", 1, args);
                doWork.global_log();
                break;
            case "find":
                validArgsNumber("find", 2, args);
                doWork.find(args[1]);
                break;
            case "status":
                validArgsNumber("status", 1, args);
                doWork.status();
                break;
            case "checkout":
                doWork.checkout(args);
                break;
            case "branch":
                //
                validArgsNumber("branch", 2, args);
                doWork.branch(args[1]);
                break;
            case "rm-branch":
                // java gitlet.Main rm-branch [branch name]
                validArgsNumber("rm-branch", 2, args);
                doWork.rmBranch(args[1]);
                break;
            case "reset":
                validArgsNumber("reset", 2, args);
                doWork.reset(args[1]);
                break;
            case "merge":
                validArgsNumber("merge", 2, args);
                doWork.merge(args[1]);
                break;

        }
    }

    static void validArgsNumber(String command, int expect, String[] args) {
        if (args.length != expect) {
            throw new RuntimeException(String.format("incorrect number of args for: %s", command));
        }
    }
}
