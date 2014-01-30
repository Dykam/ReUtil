package nl.dykam.dev.reutil.commands.parsing;

class ExecuteResult {
    private boolean success;
    private String message;

    public ExecuteResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public String getMessage() {
        return message;
    }

    public static ExecuteResult esuccess() {
        return new ExecuteResult(true, null);
    }

    public static ExecuteResult efailure() {
        return efailure(null);
    }

    public static ExecuteResult efailure(String message) {
        return new ExecuteResult(false, message);
    }
}
