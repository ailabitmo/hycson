package ru.ifmo.hymp.net;

public class NetworkException {
    public static class Timeout extends Base {
        public Timeout(String message) {
            super(message);
        }
    }

    public static class Forbidden extends Base {
        public Forbidden(String message) {
            super(message);
        }
    }

    public static class NotFound extends Base {
        public NotFound(String detailMessage) {
            super(detailMessage);
        }
    }

    public static class Gone extends Base {
        public Gone(String message) {
            super(message);
        }
    }

    public static class RateLimit extends Base {
        public RateLimit(String message) {
            super(message);
        }
    }

    public static class NotAuthorized extends Base {
        public NotAuthorized(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class BadRequest extends Base {
        public BadRequest(String message) {
            super(message);
        }
    }

    public static class Redirect extends Base {
        public Redirect(String message) {
            super(message);
        }
    }

    public static class NotAllowed extends Base {
        public NotAllowed(String message) {
            super(message);
        }
    }

    public static class Offline extends Base {
        Offline(String message) {
            super(message);
        }
    }

    public static class Unknown extends Base {
        public Unknown(String message) {
            super(message);
        }
    }

    public static class Base extends RuntimeException {
        public Base(String message) {
            super(message);
        }
    }
}